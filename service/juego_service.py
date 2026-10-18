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
        self._actualizar_fichas()
        return resultado

    def soltar_objeto(self):
        resultado = self.partida.soltar()
        self._actualizar_fichas()
        return resultado

    def equipar_objeto(self):
        return self.partida.equipar()

    def usar_objeto(self):
        resultado = self.partida.usar()
        self._actualizar_fichas()
        return resultado

    def mover(self, direccion):
        salida = self.partida.jugador.sala.salida(direccion)
        if salida is None or salida.cerrada:
            return False
        self.fabrica.poblar_sala(salida.destino)
        resultado = self.partida.mover(direccion)
        self._actualizar_fichas()
        return resultado

    def atacar(self, enemigo):
        resultado = self.partida.atacar(enemigo)
        self._actualizar_fichas()
        return resultado

    def esperar(self):
        resultado = self.partida.esperar_jugador()
        self._actualizar_fichas()
        return resultado

    def abrir_puerta(self, direccion):
        return self.partida.abrir_puerta(direccion)

    def retroceder(self):
        resultado = self.partida.retroceder()
        self._actualizar_fichas()
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

    def _actualizar_fichas(self):
        self.fabrica.marcar_en_uso(self._ids_en_uso())
