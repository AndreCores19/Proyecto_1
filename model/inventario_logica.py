from model.objeto_logica import PERGAMINO_RETROCESO


class NodoInventario:
    def __init__(self, objeto):
        self.objeto = objeto
        self.anterior = None
        self.siguiente = None
        self.en_lista = False


class Inventario:

    def __init__(self, capacidad_max, historial=None):
        self.capacidad_max = capacidad_max
        self.historial = historial
        self.cabeza = None
        self.cola = None
        self.actual = None
        self.tamano = 0
        self.arma_equipada = None
        self.armadura_equipada = None

    def esta_lleno(self):
        return self.tamano >= self.capacidad_max
    #enlace sin registro
    def _enlazar_antes(self, nodo, ref):
        if ref is None:
            nodo.anterior = self.cola
            nodo.siguiente = None
            if self.cola is not None:
                self.cola.siguiente = nodo
            else:
                self.cabeza = nodo
            self.cola = nodo
        else:
            nodo.siguiente = ref
            nodo.anterior = ref.anterior
            if ref.anterior is not None:
                ref.anterior.siguiente = nodo
            else:
                self.cabeza = nodo
            ref.anterior = nodo
        nodo.en_lista = True
        self.tamano += 1
        if self.actual is None:
            self.actual = nodo

    def _desenlazar(self, nodo):
        if self.actual is nodo:
            self.actual = nodo.siguiente if nodo.siguiente is not None else nodo.anterior
        if nodo.anterior is not None:
            nodo.anterior.siguiente = nodo.siguiente
        else:
            self.cabeza = nodo.siguiente
        if nodo.siguiente is not None:
            nodo.siguiente.anterior = nodo.anterior
        else:
            self.cola = nodo.anterior
        nodo.anterior = None
        nodo.siguiente = None
        nodo.en_lista = False
        self.tamano -= 1

    def _reinsertar(self, nodo, siguiente, anterior):
        if nodo.en_lista:
            return
        if siguiente is not None and siguiente.en_lista:
            self._enlazar_antes(nodo, siguiente)
        elif anterior is not None and anterior.en_lista:
            self._enlazar_antes(nodo, anterior.siguiente)
        else:
            self._enlazar_antes(nodo, None)
        self.actual = nodo

    def _grabar(self, objeto, deshacer):
        if self.historial is not None and objeto.clase != PERGAMINO_RETROCESO:
            self.historial.registrar(deshacer)

    def _asignar(self, obj, campo, valor):
        if self.historial is not None:
            self.historial.asignar(obj, campo, valor)
        else:
            setattr(obj, campo, valor)

    #metodos
    def agregar(self, objeto):
        if self.esta_lleno():
            return None
        nodo = NodoInventario(objeto)
        self._enlazar_antes(nodo, None)
        self._grabar(objeto, lambda: self._desenlazar(nodo) if nodo.en_lista else None)
        return nodo

    def quitar_nodo(self, nodo):
        siguiente, anterior = nodo.siguiente, nodo.anterior
        self._desenlazar(nodo)
        self._grabar(nodo.objeto, lambda: self._reinsertar(nodo, siguiente, anterior))
        return nodo.objeto

    def quitar_actual(self):
        if self.actual is None:
            return None
        return self.quitar_nodo(self.actual)

    def mover_al_frente(self, nodo):
        if self.cabeza is nodo:
            return
        siguiente, anterior = nodo.siguiente, nodo.anterior
        self._desenlazar(nodo)
        self._enlazar_antes(nodo, self.cabeza)
        self.actual = nodo

        def deshacer():
            if nodo.en_lista:
                self._desenlazar(nodo)
            self._reinsertar(nodo, siguiente, anterior)
        self._grabar(nodo.objeto, deshacer)

    #equipo
    def puede_equipar_actual(self):
        if self.actual is None:
            return False
        o = self.actual.objeto
        return o.clase in ("arma", "armadura") and not o.equipado

    def equipar_actual(self, jugador):
        if not self.puede_equipar_actual():
            return False
        nodo = self.actual
        objeto = nodo.objeto
        self.mover_al_frente(nodo)
        if objeto.clase == "arma":
            if self.arma_equipada is not None:
                self.desequipar(self.arma_equipada, jugador)
            self._asignar(objeto, "equipado", True)
            self._asignar(self, "arma_equipada", objeto)
            self._asignar(jugador, "ataque", jugador.ataque + objeto.ataque_bonus)
        else:
            if self.armadura_equipada is not None:
                self.desequipar(self.armadura_equipada, jugador)
            self._asignar(objeto, "equipado", True)
            self._asignar(self, "armadura_equipada", objeto)
            self._asignar(jugador, "defensa", jugador.defensa + objeto.defensa_bonus)
        return True

    def desequipar(self, objeto, jugador):
        if not objeto.equipado:
            return
        self._asignar(objeto, "equipado", False)
        if objeto.clase == "arma" and self.arma_equipada is objeto:
            self._asignar(jugador, "ataque", jugador.ataque - objeto.ataque_bonus)
            self._asignar(self, "arma_equipada", None)
        elif objeto.clase == "armadura" and self.armadura_equipada is objeto:
            self._asignar(jugador, "defensa", jugador.defensa - objeto.defensa_bonus)
            self._asignar(self, "armadura_equipada", None)

    #cositas de vistas
    def siguiente(self):
        if self.actual is not None and self.actual.siguiente is not None:
            self.actual = self.actual.siguiente
        return self.actual.objeto if self.actual else None

    def anterior(self):
        if self.actual is not None and self.actual.anterior is not None:
            self.actual = self.actual.anterior
        return self.actual.objeto if self.actual else None

    def obtener_actual(self):
        return self.actual.objeto if self.actual else None

    def a_lista(self):
        resultado = []
        c = self.cabeza
        while c is not None:
            resultado.append(c.objeto)
            c = c.siguiente
        return resultado

    def nodo_de(self, objeto):
        c = self.cabeza
        while c is not None:
            if c.objeto is objeto:
                return c
            c = c.siguiente
        return None

    def tiene_llave(self, id_llave):
        c = self.cabeza
        while c is not None:
            if c.objeto.clase == "llave" and c.objeto.id_catalogo == id_llave:
                return True
            c = c.siguiente
        return False