import random

from model.enemigo_logica import Enemigo
from model.jugador_logica import Jugador
from model.objeto_logica import Objeto
from model.partida_logica import Partida
from model.sala_logica import Sala, Salida
from model.trampa_logica import Trampa
from structures.ordenamientos import ordenar

PARTIDAS = 150
PASOS = 80
HISTORIAL = 5


# ------------------------------------------------------------------ el mundo
def armar_mundo(azar):
    """Un mapa pequeño con puertas, objetos, trampas y enemigos"""
    salas = [Sala(i + 1, f"Sala {i + 1}") for i in range(5)]
    for i in range(4):
        ida = Salida("N", salas[i + 1], cerrada=azar.random() < 0.3, llave="llave",
                     cierre_automatico=azar.choice((None, 150)))
        vuelta = Salida("S", salas[i])
        salas[i].agregar_salida(ida)
        salas[i + 1].agregar_salida(vuelta)
        ida.enlazar(vuelta)

    plantillas = (
        ("arma", dict(ataque_bonus=3)), ("armadura", dict(defensa_bonus=2)),
        ("pocion", dict(cura=10, modificador_velocidad=40, duracion=200)),
        ("pocion", dict(modificador_velocidad=-30, duracion=150)),
        ("antidoto", {}), ("antorcha", dict(duracion=300)), ("llave", {}),
    )
    numero = 0
    for sala in salas:
        for _ in range(azar.randint(0, 3)):
            clase, extra = azar.choice(plantillas)
            numero += 1
            sala.objetos.append(Objeto(numero, clase, f"{clase} {numero}", clase,
                                       azar.randint(1, 9), azar.randint(1, 50), **extra))
    for sala in salas[1:]:
        if azar.random() < 0.6:
            sala.trampas.append(Trampa(f"t{sala.id}", "Trampa", dano=azar.randint(1, 5),
                                       rearme=azar.randint(100, 300), veneno=azar.choice((0, 2))))
    numero = 0
    for sala in salas[1:]:
        for _ in range(azar.randint(0, 2)):
            numero += 1
            e = Enemigo(f"e{numero}", "x", f"Enemigo {numero}", 20, 20, 3, 0,
                        azar.randint(60, 140), azar.choice(("errante", "rastreador")), sala)
            e.regeneracion = azar.choice((0, 2))
            sala.inactivos.append(e)

    jugador = Jugador(vida_max=300, ataque=8, defensa=1, velocidad=100, sala=salas[0])
    partida = Partida(jugador, azar.randint(0, 10**6), sala_salida=salas[-1])
    return partida, salas


# ------------------------------------------------------------------ la foto
def foto_evento(ev):
    if ev is None or ev.pos < 0:
        return None
    return (ev.tiempo, ev.num_sec)


def foto_efectos(actor):
    return [(e.tipo, e.dano, e.fin, e.cambio_aplicado, e.cantidad, foto_evento(e.evento))
            for e in actor.efectos]


def foto_actor(a):
    return (a.vida, a.ataque, a.defensa, a.velocidad, a.sala.id,
            foto_evento(a.evento), foto_efectos(a))


def nombre_dueno(ev, partida):
    if ev.dueno is None:
        return None
    return "jugador" if ev.dueno is partida.jugador else ev.dueno.id_enemigo


def foto(partida, salas):
    inv = partida.jugador.inventario
    agenda = ordenar(partida.agenda.heap, lambda ev: (ev.tiempo, ev.num_sec))
    return (
        partida.reloj, partida.agenda.num_sec, partida.azar.getstate(),
        partida.acciones_jugador, partida.enemigos_derrotados,
        partida.terminada, partida.victoria, partida.abrio_puerta_salida,
        foto_actor(partida.jugador),
        [(o.id_instancia, o.equipado) for o in inv.a_lista()],
        inv.arma_equipada and inv.arma_equipada.id_instancia,
        inv.armadura_equipada and inv.armadura_equipada.id_instancia,
        [(e.id_enemigo, foto_actor(e)) for e in partida.enemigos],
        [(ev.tiempo, ev.num_sec, ev.tipo, nombre_dueno(ev, partida), id(ev.datos))
         for ev in agenda],
        [(s.id, s.rastro,
          [o.id_instancia for o in s.objetos],
          [e.id_enemigo for e in s.inactivos],
          [(t.armada, foto_evento(t.evento)) for t in s.trampas],
          [(sal.direccion, sal.cerrada) for sal in s.salidas]) for s in salas],
    )


# ------------------------------------------------------------- acciones al azar
def accion_al_azar(partida, azar):
    """Hace una acción cualquiera. Devuelve (nombre, si_cambio_el_juego)."""
    sala = partida.jugador.sala
    inv = partida.jugador.inventario
    opcion = azar.choice(("mover", "mover", "esperar", "esperar", "abrir", "atacar",
                          "recoger", "soltar", "equipar", "usar", "cursor", "retroceder",
                          "retroceder"))
    if opcion == "mover" and sala.salidas:
        return opcion, partida.mover(azar.choice(sala.salidas).direccion)
    if opcion == "abrir" and sala.salidas:
        return opcion, partida.abrir_puerta(azar.choice(sala.salidas).direccion)
    if opcion == "atacar":
        presentes = [e for e in partida.enemigos if e.sala is sala]
        if presentes:
            return opcion, partida.atacar(azar.choice(presentes))
    if opcion == "recoger" and sala.objetos:
        return opcion, partida.recoger(azar.choice(sala.objetos))
    if opcion == "soltar":
        return opcion, partida.soltar()
    if opcion == "equipar":
        return opcion, partida.equipar()
    if opcion == "usar":
        return opcion, partida.usar()
    if opcion == "cursor":
        (inv.siguiente if azar.random() < 0.5 else inv.anterior)()
        return opcion, False
    if opcion == "retroceder":
        return opcion, partida.retroceder()
    return "esperar", partida.esperar_jugador()


# ------------------------------------------------------------------ la prueba
def test_retroceder_restaura_exactamente_el_estado_anterior():
    verificados = 0
    for semilla in range(PARTIDAS):
        azar = random.Random(semilla)
        partida, salas = armar_mundo(azar)
        pasadas = []          # fotos previas de las últimas acciones (igual que el historial)
        for _ in range(PASOS):
            if partida.terminada:
                break
            antes = foto(partida, salas)
            nombre, hecho = accion_al_azar(partida, azar)
            if not hecho:
                continue
            if nombre == "retroceder":
                assert foto(partida, salas) == pasadas.pop(), f"partida {semilla}"
                verificados += 1
            else:
                pasadas.append(antes)
                if len(pasadas) > HISTORIAL:
                    del pasadas[0]
    assert verificados > 500        # la prueba de verdad ejercitó muchos retrocesos