class JuegoService:

    def __init__(self, partida, fabrica):
        self.partida = partida
        self.fabrica = fabrica

    def recoger_objeto(self, id_instancia):
        for objeto in self.partida.jugador.sala.objetos:
            if objeto.id_instancia == id_instancia:
                return self.partida.recoger(objeto)
        return False

    def soltar_objeto(self):
        return self.partida.soltar()

    def equipar_objeto(self):
        return self.partida.equipar()

    def usar_objeto(self):
        return self.partida.usar()

    def mover(self, direccion):
        salida = self.partida.jugador.sala.salida(direccion)
        if salida is None or salida.cerrada:
            return False
        self.fabrica.poblar_sala(salida.destino)
        return self.partida.mover(direccion)

    def atacar_enemigo(self, enemigo):
        return self.partida.atacar(enemigo)

    def esperar(self):
        return self.partida.esperar_jugador()

    def abrir_puerta(self, direccion):
        return self.partida.abrir_puerta(direccion)

    def retroceder(self):
        return self.partida.retroceder()