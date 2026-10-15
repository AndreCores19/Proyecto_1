import copy
import gc
import sys
import time

from bench.bench_ciclo import armar_partida

ESCENARIOS = (
    ("pequeño", 10, 10),
    ("larga", 300, 10),
    ("normal", 20, 30),
    ("grande", 100, 100),
    ("extremo", 100, 300),
)
REPETICIONES = 300


def promedio_us(funcion, repeticiones):
    gc.disable()
    try:
        inicio = time.perf_counter()
        for _ in range(repeticiones):
            funcion()
        return (time.perf_counter() - inicio) / repeticiones * 1_000_000
    finally:
        gc.enable()


def medir(salas, enemigos):
    partida = armar_partida(salas, enemigos)
    for _ in range(20):
        partida.esperar_jugador()

    t_accion = promedio_us(partida.esperar_jugador, REPETICIONES)

    sys.setrecursionlimit(100000)
    t_copia = promedio_us(lambda: copy.deepcopy(partida), 20)

    tiempos = []
    for _ in range(REPETICIONES):
        partida.esperar_jugador()
        gc.disable()
        inicio = time.perf_counter()
        partida.retroceder()
        tiempos.append((time.perf_counter() - inicio) * 1_000_000)
        gc.enable()
    t_deshacer = sum(tiempos) / len(tiempos)

    for _ in range(10):
        partida.esperar_jugador()
    h = partida.historial
    guardadas = 0
    for i in range(h.cantidad):
        guardadas += len(h.cuadros[(h.inicio + i) % h.capacidad])
    return t_accion, t_copia, t_deshacer, guardadas, h.cantidad


def ejecutar():
    print(" Historial de retroceso (microsegundos por operación) ")
    print(f"{'escenario':>10}{'salas':>7}{'enemigos':>10}{'acción':>10}"
          f"{'copiar todo':>13}{'deshacer':>10}{'registros':>11}")
    for nombre, salas, enemigos in ESCENARIOS:
        t_accion, t_copia, t_deshacer, guardadas, cuadros = medir(salas, enemigos)
        print(f"{nombre:>10}{salas:>7}{enemigos:>10}{t_accion:>10.1f}"
              f"{t_copia:>13.1f}{t_deshacer:>10.1f}{guardadas:>8} en {cuadros}")
    print("\nacción      = una acción del jugador con el historial activo")
    print("copiar todo = copiar el estado completo (alternativa descartada, prohibida)")
    print("deshacer    = revertir la última acción con el historial")
    print("registros   = funciones de deshacer guardadas en total (máximo 5 cuadros)")


if __name__ == "__main__":
    ejecutar()