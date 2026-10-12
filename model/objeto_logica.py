PERGAMINO_RETROCESO = "pergamino_retroceso"


class Objeto:
    def __init__(self, id_instancia, id_catalogo, nombre, clase, peso, valor, ataque_bonus=None, defensa_bonus=None, cura=None, modificador_velocidad=None, duracion=None, abre=None):
        self.id_instancia = id_instancia
        self.id_catalogo = id_catalogo
        self.nombre = nombre
        self.clase = clase
        self.peso = peso
        self.valor = valor
        self.equipado = False
        self.ataque_bonus = ataque_bonus or 0
        self.defensa_bonus = defensa_bonus or 0
        self.cura = cura or 0
        self.modificador_velocidad = modificador_velocidad or 0
        self.duracion = duracion or 0
        self.abre = abre or ""