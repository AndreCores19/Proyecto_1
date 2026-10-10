import random
from structures.agenda import AgendaEventos
from structures.bitacora import Bitacora
from model.tiempo_logica import (intervalo, COSTO_ESPERAR, COSTO_ATACAR, COSTO_MOVER, COSTO_ABRIR)

RASTRO_VIGENCIA = 400

class Partida:
    def __init__(self, jugador, semilla, sala_salida=None):
        self.reloj = 0
        self.agenda = AgendaEventos()
        self.azar = random.Random(semilla)
        self.jugador = jugador
        self.enemigos = []
        self.enemigos_derrotados = 0
        self.acciones_jugador = 0
        self.terminada = False
        self.victoria = False
        self.sala_salida = sala_salida
        self.abrio_puerta_salida = False
        self.bitacora = Bitacora()
        jugador.evento = self.agenda.programar(0, "accion", jugador)
        self._entrar_en_sala(jugador.sala)
        self.avanzar_hasta_jugador()
    #salas
    def _entrar_en_sala(self, sala):
        sala.rastro = self.reloj
        for e in sala.inactivos:
            self.activar_enemigo(e)
        sala.inactivos = []

    def _refrescar_rastro(self):
        self.jugador.sala.rastro = self.reloj

    def activar_enemigo(self, enemigo):
        self.enemigos.append(enemigo)
        self._programar_siguiente(enemigo, COSTO_ESPERAR)

    #metodos de acciones
    def mover(self, direccion):
        if self.terminada:
            return False
        salida = self.jugador.sala.salida(direccion)
        if salida is None or salida.cerrada:
            return False
        self._refrescar_rastro()
        self.acciones_jugador += 1
        self.jugador.sala = salida.destino
        self._entrar_en_sala(salida.destino)
        if self._hay_victoria():
            self.terminada = True
            self.victoria = True
            self.bitacora.agregar(f"t={self.reloj}: el jugador llega a la salida. Victoria!!")
            return True
        self._programar_siguiente(self.jugador, COSTO_MOVER)
        self.avanzar_hasta_jugador()
        return True

    def abrir_puerta(self, direccion):
        if self.terminada:
            return False
        salida = self.jugador.sala.salida(direccion)
        if salida is None or not salida.cerrada:
            return False
        if not self.jugador.tiene_llave(salida.llave):
            return False                         #si no tiene la llave no consume tiempo pero si la tiene le cuesta 50
        self._refrescar_rastro()
        self.acciones_jugador += 1
        salida.cerrada = False
        if self.sala_salida is not None and salida.destino is self.sala_salida:
            self.abrio_puerta_salida = True
        self.bitacora.agregar(f"t={self.reloj}: se abre la puerta hacia {direccion}")
        if salida.cierre_automatico is not None:
            self.agenda.programar(self.reloj + salida.cierre_automatico,
                                  "cierre_puerta", None, salida)
        self._programar_siguiente(self.jugador, COSTO_ABRIR)
        self.avanzar_hasta_jugador()
        return True

    def atacar(self, enemigo):
        if self.terminada or enemigo.sala != self.jugador.sala:
            return False
        self._refrescar_rastro()
        self.acciones_jugador += 1
        dano = self._calcular_dano(self.jugador, enemigo)
        enemigo.recibir_dano(dano)
        self.bitacora.agregar(
            f"t={self.reloj}: Jugador ataca a {enemigo.nombre}. "
            f"Daño {dano}. Vida enemigo: {enemigo.vida}"
        )
        if not enemigo.esta_vivo():
            self.agenda.cancelar(enemigo.evento)
            self.enemigos.remove(enemigo)
            self.enemigos_derrotados += 1
            self.bitacora.agregar(f"t={self.reloj}: {enemigo.nombre} muere")
        self._programar_siguiente(self.jugador, COSTO_ATACAR)
        self.avanzar_hasta_jugador()
        return True

    def esperar_jugador(self):
        if self.terminada:
            return False
        self._refrescar_rastro()
        self.acciones_jugador += 1
        self._programar_siguiente(self.jugador, COSTO_ESPERAR)
        self.avanzar_hasta_jugador()
        return True

    #reloj
    def _programar_siguiente(self, actor, costo):
        tiempo = self.reloj + intervalo(costo, actor.velocidad)
        actor.evento = self.agenda.programar(tiempo, "accion", actor)

    def _calcular_dano(self, atacante, defensor):
        return max(1, atacante.ataque + self.azar.randint(0, 4) - defensor.defensa)

    def _hay_victoria(self):
        return (self.sala_salida is not None
                and self.jugador.sala is self.sala_salida
                and self.abrio_puerta_salida)

    def avanzar_hasta_jugador(self):
        #Ejecutara eventos hasta que le toque decidir al jugador o termine la partida
        if not self.terminada and self.jugador.evento.pos < 0:
            # Sin acción pendiente del jugador el bucle nunca terminaría
            raise RuntimeError("El jugador no tiene una acción programada")
        while not self.terminada:
            ev = self.agenda.extraer()
            if ev is None:
                break
            self.reloj = ev.tiempo
            if ev.tipo == "accion":
                if ev.dueno is self.jugador:
                    return
                self._actuar_enemigo(ev.dueno)
            elif ev.tipo == "cierre_puerta":
                self._cerrar_puerta(ev.datos)
            else:
                raise ValueError(f"Tipo de evento sin manejador: {ev.tipo}")

    def _cerrar_puerta(self, salida):
        salida.cerrada = True
        self.bitacora.agregar(f"t={self.reloj}: una puerta se cierra automáticamente")

    #enemigos
    def _actuar_enemigo(self, enemigo):
        if enemigo.sala is self.jugador.sala:
            dano = self._calcular_dano(enemigo, self.jugador)
            self.jugador.recibir_dano(dano)
            self.bitacora.agregar(
                f"t={self.reloj}: {enemigo.nombre} ataca. Daño {dano}. "
                f"Vida jugador: {self.jugador.vida}"
            )
            if not self.jugador.esta_vivo():
                self.terminada = True
                self.bitacora.agregar(f"t={self.reloj}: el jugador ha muerto.")
                return
            self._programar_siguiente(enemigo, COSTO_ATACAR)
            return

        salida = None
        if enemigo.comportamiento == "errante":
            salida = self._decidir_errante(enemigo)
        elif enemigo.comportamiento == "rastreador":
            salida = self._decidir_rastreador(enemigo)
        if salida is not None:
            enemigo.sala = salida.destino
            self.bitacora.agregar(
                f"t={self.reloj}: {enemigo.nombre} se mueve hacia {salida.direccion}")
        else:
            self.bitacora.agregar(f"t={self.reloj}: {enemigo.nombre} espera")
        # Atacar, moverse y esperar cuestan 100
        self._programar_siguiente(enemigo, COSTO_ESPERAR)

    def _salidas_abiertas(self, sala):
        return [s for s in sala.salidas if not s.cerrada]

    def _decidir_errante(self, enemigo):
        abiertas = self._salidas_abiertas(enemigo.sala)
        if not abiertas:
            return None
        return self.azar.choice(abiertas)

    def _decidir_rastreador(self, enemigo):
        # Costo proporcional al número de salidas de su sala (§4.7).
        # El rastro caducado se detecta al consultarlo, sin limpieza activa.
        mejor = None
        mejor_rastro = -1
        mejor_id = -1
        for s in self._salidas_abiertas(enemigo.sala):
            rastro = s.destino.rastro
            if rastro is None or self.reloj - rastro >= RASTRO_VIGENCIA:
                continue
            id_vecina = s.destino.id
            if (mejor is None
                    or rastro > mejor_rastro
                    or (rastro == mejor_rastro and id_vecina < mejor_id)):
                mejor, mejor_rastro, mejor_id = s, rastro, id_vecina
        return mejor