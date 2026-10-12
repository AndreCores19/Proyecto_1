from model.enemigo_logica import Enemigo
from model.jugador_logica import Jugador
from model.partida_logica import Partida
from model.sala_logica import Sala, Salida


def jugar(semilla, turnos=60):
    salas = [Sala(i + 1, f"Sala {i + 1}") for i in range(5)]
    for i in range(4):
        ida = Salida("N", salas[i + 1])
        vuelta = Salida("S", salas[i])
        salas[i].agregar_salida(ida)
        salas[i + 1].agregar_salida(vuelta)
        ida.enlazar(vuelta)
    for k in range(3):
        salas[1 + k].inactivos.append(
            Enemigo(f"e{k}", "x", f"Enemigo {k}", 20, 20, 3, 0, 100, "errante", salas[1 + k]))
    jugador = Jugador(vida_max=10**6, ataque=8, defensa=1, velocidad=100, sala=salas[0])
    partida = Partida(jugador, semilla=semilla)
    for sala in salas:                   # activa a todos, para que se muevan por el mapa
        for e in list(sala.inactivos):
            partida.activar_enemigo(e)
        sala.inactivos = []
    recorrido = []
    for _ in range(turnos):
        partida.esperar_jugador()
        recorrido.append([e.sala.id for e in partida.enemigos])
    return recorrido, jugador.vida


def test_misma_semilla_misma_partida():
    assert jugar(42) == jugar(42)


def test_distinta_semilla_distinta_partida():
    assert jugar(1) != jugar(2)


def test_sin_semilla_se_genera_y_se_guarda():
    jugador = Jugador(vida_max=100, ataque=8, defensa=1, velocidad=100, sala=Sala(1, "A"))
    partida = Partida(jugador)
    assert isinstance(partida.semilla, int)
    # Repetir con esa semilla reproduce la partida: es lo que permite saber qué se usó
    otra = Partida(Jugador(vida_max=100, ataque=8, defensa=1, velocidad=100, sala=Sala(1, "A")),
                   semilla=partida.semilla)
    assert otra.semilla == partida.semilla
    assert otra.azar.getstate() == partida.azar.getstate()