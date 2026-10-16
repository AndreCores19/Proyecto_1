from structures.bitacora import Bitacora


def test_bitacora_sin_llenar_devuelve_todo_en_orden():
    b = Bitacora(20)
    for i in range(5):
        b.agregar(f"m{i}")
    assert b.obtener_todos() == ["m0", "m1", "m2", "m3", "m4"]


def test_bitacora_conserva_solo_los_ultimos_20_en_orden():
    b = Bitacora(20)
    for i in range(53):
        b.agregar(f"m{i}")
    assert b.obtener_todos() == [f"m{i}" for i in range(33, 53)]


def test_bitacora_no_crece_mas_alla_de_su_capacidad():
    b = Bitacora(20)
    for i in range(1000):
        b.agregar(f"m{i}")
    assert len(b.buffer) == 20
    assert b.cantidad == 20