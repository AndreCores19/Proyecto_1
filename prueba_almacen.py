import tempfile
from pathlib import Path

from adapters.almacen_fichas import AlmacenFichas

FICHA = {'id': 'ent_guardian_oseo', 'nombre': 'Guardián óseo', 'clase': 'enemigo', 'daño': 4}


def lanza_valueerror(accion):
    try:
        accion()
    except ValueError:
        return True
    return False


# Todo se hace en una carpeta temporal que se borra sola al terminar
with tempfile.TemporaryDirectory() as tmp:
    base = Path(tmp)

    # 1. La carpeta se crea si no existe
    almacen = AlmacenFichas(base / "cache" / "catalogo")
    assert (base / "cache" / "catalogo").is_dir()
    print("OK   crea la carpeta si no existe")

    # 2. Guardar y leer, con tildes y ñ
    almacen.guardar("ent_guardian_oseo", FICHA)
    assert almacen.leer("ent_guardian_oseo") == FICHA
    texto = (almacen.carpeta / "ent_guardian_oseo.json").read_text(encoding="utf-8")
    assert "Guardián óseo" in texto and "daño" in texto      # se guardó legible
    print("OK   guardar y leer con tildes y ñ")

    # 3. Leer un id que no existe
    assert almacen.leer("itm_no_existe") is None
    print("OK   id inexistente -> None")

    # 4. Ids no válidos: deben avisar, no devolver None
    for malo in ["../x", "a/b", "a\\b", ""]:
        assert lanza_valueerror(lambda: almacen.guardar(malo, FICHA)), malo
        assert lanza_valueerror(lambda: almacen.leer(malo)), malo
    print("OK   ids no válidos -> ValueError (guardar y leer)")

    # 5. Sobrescribir una ficha
    almacen.guardar("ent_guardian_oseo", {'id': 'ent_guardian_oseo', 'nombre': 'Nuevo'})
    assert almacen.leer("ent_guardian_oseo")['nombre'] == 'Nuevo'
    print("OK   sobrescribir")

    # 6. Archivos dañados se tratan como inexistentes
    (almacen.carpeta / "roto.json").write_text('{"nombre": "Guard', encoding="utf-8")
    assert almacen.leer("roto") is None
    (almacen.carpeta / "cortado.json").write_bytes(b'{"nombre": "Guardi\xc3')   # UTF-8 cortado
    assert almacen.leer("cortado") is None
    print("OK   archivo dañado -> None (JSON inválido y UTF-8 cortado)")

    # 7. Versión
    assert almacen.version() is None
    almacen.guardar_version("cat-2026-08-01")
    assert almacen.version() == "cat-2026-08-01"
    (almacen.carpeta / "version.txt").write_text("cat-2\n", encoding="utf-8")
    assert almacen.version() == "cat-2"
    (almacen.carpeta / "version.txt").write_text("", encoding="utf-8")
    assert almacen.version() is None
    print("OK   versión: ausente, guardada, con salto de línea y vacía")

    # 8. Persistencia: un almacén nuevo lee lo que dejó el anterior
    almacen.guardar_version("cat-3")
    almacen.guardar("itm_antorcha", {'id': 'itm_antorcha', 'nombre': 'Antorcha'})
    otro = AlmacenFichas(base / "cache" / "catalogo")
    assert otro.leer("itm_antorcha")['nombre'] == 'Antorcha' and otro.version() == "cat-3"
    print("OK   sobrevive a 'cerrar y abrir' (instancia nueva)")

    # 9. borrar_todo: borra fichas y versión, no otros archivos ni la carpeta
    (almacen.carpeta / "nota.txt").write_text("no tocar", encoding="utf-8")
    almacen.borrar_todo()
    assert almacen.leer("itm_antorcha") is None and almacen.leer("ent_guardian_oseo") is None
    assert almacen.version() is None
    assert (almacen.carpeta / "nota.txt").exists() and almacen.carpeta.is_dir()
    print("OK   borrar_todo: sin fichas ni versión, la carpeta y otros archivos siguen")

print("\nTodas las pruebas pasaron (la carpeta temporal ya se borró)")