import copy
import gc
import os
import random
import sys
import tempfile
import time

from adapters.bitacora_partida import BitacoraPartida
from adapters.lector_bitacora import LectorBitacora
from bench.bench_ciclo import armar_partida

CANTIDADES = (100, 1000, 10000)
COMANDOS = ("w", "a", "s", "d", "esperar", "atacar", "recoger 1", "usar 1", "sig", "ant")


class BitacoraSinFlush(BitacoraPartida):
    """Alternativa descartada: no forzar la escritura en cada comando."""

    def anotar(self, comando):
        if self.archivo is not None:
            self.archivo.write(f"{comando}\n")


def comandos_al_azar(n):
    azar = random.Random(11)
    return [azar.choice(COMANDOS) for _ in range(n)]


def escribir(clase, carpeta, comandos):
    bitacora = clase(carpeta)
    bitacora.abrir("cripta-bench", "v1", "cat-1", 12345)
    inicio = time.perf_counter()
    for c in comandos:
        bitacora.anotar(c)
    t = (time.perf_counter() - inicio) / len(comandos) * 1_000_000
    bitacora.cerrar()
    return t, os.path.join(carpeta, "partida_cripta-bench_12345.log")


def leer_ms(ruta):
    mejor = None
    for _ in range(3):
        inicio = time.perf_counter()
        lector = LectorBitacora(ruta)
        lector.leer()
        t = (time.perf_counter() - inicio) * 1000
        if mejor is None or t < mejor:
            mejor = t
    return mejor, len(lector.comandos)


def reejecutar_ms(n):
    partida = armar_partida(100, 100)
    gc.disable()
    try:
        inicio = time.perf_counter()
        for _ in range(n):
            partida.esperar_jugador()
        return (time.perf_counter() - inicio) * 1000
    finally:
        gc.enable()


def copiar_estado_ms():
    partida = armar_partida(100, 100)
    for _ in range(50):
        partida.esperar_jugador()
    sys.setrecursionlimit(100000)
    inicio = time.perf_counter()
    for _ in range(5):
        copy.deepcopy(partida)
    return (time.perf_counter() - inicio) / 5 * 1000


def ejecutar():
    print(" Bitácora de partida y reproducción ")
    with tempfile.TemporaryDirectory() as carpeta:
        print("\nAnotar un comando (microsegundos) y tamaño del archivo:")
        print(f"{'comandos':>9}{'con flush':>11}{'sin flush':>11}{'tamaño':>10}{'bytes/cmd':>11}")
        for n in CANTIDADES:
            comandos = comandos_al_azar(n)
            t_flush, ruta = escribir(BitacoraPartida, carpeta, comandos)
            t_sin, _ = escribir(BitacoraSinFlush, carpeta, comandos)
            escribir(BitacoraPartida, carpeta, comandos)   # deja el archivo con flush para medir
            tam = os.path.getsize(ruta)
            print(f"{n:>9}{t_flush:>11.1f}{t_sin:>11.2f}{tam / 1024:>8.1f}KB{tam / n:>11.1f}")

        print("\nLeer la bitácora completa y volver a ejecutar los comandos (milisegundos):")
        print(f"{'comandos':>9}{'leer':>10}{'reejecutar':>13}")
        for n in CANTIDADES:
            comandos = comandos_al_azar(n)
            escribir(BitacoraPartida, carpeta, comandos)
            ruta = os.path.join(carpeta, "partida_cripta-bench_12345.log")
            t_leer, leidos = leer_ms(ruta)
            assert leidos == n
            print(f"{n:>9}{t_leer:>10.2f}{reejecutar_ms(n):>13.1f}")

        print(f"\nAlternativa descartada: copiar el estado completo por comando = "
              f"{copiar_estado_ms():.2f} ms cada vez (cripta de 100 salas, 100 enemigos)")
    print("\ncon flush  = lo que hace el programa (cada comando queda en disco aunque el programa se caiga)")
    print("reejecutar = costo del modelo solo, con la acción 'esperar' y sin red ni vista")


if __name__ == "__main__":
    ejecutar()