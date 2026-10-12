class Cola:
    def __init__(self):
        self.head = 0
        self.cola = []

    def encolar(self, item):
        self.cola.append(item)

    def desencolar(self):
        if self.head < len(self.cola):
            item = self.cola[self.head]
            self.head += 1
            return item
        else:
            raise IndexError("La cola está vacía")

    def esta_vacia(self):
        return self.head >= len(self.cola)