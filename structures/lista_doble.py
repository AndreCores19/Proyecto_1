
class Nodo:
    def __init__(self, id_ficha, ficha):
        self.id_ficha = id_ficha
        self.ficha = ficha
        self.en_uso = 0
        self.siguiente = None
        self.anterior = None 



class ListaDoble:
    def __init__(self):
        self.frente = None
        self.final = None
        self.tamano = 0

    def esta_vacia(self):
        return self.tamano == 0

    def agregar_al_frente(self, nodo):
        nodo.anterior = None
        nodo.siguiente = self.frente
        if self.frente is not None:
            self.frente.anterior = nodo
        else:
            self.final = nodo
        self.frente = nodo
        self.tamano += 1

    def quitar(self, nodo):
        if nodo.anterior is not None:
            nodo.anterior.siguiente = nodo.siguiente
        else:
            self.frente = nodo.siguiente

        if nodo.siguiente is not None:
            nodo.siguiente.anterior = nodo.anterior
        else:
            self.final = nodo.anterior

        nodo.anterior = None
        nodo.siguiente = None
        self.tamano -= 1

    def mover_al_frente(self, nodo):
        if nodo is self.frente:
            return
        self.quitar(nodo)
        self.agregar_al_frente(nodo)