
from structures.lista_doble import Nodo, ListaDoble


def adelante(lista):
    """Ids recorriendo desde el frente con 'siguiente'."""
    ids = []
    actual = lista.frente
    while actual is not None:
        ids.append(actual.id_ficha)
        actual = actual.siguiente
    return ids


def atras(lista):
    """Ids recorriendo desde el final con 'anterior'."""
    ids = []
    actual = lista.final
    while actual is not None:
        ids.append(actual.id_ficha)
        actual = actual.anterior
    return ids


def verificar(nombre, lista, esperado):
    """Comprueba el orden, el recorrido inverso, el tamaño y los extremos."""
    assert adelante(lista) == esperado, f"{nombre}: adelante {adelante(lista)} != {esperado}"
    assert atras(lista) == esperado[::-1], f"{nombre}: atrás {atras(lista)} != {esperado[::-1]}"
    assert lista.tamano == len(esperado), f"{nombre}: tamaño {lista.tamano} != {len(esperado)}"
    if esperado:
        assert lista.frente.anterior is None, f"{nombre}: el frente tiene 'anterior'"
        assert lista.final.siguiente is None, f"{nombre}: el final tiene 'siguiente'"
    else:
        assert lista.frente is None and lista.final is None, f"{nombre}: debería estar vacía"
    print("OK  ", nombre, esperado)


def armar(*ids):
    """Agrega los ids al frente en el orden dado. Devuelve la lista y los nodos (en ese orden)."""
    lista = ListaDoble()
    nodos = []
    for i in ids:
        nodo = Nodo(i, "ficha " + i)
        lista.agregar_al_frente(nodo)
        nodos.append(nodo)
    return lista, nodos


# 1. Lista vacía
lista = ListaDoble()
assert lista.esta_vacia()
verificar("vacía", lista, [])

# 2. Un solo nodo
lista, nodos = armar("A")
assert lista.frente is lista.final
verificar("un nodo", lista, ["A"])

# 3. Tres nodos (el último agregado queda al frente)
lista, nodos = armar("A", "B", "C")
verificar("agregar A, B, C", lista, ["C", "B", "A"])

# 4. Quitar
lista, nodos = armar("A", "B", "C")
lista.quitar(nodos[2])          # C, el frente
verificar("quitar el primero", lista, ["B", "A"])

lista, nodos = armar("A", "B", "C")
lista.quitar(nodos[0])          # A, el final
verificar("quitar el último", lista, ["C", "B"])

lista, nodos = armar("A", "B", "C")
lista.quitar(nodos[1])          # B, el del medio
verificar("quitar el del medio", lista, ["C", "A"])
assert nodos[1].anterior is None and nodos[1].siguiente is None, "el nodo quitado conserva enlaces"

# 5. Mover al frente
lista, nodos = armar("A", "B", "C")
lista.mover_al_frente(nodos[0])  # A, el final
verificar("mover el último al frente", lista, ["A", "C", "B"])

lista, nodos = armar("A", "B", "C")
lista.mover_al_frente(nodos[2])  # C, ya es el frente
verificar("mover el que ya es frente", lista, ["C", "B", "A"])

lista, nodos = armar("A", "B", "C")
lista.mover_al_frente(nodos[1])  # B, el del medio
verificar("mover el del medio al frente", lista, ["B", "C", "A"])

# 6. Vaciar y volver a usar
lista, nodos = armar("A", "B", "C")
for n in nodos:
    lista.quitar(n)
verificar("vaciar quitando todo", lista, [])
lista.agregar_al_frente(Nodo("X", "ficha X"))
verificar("agregar tras vaciar", lista, ["X"])

# 7. Reusar un nodo ya quitado
lista, nodos = armar("A", "B", "C")
lista.quitar(nodos[1])
lista.agregar_al_frente(nodos[1])
verificar("reinsertar un nodo quitado", lista, ["B", "C", "A"])

print("\nTodas las pruebas pasaron")