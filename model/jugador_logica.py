from model.entidad_logica import Entidad

class Jugador(Entidad):
    def __init__(self, vida_max, ataque, defensa, velocidad, sala):
        super().__init__("Jugador", vida_max, vida_max, ataque, defensa, velocidad, sala)
        self.llaves = set()

    def tiene_llave(self, id_llave):
        return id_llave in self.llaves

    #Partida solo usa tiene_llave(), así el cambio queda aquí