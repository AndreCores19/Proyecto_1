import gc
import time

from model.enemigo_logica import Enemigo
from model.jugador_logica import Jugador
from model.partida_logica import Partida, RASTRO_VIGENCIA
from model.sala_logica import Sala, Salida
from structures.agenda import AgendaEventos

REPETICIONES = 20000


def promedio_us(funcion, repeticiones):
    gc.disable()
    try:
        inicio = time.perf_counter()
        for _ in range(repeticiones):
            funcion()
        return (time.perf_counter() - inicio) / repeticiones * 1_000_000
    finally:
        gc.enable()


def jugador_en(sala):
    return Jugador(vida_max=10**9, ataque=10, defensa=10**6, velocidad=100, inventario_max=10, sala=sala)


def armar_estrella(salidas, total_salas):
    centro = Sala(0, "centro")
    vecinas = []
    for i in range(salidas):
        v = Sala(i + 1, f"v{i + 1}")
        centro.agregar_salida(Salida("NSEO"[i], v))
        v.agregar_salida(Salida("X", centro))
        vecinas.append(v)
    anterior = vecinas[0]
    for i in range(salidas + 1, total_salas):
        s = Sala(i, f"s{i}")
        anterior.agregar_salida(Salida("N", s))
        s.agregar_salida(Salida("S", anterior))
        s.rastro = 1000 - (i % 300)
        anterior = s
    partida = Partida(jugador_en(centro), semilla=1)
    partida.reloj = 1100
    for i, v in enumerate(vecinas):
        v.rastro = 1050 - i
    rastreador = Enemigo("r", "x", "Rastreador", 50, 50, 5, 0, 100, "rastreador", centro)
    return partida, rastreador


def medir_decision(salidas, total_salas):
    partida, rastreador = armar_estrella(salidas, total_salas)
    return promedio_us(lambda: partida._decidir_rastreador(rastreador), REPETICIONES)


def armar_con_rastreadores(num_salas, num_rastreadores):
    salas = [Sala(i + 1, f"Sala {i + 1}") for i in range(num_salas)]
    for i in range(num_salas - 1):
        salas[i].agregar_salida(Salida("N", salas[i + 1]))
        salas[i + 1].agregar_salida(Salida("S", salas[i]))
    partida = Partida(jugador_en(salas[0]), semilla=1)
    for k in range(num_rastreadores):
        sala = salas[k % num_salas]
        partida.activar_enemigo(
            Enemigo(f"r{k}", "x", f"Rastreador {k}", 50, 50, 5, 0, 100, "rastreador", sala))
    return partida


def medir_por_ciclo(num_rastreadores):
    partida = armar_con_rastreadores(100, num_rastreadores)
    for _ in range(10):
        partida.esperar_jugador()
    repeticiones = 200
    t = promedio_us(partida.esperar_jugador, repeticiones)
    return t, t / num_rastreadores

def medir_perezoso(num_salas):
    salas = [Sala(i, f"s{i}") for i in range(num_salas)]
    reloj = [0]

    def mover():
        reloj[0] += 100
        salas[reloj[0] // 100 % num_salas].rastro = reloj[0]
    return promedio_us(mover, REPETICIONES)


def medir_con_evento(num_salas):
    salas = [Sala(i, f"s{i}") for i in range(num_salas)]
    agenda = AgendaEventos()
    pendientes = [None] * num_salas
    reloj = [0]
    for i in range(num_salas):
        pendientes[i] = agenda.programar(400 + i, "caduca", salas[i])

    def mover():
        reloj[0] += 100
        i = reloj[0] // 100 % num_salas
        salas[i].rastro = reloj[0]
        if pendientes[i] is not None and pendientes[i].pos >= 0:
            agenda.cancelar(pendientes[i])
        pendientes[i] = agenda.programar(reloj[0] + RASTRO_VIGENCIA, "caduca", salas[i])
    return promedio_us(mover, REPETICIONES // 4)


def medir_barrido(num_salas):
    salas = [Sala(i, f"s{i}") for i in range(num_salas)]
    for i, s in enumerate(salas):
        s.rastro = i
    reloj = [10_000]

    def barrer():
        reloj[0] += 100
        for s in salas:
            if s.rastro is not None and reloj[0] - s.rastro >= RASTRO_VIGENCIA:
                s.rastro = None
    return promedio_us(barrer, max(50, REPETICIONES // max(1, num_salas // 10)))


def ejecutar():
    print(" Rastro (microsegundos por operación) ")

    print("\nDecisión de un rastreador (el resto de la cripta es un recorrido largo con rastro):")
    tamanos = (10, 100, 1000, 5000)
    print(f"{'salidas':>9}" + "".join(f"{str(n) + ' salas':>13}" for n in tamanos))
    for salidas in (1, 2, 3, 4):
        print(f"{salidas:>9}" + "".join(f"{medir_decision(salidas, n):>13.2f}" for n in tamanos))

    print("\nUn ciclo con muchos rastreadores actuando (cripta de 100 salas):")
    print(f"{'rastreadores':>13}{'ciclo':>12}{'por rastreador':>16}")
    for r in (10, 100, 1000):
        t, por = medir_por_ciclo(r)
        print(f"{r:>13}{t:>12.1f}{por:>16.2f}")

    print("\nCosto de mantener el rastro en cada movimiento del jugador:")
    print(f"{'salas':>9}{'al consultar':>14}{'con evento':>12}{'con barrido':>13}")
    for n in (100, 1000, 5000):
        print(f"{n:>9}{medir_perezoso(n):>14.2f}{medir_con_evento(n):>12.2f}{medir_barrido(n):>13.2f}")
    print("\nal consultar = lo que hace el programa (anotar el instante)")
    print("con evento   = programar y cancelar un evento de caducidad por sala")
    print("con barrido  = recorrer todas las salas en cada acción")


if __name__ == "__main__":
    ejecutar()