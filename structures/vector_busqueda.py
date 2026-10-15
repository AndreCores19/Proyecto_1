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
        pos = self.posicion(id_buscado)
        if pos == -1:
            return None
        return self.valores[pos]

    def posicion(self, id_buscado):
        bajo = 0
        alto = len(self.ids) - 1
        while bajo <= alto:
            medio = (bajo + alto) // 2
            self.comparaciones += 1
            if self.ids[medio] == id_buscado:
                return medio
            if self.ids[medio] < id_buscado:
                bajo = medio + 1
            else:
                alto = medio - 1
        return -1

    def tamano(self):
        return len(self.ids)

    def insertar_ordenado(self, id_elemento, valor): ## O(n): la búsqueda es O(log n), pero desplazar los elementos es O(n)
        bajo = 0
        alto = len(self.ids) - 1
        while bajo <= alto:
            medio = (bajo + alto) // 2
            self.comparaciones += 1
            if self.ids[medio] == id_elemento:
                raise ValueError("El id ya existe")
            if self.ids[medio] < id_elemento:
                bajo = medio + 1
            else:
                alto = medio - 1

        self.ids.append(id_elemento)
        self.valores.append(valor)
        i = len(self.ids) - 1
        while i > bajo:
            self.ids[i] = self.ids[i - 1]
            self.valores[i] = self.valores[i - 1]
            i -= 1
        self.ids[bajo] = id_elemento
        self.valores[bajo] = valor

    def eliminar(self, id_elemento): ## O(n) por el desplazamiento. Lanza ValueError si el id no existe.
        pos = self.posicion(id_elemento)
        if pos == -1:
            raise ValueError("El id no existe")
        while pos < len(self.ids) - 1:
            self.ids[pos] = self.ids[pos + 1]
            self.valores[pos] = self.valores[pos + 1]
            pos += 1
        self.ids.pop()
        self.valores.pop()