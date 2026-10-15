from structures.vector_busqueda import VectorBusqueda
from structures.lista_doble import ListaDoble, Nodo

class CacheFichas:
    def __init__(self, capacidad):
        if capacidad < 1:
            raise ValueError("La capacidad debe ser al menos 1")
        self.capacidad = capacidad
        self.indice = VectorBusqueda()
        self.orden = ListaDoble()
        self.aciertos = 0
        self.fallos = 0

    def obtener(self, id_ficha):
        nodo = self.indice.buscar(id_ficha)
        if nodo is None:
            self.fallos += 1
            return None
        self.aciertos += 1
        self.orden.mover_al_frente(nodo)
        return nodo.ficha

    def contiene(self, id_ficha):
        return self.indice.buscar(id_ficha) is not None

    def _liberar(self):
        actual = self.orden.final
        while actual is not None and actual.en_uso > 0:
            actual = actual.anterior
        if actual is None:
            return False
        self.orden.quitar(actual)
        self.indice.eliminar(actual.id_ficha)
        return True

    def insertar(self, id_ficha, ficha):
        nodo = self.indice.buscar(id_ficha)
        if nodo is not None:
            nodo.ficha = ficha
            self.orden.mover_al_frente(nodo)
            return
        while self.orden.tamano >= self.capacidad:
            if not self._liberar():
                break
        nodo = Nodo(id_ficha, ficha)
        self.orden.agregar_al_frente(nodo)
        self.indice.insertar_ordenado(id_ficha, nodo)

    def adquirir(self, id_ficha):
        nodo = self.indice.buscar(id_ficha)
        if nodo is None:
            raise ValueError(f"La ficha {id_ficha} no está en la caché")
        nodo.en_uso += 1
        self.orden.mover_al_frente(nodo)
        return nodo.ficha

    def soltar(self, id_ficha):
        nodo = self.indice.buscar(id_ficha)
        if nodo is None:
            raise ValueError(f"La ficha {id_ficha} no está en la caché")
        if nodo.en_uso == 0:
            raise ValueError(f"La ficha {id_ficha} no está en uso")
        nodo.en_uso -= 1