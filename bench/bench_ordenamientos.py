import random
import time

from structures.ordenamientos import insercion, mergesort, ordenar

TAMANOS = (4, 8, 16, 24, 32, 40, 48, 64, 72, 80, 90, 100, 200)
TAMANOS_GRANDES = (16, 48, 200, 1000)
TANDAS = 5


def medir(algoritmo, datos, clave, repeticiones):
    mejor = None
    for _ in range(TANDAS):
        inicio = time.perf_counter()
        for _ in range(repeticiones):
            algoritmo(datos, clave)
        promedio = (time.perf_counter() - inicio) / repeticiones * 1_000_000
        if mejor is None or promedio < mejor:
            mejor = promedio
    return mejor


def identidad(x):
    return x


def clave_costosa(x):
    for _ in range(30):
        pass
    return x


def contar_llamadas(algoritmo, datos):
    llamadas = [0]

    def clave(x):
        llamadas[0] += 1
        return x
    algoritmo(datos, clave)
    return llamadas[0]


def al_azar(n, azar):
    return [azar.randint(0, 100000) for _ in range(n)]


def casi_ordenado(n, azar):
    datos = list(range(n))
    for _ in range(max(1, n // 20)):
        i = azar.randrange(n - 1)
        datos[i], datos[i + 1] = datos[i + 1], datos[i]
    return datos


def ordenado(n, azar):
    return list(range(n))


def inverso(n, azar):
    return list(range(n, 0, -1))


def dos_mitades(n, azar):
    return list(range(n // 2, n)) + list(range(0, n // 2))


TIPOS = (("al azar", al_azar), ("casi ordenado", casi_ordenado),
         ("ordenado", ordenado), ("inverso", inverso), ("dos mitades", dos_mitades))


def repeticiones_para(n):
    return max(3, 6000 // n)


def ejecutar():
    azar = random.Random(1)
    print(" Ordenamientos (microsegundos por ordenamiento) ")

    print("\nDatos al azar, por tamaño:")
    print(f"{'n':>5} {'insercion':>12} {'mergesort':>12}  mejor")
    corte = None
    for n in TAMANOS:
        datos = al_azar(n, azar)
        reps = max(200, 20000 // n)
        t_ins = medir(insercion, datos, identidad, reps)
        t_mer = medir(mergesort, datos, identidad, reps)
        mejor = "insercion" if t_ins <= t_mer else "mergesort"
        if mejor == "mergesort" and corte is None:
            corte = n
        print(f"{n:>5} {t_ins:>12.1f} {t_mer:>12.1f}  {mejor}")
    print(f"Mergesort empieza a ganar desde n = {corte}")

    print("\nSegún cómo vienen los datos (ordenar = la selección automática del programa):")
    print(f"{'tipo de datos':>14}{'n':>6}{'insercion':>12}{'mergesort':>12}{'ordenar':>12}  mejor fijo")
    for nombre, fabrica in TIPOS:
        for n in TAMANOS_GRANDES:
            datos = fabrica(n, azar)
            reps = repeticiones_para(n)
            t_ins = medir(insercion, datos, identidad, reps)
            t_mer = medir(mergesort, datos, identidad, reps)
            t_aut = medir(ordenar, datos, identidad, reps)
            mejor = "insercion" if t_ins <= t_mer else "mergesort"
            print(f"{nombre:>14}{n:>6}{t_ins:>12.1f}{t_mer:>12.1f}{t_aut:>12.1f}  {mejor}")

    print("\nComparaciones costosas (datos al azar): veces que se pide la clave y tiempo con clave costosa:")
    print(f"{'n':>5}{'llamadas ins':>14}{'llamadas mer':>14}{'ins (us)':>12}{'mer (us)':>12}  mejor")
    for n in (8, 16, 32, 48, 100, 200):
        datos = al_azar(n, azar)
        reps = max(20, 3000 // n)
        l_ins = contar_llamadas(insercion, datos)
        l_mer = contar_llamadas(mergesort, datos)
        t_ins = medir(insercion, datos, clave_costosa, reps)
        t_mer = medir(mergesort, datos, clave_costosa, reps)
        mejor = "insercion" if t_ins <= t_mer else "mergesort"
        print(f"{n:>5}{l_ins:>14}{l_mer:>14}{t_ins:>12.1f}{t_mer:>12.1f}  {mejor}")


if __name__ == "__main__":
    ejecutar()