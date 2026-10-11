from model.entidad_logica import Entidad
from model.inventario_logica import Inventario


class Jugador(Entidad):
    def __init__(self, vida_max, ataque, defensa, velocidad, sala, inventario_max=10):
        super().__init__("Jugador", vida_max, vida_max, ataque, defensa, velocidad, sala)
        self.inventario = Inventario(inventario_max)

    def tiene_llave(self, id_llave):
        return self.inventario.tiene_llave(id_llave)
