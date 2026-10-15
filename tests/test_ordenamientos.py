import random

from structures.ordenamientos import insercion, mergesort, ordenar, TAMANO_MAXIMO


def por_valor(x):
    return x


def test_ordenan_una_lista_sencilla():
    assert insercion([5, 2, 8, 1], por_valor) == [1, 2, 5, 8]
    assert mergesort([5, 2, 8, 1], por_valor) == [1, 2, 5, 8]


def test_listas_vacia_y_de_un_elemento():
    for algoritmo in (insercion, mergesort, ordenar):
        assert algoritmo([], por_valor) == []
        assert algoritmo([7], por_valor) == [7]


def test_no_modifican_la_lista_original():
    for algoritmo in (insercion, mergesort, ordenar):
        original = [3, 1, 2]
        algoritmo(original, por_valor)
        assert original == [3, 1, 2]


def test_ordenar_por_clave():
    palabras = ["luna", "sol", "estrella", "mar"]
    assert ordenar(palabras, len) == ["sol", "mar", "luna", "estrella"]


def test_los_empates_conservan_su_orden_original():
    datos = [(2, 0), (1, 1), (2, 2), (1, 3), (2, 4)]
    esperado = [(1, 1), (1, 3), (2, 0), (2, 2), (2, 4)]
    assert insercion(datos, lambda t: t[0]) == esperado
    assert mergesort(datos, lambda t: t[0]) == esperado


def test_listas_aleatorias_quedan_ordenadas_y_completas():
    azar = random.Random(7)
    for _ in range(300):
        n = azar.randint(0, 120)
        datos = [(azar.randint(0, 9), i) for i in range(n)]
        for algoritmo in (insercion, mergesort, ordenar):
            resultado = algoritmo(datos, lambda t: t[0])
            assert len(resultado) == n
            assert sum(t[1] for t in resultado) == sum(range(n))
            for i in range(1, n):
                assert resultado[i - 1] <= resultado[i]


def test_ordenar_da_igual_resultado_en_listas_chicas_y_grandes():
    azar = random.Random(3)
    for n in (TAMANO_MAXIMO, TAMANO_MAXIMO + 1):
        datos = [azar.randint(0, 50) for _ in range(n)]
        assert ordenar(datos, por_valor) == insercion(datos, por_valor)
        assert ordenar(datos, por_valor) == mergesort(datos, por_valor)