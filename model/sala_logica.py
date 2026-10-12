class Salida:
    def __init__(self, direccion, destino, cerrada=False, llave=None, cierre_automatico=None):
        self.direccion = direccion
        self.destino = destino  
        self.cerrada = cerrada
        self.llave = llave                  
        self.cierre_automatico = cierre_automatico
        self.opuesta = None  #la Salida gemela

    def enlazar(self, otra):
        #soluciona la relación entre dos salidas
        #Une dos Salida como las dos caras de una misma puerta y si alguna viene
        #cerrada, las dos quedan cerradas
        self.opuesta = otra
        otra.opuesta = self
        cerrada = self.cerrada or otra.cerrada
        llave = self.llave if self.llave is not None else otra.llave
        cierre = (self.cierre_automatico if self.cierre_automatico is not None
                  else otra.cierre_automatico)
        for cara in (self, otra):
            cara.cerrada = cerrada
            cara.llave = llave
            cara.cierre_automatico = cierre


class Sala:
    def __init__(self, id_sala, nombre):
        self.id = id_sala
        self.nombre = nombre
        self.salidas = []        
        self.rastro = None       #último instante en que el jugador estuvo aquí
        self.inactivos = []     
        self.objetos = []        
        self.trampas = []       

    def agregar_salida(self, salida):
        self.salidas.append(salida)

    def salida(self, direccion):
        for s in self.salidas:
            if s.direccion == direccion:
                return s
        return None