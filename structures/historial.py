from contextlib import contextmanager


class Historial:
    def __init__(self, capacidad=5):
        self.capacidad = capacidad
        self.cuadros = [None] * capacidad
        self.inicio = 0
        self.cantidad = 0
        self._actual = None
        self._deshaciendo = False

    def __len__(self):
        return self.cantidad

    #cuadros
    def abrir(self):
        cuadro = []
        if self.cantidad == self.capacidad:
            self.cuadros[self.inicio] = cuadro
            self.inicio = (self.inicio + 1) % self.capacidad
        else:
            self.cuadros[(self.inicio + self.cantidad) % self.capacidad] = cuadro
            self.cantidad += 1
        self._actual = cuadro

    def cerrar(self):
        self._actual = None

    @contextmanager
    def accion(self):
        self.abrir()
        try:
            yield
        finally:
            self.cerrar()

    #registro
    def _grabando(self):
        return self._actual is not None and not self._deshaciendo

    def registrar(self, deshacer):
        if self._grabando():
            self._actual.append(deshacer)

    def guardar(self, obj, campo):
        if self._grabando():
            anterior = getattr(obj, campo)
            self._actual.append(lambda: setattr(obj, campo, anterior))

    def asignar(self, obj, campo, valor):
        self.guardar(obj, campo)
        setattr(obj, campo, valor)

    def lista_agregar(self, lista, elemento):
        lista.append(elemento)
        self.registrar(lambda: lista.pop())

    def lista_quitar(self, lista, elemento):
        i = lista.index(elemento)
        del lista[i]
        self.registrar(lambda: lista.insert(i, elemento))

    #deshacer
    def deshacer(self):
        if self._actual is not None:
            raise RuntimeError("No se puede deshacer con una acción en curso")
        if self.cantidad == 0:
            return False
        pos = (self.inicio + self.cantidad - 1) % self.capacidad
        cuadro = self.cuadros[pos]
        self.cuadros[pos] = None
        self.cantidad -= 1
        self._deshaciendo = True
        try:
            for i in range(len(cuadro) - 1, -1, -1):
                cuadro[i]()
        finally:
            self._deshaciendo = False
        return True