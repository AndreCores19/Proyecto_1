import random
import time

from structures.cache_fichas import CacheFichas
from structures.vector_busqueda import VectorBusqueda

TAMANOS = (25, 250, 2500, 25000)
BUSQUEDAS = 20000
TANDAS = 5
CAPACIDADES = (10, 25, 50)
ACCESOS = 20000


class IndiceLineal:
    """Alternativa descartada: listas paralelas con búsqueda lineal."""

    def __init__(self):
        self.ids = []
        self.valores = []
        self.comparaciones = 0

    def agregar(self, id_elemento, valor):
        self.ids.append(id_elemento)
        self.valores.append(valor)

    def buscar(self, id_buscado):
        for i in range(len(self.ids)):
            self.comparaciones += 1
            if self.ids[i] == id_buscado:
                return self.valores[i]
        return None


class CacheFifo:
    """Alternativa descartada: expulsa la más antigua en llegar, sin importar el uso."""

    def __init__(self, capacidad):
        self.capacidad = capacidad
        self.ids = []

    def acceder(self, id_ficha):
        for i in range(len(self.ids)):
            if self.ids[i] == id_ficha:
                return True
        if len(self.ids) >= self.capacidad:
            del self.ids[0]
        self.ids.append(id_ficha)
        return False


def llenar(indice, n):
    for i in range(n):
        indice.agregar(f"ficha-{i:06d}", i)


def mejor_us(indice, consultas):
    mejor = None
    for _ in range(TANDAS):
        inicio = time.perf_counter()
        for c in consultas:
            indice.buscar(c)
        promedio = (time.perf_counter() - inicio) / len(consultas) * 1_000_000
        if mejor is None or promedio < mejor:
            mejor = promedio
    return mejor


def comparaciones_promedio(indice, consultas):
    indice.comparaciones = 0
    for c in consultas:
        indice.buscar(c)
    return indice.comparaciones / len(consultas)


def medir_busqueda(n, azar):
    binario, lineal = VectorBusqueda(), IndiceLineal()
    llenar(binario, n)
    llenar(lineal, n)
    binario.preparar()
    consultas = [f"ficha-{azar.randrange(n):06d}" for _ in range(BUSQUEDAS)]
    return (mejor_us(binario, consultas), mejor_us(lineal, consultas),
            comparaciones_promedio(binario, consultas),
            comparaciones_promedio(lineal, consultas))


def patron_con_localidad(azar, total_fichas=300, calientes=20):
    """80% de los accesos a pocas fichas (las de la zona actual), 20% al resto."""
    if azar.random() < 0.8:
        return f"ficha-{azar.randrange(calientes):06d}"
    return f"ficha-{azar.randrange(total_fichas):06d}"


def tasa_lru(capacidad, accesos):
    cache = CacheFichas(capacidad)
    aciertos = 0
    for id_ficha in accesos:
        if cache.obtener(id_ficha) is not None:
            aciertos += 1
        else:
            cache.insertar(id_ficha, id_ficha)
    return aciertos / len(accesos) * 100


def tasa_fifo(capacidad, accesos):
    cache = CacheFifo(capacidad)
    aciertos = 0
    for id_ficha in accesos:
        if cache.acceder(id_ficha):
            aciertos += 1
    return aciertos / len(accesos) * 100


def ejecutar():
    azar = random.Random(5)
    print(" Caché de fichas ")

    print("\nBuscar una ficha por id (microsegundos y comparaciones por búsqueda):")
    print(f"{'fichas':>8}{'binaria us':>12}{'lineal us':>11}{'binaria cmp':>13}{'lineal cmp':>12}")
    for n in TAMANOS:
        t_bin, t_lin, c_bin, c_lin = medir_busqueda(n, azar)
        print(f"{n:>8}{t_bin:>12.2f}{t_lin:>11.2f}{c_bin:>13.1f}{c_lin:>12.1f}")

    print("\nTasa de aciertos con accesos que repiten zona (80% a 20 fichas, 300 fichas en total):")
    accesos = [patron_con_localidad(azar) for _ in range(ACCESOS)]
    print(f"{'capacidad':>10}{'LRU %':>9}{'FIFO %':>9}")
    for capacidad in CAPACIDADES:
        print(f"{capacidad:>10}{tasa_lru(capacidad, accesos):>9.1f}{tasa_fifo(capacidad, accesos):>9.1f}")

    print("\nTasa de aciertos con accesos sin repetición de zona (uniformes sobre 300 fichas):")
    uniformes = [f"ficha-{azar.randrange(300):06d}" for _ in range(ACCESOS)]
    print(f"{'capacidad':>10}{'LRU %':>9}{'FIFO %':>9}")
    for capacidad in CAPACIDADES:
        print(f"{capacidad:>10}{tasa_lru(capacidad, uniformes):>9.1f}{tasa_fifo(capacidad, uniformes):>9.1f}")

    print("\nbinaria = VectorBusqueda (lo que hace el programa), lineal = recorrer las listas paralelas")
    print("LRU     = CacheFichas (lista doble + vector), FIFO = expulsar la más antigua en llegar")


if __name__ == "__main__":
    ejecutar()