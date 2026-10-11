PERGAMINO_RETROCESO = "pergamino_retroceso"


class Objeto:
    def __init__(self, id_instancia, id_catalogo, nombre, clase, peso, valor, **kwargs):
        self.id_instancia = id_instancia
        self.id_catalogo = id_catalogo
        self.nombre = nombre
        self.clase = clase
        self.peso = peso
        self.valor = valor
        self.equipado = False
        self.ataque_bonus = kwargs.get("ataque_bonus") or 0
        self.defensa_bonus = kwargs.get("defensa_bonus") or 0
        self.cura = kwargs.get("cura") or 0
        self.modificador_velocidad = kwargs.get("modificador_velocidad") or 0
        self.duracion = kwargs.get("duracion") or 0
        self.abre = kwargs.get("abre") or ""

    def __repr__(self):
        eq = " [Equipado]" if self.equipado else ""
        return f"<{self.nombre} ({self.clase}){eq}>"