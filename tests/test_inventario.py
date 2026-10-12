from model.inventario_logica import Inventario
from model.objeto_logica import Objeto


def test_inventario_vista_ordenada():
    inv = Inventario(10)
    datos = [("Escudo", 8, 30), ("Daga", 2, 15), ("Poción", 1, 15), ("Llave", 1, 5)]
    for i, (nombre, peso, valor) in enumerate(datos):
        inv.agregar(Objeto(i, nombre.lower(), nombre, "x", peso, valor))

    por_peso = [o.nombre for o in inv.vista_ordenada("peso")]
    assert por_peso == ["Poción", "Llave", "Daga", "Escudo"]    # Poción y Llave empatan: se respeta el orden original
    por_nombre = [o.nombre for o in inv.vista_ordenada("nombre")]
    assert por_nombre == ["Daga", "Escudo", "Llave", "Poción"]
    assert [o.nombre for o in inv.a_lista()] == ["Escudo", "Daga", "Poción", "Llave"]   # el original no cambió
