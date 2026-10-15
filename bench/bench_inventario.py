import random
import time

from model.inventario_logica import Inventario
from model.objeto_logica import Objeto
from tests.test_retroceso_aleatorio import armar_mundo, accion_al_azar

TAMANOS = (10, 100, 1000, 10000)
OPERACIONES = 20000

class InventarioArreglo:
    def __init__(self, objetos):
        self.objetos = list(objetos)

    def soltar_y_recoger(self, i):
        objeto = self.objetos[i]
        del self.objetos[i]
        self.objetos.append(objeto)

    def equipar(self, i):
        objeto = self.objetos.pop(i)
        self.objetos.insert(0, objeto)


def crear_inventario(n):
    inv = Inventario(n + 1)
    nodos = []
    for i in range(n):
        nodos.append(inv.agregar(Objeto(i, f"obj{i}", f"Objeto {i}", "arma", 1, 1)))
    return inv, nodos


def contar_operaciones(partidas=60, pasos=80):
    cuentas = {"agregar": 0, "quitar_nodo": 0, "mover_al_frente": 0, "siguiente": 0, "anterior": 0}
    acciones = 0
    maximo = 0
    for semilla in range(partidas):
        azar = random.Random(semilla)
        partida, _ = armar_mundo(azar)
        inv = partida.jugador.inventario
        for nombre in cuentas:
            original = getattr(inv, nombre)

            def envuelta(*args, _n=nombre, _f=original, **kw):
                cuentas[_n] += 1
                return _f(*args, **kw)
            setattr(inv, nombre, envuelta)
        for _ in range(pasos):
            if partida.terminada:
                break
            accion_al_azar(partida, azar)
            acciones += 1
            maximo = max(maximo, inv.tamano)
    return cuentas, acciones, maximo


def medir_soltar(n, azar):
    inv, nodos = crear_inventario(n)
    arreglo = InventarioArreglo(range(n))
    posiciones = [azar.randrange(n) for _ in range(OPERACIONES)]

    inicio = time.perf_counter()
    for p in posiciones:
        arreglo.soltar_y_recoger(p)
    t_arreglo = (time.perf_counter() - inicio) / OPERACIONES * 1_000_000

    inicio = time.perf_counter()
    for p in posiciones:
        nodo = nodos[p]
        inv.quitar_nodo(nodo)
        nodos[p] = inv.agregar(nodo.objeto)
    t_enlazada = (time.perf_counter() - inicio) / OPERACIONES * 1_000_000
    return t_enlazada, t_arreglo


def medir_equipar(n, azar):
    inv, nodos = crear_inventario(n)
    arreglo = InventarioArreglo(range(n))
    posiciones = [azar.randrange(n) for _ in range(OPERACIONES)]

    inicio = time.perf_counter()
    for p in posiciones:
        arreglo.equipar(p)
    t_arreglo = (time.perf_counter() - inicio) / OPERACIONES * 1_000_000

    elegidos = [nodos[p] for p in posiciones]
    inicio = time.perf_counter()
    for nodo in elegidos:
        inv.mover_al_frente(nodo)
    t_enlazada = (time.perf_counter() - inicio) / OPERACIONES * 1_000_000
    return t_enlazada, t_arreglo


def ejecutar():
    print(" Inventario ")
    cuentas, acciones, maximo = contar_operaciones()
    print(f"Operaciones reales en {acciones} acciones al azar del jugador:")
    for nombre, total in cuentas.items():
        print(f"    {nombre:<16} {total:6d}  ({total / acciones:.3f} por acción)")
    print(f"    objetos en el inventario a la vez (máximo): {maximo}")

    for titulo, funcion in (("Soltar el objeto que se ve y volver a recogerlo (microsegundos)", medir_soltar),
                            ("Llevar al frente el objeto que se ve, como al equipar (microsegundos)", medir_equipar)):
        print(f"\n{titulo}:")
        print(f"{'objetos':>9}{'lista enlazada':>18}{'arreglo':>12}")
        for n in TAMANOS:
            t_enlazada, t_arreglo = funcion(n, random.Random(3))
            print(f"{n:>9}{t_enlazada:>18.2f}{t_arreglo:>12.2f}")


if __name__ == "__main__":
    ejecutar()