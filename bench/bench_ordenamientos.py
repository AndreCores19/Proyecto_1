import random
import time

from structures.ordenamientos import insercion, mergesort

TAMANOS = (4, 8, 16, 24, 32, 40, 48, 64, 72, 80, 90, 100, 200)
TANDAS = 7


def medir(algoritmo, datos, repeticiones):
    #El tiempo que dura cada tanda se promedia y se devuelve el mejor promedio de todas las tandas
    mejor = None
    for _ in range(TANDAS):
        inicio = time.perf_counter()
        for _ in range(repeticiones):
            algoritmo(datos, lambda x: x)
        promedio = (time.perf_counter() - inicio) / repeticiones * 1_000_000
        if mejor is None or promedio < mejor:
            mejor = promedio
    return mejor


if __name__ == "__main__":
    azar = random.Random(1)
    print(f"{'n':>5} {'insercion (us)':>16} {'mergesort (us)':>16}  mejor")
    corte = None
    for n in TAMANOS:
        datos = [azar.randint(0, 1000) for _ in range(n)]
        repeticiones = max(200, 20000 // n)
        t_ins = medir(insercion, datos, repeticiones)
        t_mer = medir(mergesort, datos, repeticiones)
        mejor = "insercion" if t_ins <= t_mer else "mergesort"
        if mejor == "mergesort" and corte is None:
            corte = n
        print(f"{n:>5} {t_ins:>16.1f} {t_mer:>16.1f}  {mejor}")
    print(f"\nMergesort empieza a ganar desde n = {corte}")