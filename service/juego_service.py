class JuegoService:
    def __init__(self, partida):
        self.partida = partida

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