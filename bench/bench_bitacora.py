import gc
import time
import tracemalloc

from structures.bitacora import Bitacora

CAPACIDAD = 20


class BitacoraGuardaTodo:

    def __init__(self):
        self.mensajes = []

    def agregar(self, mensaje):
        self.mensajes.append(mensaje)

    def obtener_todos(self):
        return self.mensajes[-CAPACIDAD:]


class BitacoraDesplaza:
    def __init__(self, capacidad):
        self.capacidad = capacidad
        self.mensajes = []

    def agregar(self, mensaje):
        self.mensajes.append(mensaje)
        if len(self.mensajes) > self.capacidad:
            del self.mensajes[0]

    def obtener_todos(self):
        return list(self.mensajes)


def memoria_kb(fabrica, cantidad):
    gc.collect()
    tracemalloc.start()
    estructura = fabrica()
    for i in range(cantidad):
        estructura.agregar(f"t={i}: mensaje de prueba número {i}")
    actual, _ = tracemalloc.get_traced_memory()
    tracemalloc.stop()
    del estructura
    return actual / 1024

def tiempo_agregar_us(fabrica, repeticiones=200000):
    mensajes = [f"t={i}: mensaje de prueba" for i in range(1000)]
    estructura = fabrica()
    for m in mensajes:
        estructura.agregar(m)
    gc.disable()
    try:
        inicio = time.perf_counter()
        for i in range(repeticiones):
            estructura.agregar(mensajes[i % 1000])
        return (time.perf_counter() - inicio) / repeticiones * 1_000_000
    finally:
        gc.enable()


def ejecutar():
    print(" Bitácora ")

    print("\nMemoria usada después de agregar N mensajes (KB):")
    print(f"{'mensajes':>10}{'buffer circular':>17}{'guarda todo':>14}")
    for n in (1000, 100000, 500000):
        circular = memoria_kb(lambda: Bitacora(CAPACIDAD), n)
        todo = memoria_kb(BitacoraGuardaTodo, n)
        print(f"{n:>10}{circular:>17.1f}{todo:>14.1f}")

    print("\nTiempo por mensaje agregado, con la bitácora llena (microsegundos):")
    print(f"{'capacidad':>10}{'buffer circular':>17}{'desplazando':>14}")
    for capacidad in (20, 200, 2000, 20000):
        circular = tiempo_agregar_us(lambda: Bitacora(capacidad))
        desplaza = tiempo_agregar_us(lambda: BitacoraDesplaza(capacidad))
        print(f"{capacidad:>10}{circular:>17.3f}{desplaza:>14.3f}")
    print("\nbuffer circular = lo que hace el programa")
    print("guarda todo     = guardar todos los mensajes y mostrar los últimos 20")
    print("desplazando     = lista que, al llenarse, saca el primero y corre los demás")


if __name__ == "__main__":
    ejecutar()