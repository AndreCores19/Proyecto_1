import os

from structures.ordenamientos import ordenar

class Puntaje:
    def __init__(self, nombre, id_cripta, version, acciones, derrotados, tiempo, victoria):
        self.nombre = nombre
        self.id_cripta = id_cripta
        self.version = version
        self.acciones = acciones
        self.derrotados = derrotados
        self.tiempo = tiempo
        self.victoria = victoria

class TablaPuntajes:
    def __init__(self, ruta="saves/puntajes.txt"):
        self.ruta = ruta

    def registrar(self, puntaje):
        carpeta = os.path.dirname(self.ruta)
        if carpeta:
            os.makedirs(carpeta, exist_ok=True)
        campos = [puntaje.nombre.replace("|", " "), str(puntaje.id_cripta),
                  str(puntaje.version), str(puntaje.acciones),
                  str(puntaje.derrotados), str(puntaje.tiempo),
                  "1" if puntaje.victoria else "0"]
        with open(self.ruta, "a", encoding="utf-8") as archivo:
            archivo.write("|".join(campos) + "\n")

    def leer(self):
        puntajes = []
        if not os.path.exists(self.ruta):
            return puntajes
        with open(self.ruta, encoding="utf-8") as archivo:
            for linea in archivo:
                partes = linea.rstrip("\n").split("|")
                if len(partes) != 7:
                    continue
                try:
                    puntajes.append(Puntaje(partes[0], partes[1], partes[2],
                                            int(partes[3]), int(partes[4]),
                                            int(partes[5]), partes[6] == "1"))
                except ValueError:
                    continue
        return puntajes

    def ordenados(self):
        ## primero las victorias, luego más enemigos derrotados, luego menos tiempo
        return ordenar(self.leer(),
                       lambda p: (0 if p.victoria else 1, -p.derrotados, p.tiempo))