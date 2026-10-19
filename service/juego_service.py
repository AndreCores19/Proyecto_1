from model.objeto_logica import PERGAMINO_RETROCESO


class JuegoService:

    def __init__(self, partida, fabrica):
        self.partida = partida
        self.fabrica = fabrica

    def recoger_objeto(self, id_instancia):
        resultado = False
        for objeto in self.partida.jugador.sala.objetos:
            if objeto.id_instancia == id_instancia:
                resultado = self.partida.recoger(objeto)
                break
        self.actualizar_fichas()
        return resultado

    def soltar_objeto(self):
        resultado = self.partida.soltar()
        self.actualizar_fichas()
        return resultado

    def equipar_objeto(self):
        return self.partida.equipar()

    def usar_objeto(self):
        resultado = self.partida.usar()
        self.actualizar_fichas()
        return resultado

    def mover(self, direccion):
        salida = self.partida.jugador.sala.salida(direccion)
        if salida is None or salida.cerrada:
            return False
        self.fabrica.poblar_sala(salida.destino)
        resultado = self.partida.mover(direccion)
        self.actualizar_fichas()
        return resultado

    def atacar(self, enemigo):
        resultado = self.partida.atacar(enemigo)
        self.actualizar_fichas()
        return resultado

    def esperar(self):
        resultado = self.partida.esperar_jugador()
        self.actualizar_fichas()
        return resultado

    def abrir_puerta(self, direccion):
        return self.partida.abrir_puerta(direccion)

    def retroceder(self):
        ## el comando retroceder usa un pergamino del inventario
        inv = self.partida.jugador.inventario
        nodo = inv.cabeza
        while nodo is not None and nodo.objeto.clase != PERGAMINO_RETROCESO:
            nodo = nodo.siguiente
        if nodo is None:
            return False
        inv.actual = nodo
        resultado = self.partida.usar_pergamino()
        self.actualizar_fichas()
        return resultado

    def siguiente_objeto(self):
        return self.partida.jugador.inventario.siguiente()

    def anterior_objeto(self):
        return self.partida.jugador.inventario.anterior()

    def _ids_en_uso(self):
        sala = self.partida.jugador.sala
        ids = []
        for enemigo in self.partida.enemigos:
            ids.append(enemigo.tipo)
        for enemigo in sala.inactivos:
            ids.append(enemigo.tipo)
        for objeto in sala.objetos:
            ids.append(objeto.id_catalogo)
        for objeto in self.partida.jugador.inventario.a_lista():
            ids.append(objeto.id_catalogo)
        for trampa in sala.trampas:
            ids.append(trampa.id_catalogo)
        return ids

    def actualizar_fichas(self):
        self.fabrica.marcar_en_uso(self._ids_en_uso())

    def vista_ordenada(self, criterio):
        return self.partida.jugador.inventario.vista_ordenada(criterio)