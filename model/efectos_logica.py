from model.tiempo_logica import nuevo_tiempo_por_velocidad

VENENO_INTERVALO = 80
VENENO_DURACION = 400
REGENERACION_INTERVALO = 200
EVENTOS_EFECTO = ("veneno", "fin_velocidad", "apagar_antorcha",
                  "regeneracion", "rearme_trampa")


class Efecto:
    def __init__(self, tipo, dueno, dano=0, fin=0, cambio_aplicado=0, cantidad=0):
        self.tipo = tipo
        self.dueno = dueno
        self.evento = None
        self.dano = dano            #daño por pulso
        self.fin = fin
        self.cambio_aplicado = cambio_aplicado          #velocidad por aplicar
        self.cantidad = cantidad    # regeneración: vida por pulso


class Efectos:
    def __init__(self, partida):
        self.p = partida

    #utilidades
    def _agregar(self, efecto):
        self.p.historial.lista_agregar(efecto.dueno.efectos, efecto)

    def _programar(self, tiempo, tipo, efecto):
        ev = self.p.agenda.programar(tiempo, tipo, efecto.dueno, efecto)
        self.p.historial.asignar(efecto, "evento", ev)

    def quitar(self, efecto):
        if efecto.evento is not None:
            self.p.agenda.cancelar(efecto.evento)
        for e in efecto.dueno.efectos:
            if e is efecto:
                self.p.historial.lista_quitar(efecto.dueno.efectos, efecto)
                return

    def cancelar_todos(self, actor):
        for efecto in list(actor.efectos):
            self.quitar(efecto)

    def _jugador_murio(self, actor):
        if actor is self.p.jugador and not actor.esta_vivo():
            self.p.terminada = True
            self.p.bitacora.agregar(f"t={self.p.reloj}: el jugador ha muerto.")
            return True
        return False

    #veneno
    def aplicar_veneno(self, actor, dano):
        previo = actor.efecto_activo("veneno")
        if previo is not None:
            self.quitar(previo)
        e = Efecto("veneno", actor, dano=dano, fin=self.p.reloj + VENENO_DURACION)
        self._agregar(e)
        self._programar(self.p.reloj + VENENO_INTERVALO, "veneno", e)
        self.p.bitacora.agregar(f"t={self.p.reloj}: {actor.nombre} es envenenado")

    def cancelar_veneno(self, actor):
        e = actor.efecto_activo("veneno")
        if e is None:
            return False
        self.quitar(e)
        return True

    def _pulso_veneno(self, ev):
        e, actor = ev.datos, ev.datos.dueno
        self.p.historial.guardar(actor, "vida")
        actor.recibir_dano(e.dano)
        self.p.bitacora.agregar(
            f"t={self.p.reloj}: el veneno causa {e.dano} de daño a {actor.nombre}. Vida: {actor.vida}")
        if self._jugador_murio(actor):
            return
        siguiente = self.p.reloj + VENENO_INTERVALO
        if siguiente <= e.fin:                 #dura 400 desde y se aplica con pulsos de 80
            self._programar(siguiente, "veneno", e)
        else:
            self.quitar(e)

    #velocidad
    def aplicar_velocidad(self, actor, modificador, duracion):
        antes = actor.velocidad
        despues = max(1, antes + modificador)
        e = Efecto("velocidad", actor, cambio_aplicado=despues - antes)
        self._agregar(e)
        self._cambiar_velocidad(actor, despues)
        self._programar(self.p.reloj + max(1, duracion), "fin_velocidad", e)
        self.p.bitacora.agregar(f"t={self.p.reloj}: velocidad de {actor.nombre}: {antes} -> {despues}")

    def _cambiar_velocidad(self, actor, nueva):
        vieja = actor.velocidad
        if nueva == vieja:
            return
        self.p.historial.asignar(actor, "velocidad", nueva)
        ev = actor.evento
        if ev is not None and ev.pos >= 0:     # ya tenía una acción programada (§2.3)
            nuevo = nuevo_tiempo_por_velocidad(ev.tiempo, self.p.reloj, vieja, nueva)
            self.p.agenda.reprogramar(ev, nuevo)

    def _fin_velocidad(self, ev):
        e, actor = ev.datos, ev.datos.dueno
        self._cambiar_velocidad(actor, max(1, actor.velocidad - e.cambio_aplicado))
        self.quitar(e)
        self.p.bitacora.agregar(f"t={self.p.reloj}: termina el efecto de velocidad de {actor.nombre}")

    #antorcha
    def encender_antorcha(self, actor, duracion):
        previa = actor.efecto_activo("antorcha")
        if previa is not None:
            self.quitar(previa)                #encenderla de nuevo reinicia la duración
        e = Efecto("antorcha", actor)
        self._agregar(e)
        self._programar(self.p.reloj + max(1, duracion), "apagar_antorcha", e)
        self.p.bitacora.agregar(f"t={self.p.reloj}: enciendes una antorcha")

    def _apagar_antorcha(self, ev):
        self.quitar(ev.datos)
        self.p.bitacora.agregar(f"t={self.p.reloj}: tu antorcha se apaga")

    #regeneración
    def iniciar_regeneracion(self, enemigo):
        cantidad = getattr(enemigo, "regeneracion", 0)
        if not cantidad:
            return
        e = Efecto("regeneracion", enemigo, cantidad=cantidad)
        self._agregar(e)
        self._programar(self.p.reloj + REGENERACION_INTERVALO, "regeneracion", e)

    def _pulso_regeneracion(self, ev):
        e, enemigo = ev.datos, ev.datos.dueno
        if enemigo.vida < enemigo.vida_max:
            self.p.historial.guardar(enemigo, "vida")
            enemigo.curar(e.cantidad)          # curar no puede superar vida_max
            self.p.bitacora.agregar(f"t={self.p.reloj}: {enemigo.nombre} se regenera")
        self._programar(self.p.reloj + REGENERACION_INTERVALO, "regeneracion", e)

    #trampas
    def activar_trampas(self, sala):
        #Se llama justo después de que el jugador completa un movimiento
        jugador = self.p.jugador
        for t in sala.trampas:
            if not t.armada:
                continue
            self.p.historial.guardar(jugador, "vida")
            jugador.recibir_dano(t.dano)
            self.p.bitacora.agregar(
                f"t={self.p.reloj}: {t.nombre} te causa {t.dano} de daño. Vida: {jugador.vida}")
            self.p.historial.asignar(t, "armada", False)
            ev = self.p.agenda.programar(self.p.reloj + t.rearme, "rearme_trampa", None, t)
            self.p.historial.asignar(t, "evento", ev)
            if self._jugador_murio(jugador):
                return True                       #Devuelve True si el jugador murió en una trampa
            if t.veneno:
                self.aplicar_veneno(jugador, t.veneno)
        return False

    def _rearmar(self, trampa):
        self.p.historial.asignar(trampa, "armada", True)

    def maneja(self, tipo):
        return tipo in EVENTOS_EFECTO

    def ejecutar(self, ev):
        t = ev.tipo
        if t == "veneno":
            self._pulso_veneno(ev)
        elif t == "fin_velocidad":
            self._fin_velocidad(ev)
        elif t == "apagar_antorcha":
            self._apagar_antorcha(ev)
        elif t == "regeneracion":
            self._pulso_regeneracion(ev)
        elif t == "rearme_trampa":
            self._rearmar(ev.datos)