from pathlib import Path
import json

class AlmacenFichas:
    def __init__(self, carpeta):
        self.carpeta = Path(carpeta)
        self.carpeta.mkdir(parents=True, exist_ok=True)

    def guardar(self, id_ficha, json_ficha):
        ruta = self._ruta(id_ficha)
        with open(ruta, 'w', encoding="utf-8") as archivo:
            json.dump(json_ficha, archivo, ensure_ascii=False)

    def leer(self, id_ficha):
        ruta = self._ruta(id_ficha)
        if not ruta.exists():
            return None
        try:
            with open(ruta, 'r', encoding="utf-8") as archivo:
                return json.load(archivo)
        except (json.JSONDecodeError, UnicodeDecodeError):
            return None

    def version(self):
        ruta = self.carpeta / "version.txt"
        if not ruta.exists():
            return None
        try:
            with open(ruta, 'r', encoding="utf-8") as archivo:
                texto = archivo.read().strip()
                return texto or None
        except UnicodeDecodeError:
            return None

    def guardar_version(self, valor):
        ruta = self.carpeta / "version.txt"
        with open(ruta, 'w', encoding="utf-8") as archivo:
            archivo.write(valor)

    def borrar_todo(self):
        for archivo in self.carpeta.glob("*.json"):
            archivo.unlink()
        ruta_version = self.carpeta / "version.txt"
        if ruta_version.exists():
            ruta_version.unlink()

    def _ruta(self, id_ficha):
        if not id_ficha or "/" in id_ficha or "\\" in id_ficha:
            raise ValueError(f"Id de ficha no válido: {id_ficha}")
        return self.carpeta / f"{id_ficha}.json"
