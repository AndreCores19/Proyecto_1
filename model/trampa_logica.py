REARME_POR_DEFECTO = 300

class Trampa:
    #Aparece armada, al activarse queda desarmada y se rearma luego
    def __init__(self, id_instancia, nombre, dano, rearme=None, veneno=0):
        self.id_instancia = id_instancia
        self.nombre = nombre
        self.dano = dano or 0
        self.rearme = REARME_POR_DEFECTO if rearme is None else rearme
        self.veneno = veneno or 0        #aplica si tienen veneno (es un por si acaso)
        self.armada = True
        self.evento = None