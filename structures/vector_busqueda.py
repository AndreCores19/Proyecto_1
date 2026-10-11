'''Vector de Busqueda Binaria entre los ids
de las salas y sus salas asociadas.'''

class VectorBusqueda:
    def __init__(self):
        self.ids = []
        self.valores = []
        self.comparaciones = 0


    def agregar(self, id_elemento, valor):
        self.ids.append(id_elemento)
        self.valores.append(valor)

    def preparar(self):
        for i in range(len(self.ids) - 1):
            if self.ids[i] > self.ids[i + 1]:
                raise ValueError("Los ids no están ordenados")

    def buscar(self, id_buscado): ## Busqueda con complejidad O(log n)
        bajo = 0
        alto = len(self.ids) - 1
        while bajo <= alto:
            medio = (bajo + alto) // 2
            self.comparaciones += 1
            if self.ids[medio] == id_buscado:
                return self.valores[medio]
            if self.ids[medio] < id_buscado:
                bajo = medio + 1
            else:
                alto = medio - 1
        return None