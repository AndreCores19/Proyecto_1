from structures.vector_busqueda import VectorBusqueda

v = VectorBusqueda()
for i in [1, 2, 3, 4, 5, 6]:
    v.agregar(i, f"sala{i}")
v.preparar()
print(v.buscar(5), v.buscar(99))
print(v.posicion(1))