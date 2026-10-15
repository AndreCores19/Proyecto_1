from structures.cache_fichas import CacheFichas


def orden(cache):
    """Ids de la lista de uso, del más reciente al menos reciente."""
    ids = []
    actual = cache.orden.frente
    while actual is not None:
        ids.append(actual.id_ficha)
        actual = actual.siguiente
    return ids


def verificar(nombre, cache, esperado_orden):
    """Comprueba el orden de uso y que el vector y la lista guarden las mismas fichas."""
    assert orden(cache) == esperado_orden, f"{nombre}: orden {orden(cache)} != {esperado_orden}"
    assert cache.indice.ids == sorted(esperado_orden), f"{nombre}: vector {cache.indice.ids}"
    assert cache.orden.tamano == len(esperado_orden), f"{nombre}: tamaño {cache.orden.tamano}"
    print("OK  ", nombre, esperado_orden)


def acceder(cache, id_ficha):
    """Lo que haría el programa: consultar y, si falla, traer la ficha e insertarla."""
    ficha = cache.obtener(id_ficha)
    if ficha is None:
        cache.insertar(id_ficha, "ficha " + id_ficha)


# 1. Política LRU: A, B, C, A, D, A con capacidad 3
c = CacheFichas(3)
for i in ["A", "B", "C", "A", "D", "A"]:
    acceder(c, i)
assert c.fallos == 4 and c.aciertos == 2, (c.fallos, c.aciertos)
verificar("LRU: A, B, C, A, D, A", c, ["A", "D", "C"])

# 2. Una ficha en uso no se libera, aunque sea la menos reciente
c = CacheFichas(3)
for i in ["A", "B", "C"]:
    c.insertar(i, "ficha " + i)
c.adquirir("A")                 # A en uso (pasa al frente)
c.obtener("B")
c.obtener("C")                  # A vuelve a ser la menos reciente
verificar("antes de insertar D", c, ["C", "B", "A"])
c.insertar("D", "ficha D")      # debe salir B, no A
verificar("A protegida, sale B", c, ["D", "C", "A"])
c.soltar("A")
c.insertar("E", "ficha E")      # ahora sí puede salir A
verificar("A liberada, sale A", c, ["E", "D", "C"])

# 3. Si todas están en uso, supera el tope y luego se achica sola
c = CacheFichas(2)
c.insertar("A", "ficha A")
c.insertar("B", "ficha B")
c.adquirir("A")
c.adquirir("B")
c.insertar("C", "ficha C")      # todas en uso: se permite superar el tope
assert c.orden.tamano == 3, c.orden.tamano
c.soltar("A")
c.insertar("D", "ficha D")      # libera mientras haya exceso y se pueda
verificar("se achica al soltar", c, ["D", "B"])

# 4. Insertar una ficha que ya existe la actualiza sin duplicarla
c = CacheFichas(3)
c.insertar("A", "vieja")
c.insertar("B", "ficha B")
c.insertar("A", "nueva")
assert c.obtener("A") == "nueva"
verificar("actualizar existente", c, ["A", "B"])

# 5. contiene no cambia el orden ni los contadores
c = CacheFichas(3)
c.insertar("A", "ficha A")
c.insertar("B", "ficha B")
assert c.contiene("A") and not c.contiene("Z")
assert c.aciertos == 0 and c.fallos == 0
verificar("contiene no reordena", c, ["B", "A"])

# 6. Errores esperados
c = CacheFichas(2)
c.insertar("A", "ficha A")
c.adquirir("A")
c.soltar("A")
for nombre, accion in [
    ("soltar de más", lambda: c.soltar("A")),
    ("adquirir inexistente", lambda: c.adquirir("Z")),
    ("soltar inexistente", lambda: c.soltar("Z")),
    ("capacidad 0", lambda: CacheFichas(0)),
]:
    try:
        accion()
        raise AssertionError(nombre + ": debía lanzar ValueError")
    except ValueError:
        print("OK  ", nombre, "-> ValueError")

# 7. Capacidad 1
c = CacheFichas(1)
for i in ["A", "B", "A"]:
    acceder(c, i)
assert c.fallos == 3 and c.aciertos == 0
verificar("capacidad 1", c, ["A"])

print("\nTodas las pruebas pasaron")