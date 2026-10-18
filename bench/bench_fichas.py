import gc
import tempfile
import time

from adapters.almacen_fichas import AlmacenFichas
from data.repositorio_catalogo import RepositorioCatalogo
from structures.cache_fichas import CacheFichas

CANTIDADES = (10, 100, 500)
ACTIVAS = (5, 25, 100, 400)
CAPACIDAD_GRANDE = 2000
LATENCIA_RED_MS = 400          # §3.4 del enunciado: cada solicitud tarda ~400 ms
REPETICIONES = 20


class FuenteFalsa:
    """Responde como el servicio pero sin red: solo sirve para poblar el disco."""

    def version_catalogo(self):
        return {"version": "cat-bench"}

    def catalogo(self, ids):
        return {"entidades": [ficha_json(i) for i in ids]}


def ficha_json(id_ficha):
    return {"id": id_ficha, "clase": "enemigo", "nombre": "Enemigo de prueba",
            "vida_max": 12, "ataque": 5, "defensa": 1, "velocidad": 150,
            "comportamiento": "rastreador", "suelta": []}


def ids_de(n):
    return [f"ent_{i:05d}" for i in range(n)]


def repositorio(carpeta, capacidad):
    return RepositorioCatalogo(FuenteFalsa(), CacheFichas(capacidad), AlmacenFichas(carpeta))


def promedio_us(funcion, repeticiones):
    mejor = None
    gc.disable()
    try:
        for _ in range(3):
            inicio = time.perf_counter()
            for _ in range(repeticiones):
                funcion()
            t = (time.perf_counter() - inicio) / repeticiones * 1_000_000
            if mejor is None or t < mejor:
                mejor = t
    finally:
        gc.enable()
    return mejor


def medir_origen(n, carpeta):
    ids = ids_de(n)
    # deja las n fichas guardadas en disco
    repositorio(carpeta, CAPACIDAD_GRANDE).resolver(ids, con_sueltos=False)

    def desde_disco():
        repo = repositorio(carpeta, CAPACIDAD_GRANDE)
        repo.resolver(ids, con_sueltos=False)

    repo_memoria = repositorio(carpeta, CAPACIDAD_GRANDE)
    repo_memoria.resolver(ids, con_sueltos=False)

    def desde_memoria():
        repo_memoria.resolver(ids, con_sueltos=False)

    t_disco = promedio_us(desde_disco, REPETICIONES) / n
    t_memoria = promedio_us(desde_memoria, REPETICIONES) / n
    return t_memoria, t_disco


def medir_en_uso(activas, carpeta):
    repo = repositorio(carpeta, CAPACIDAD_GRANDE)
    todas = ids_de(activas * 2)
    repo.resolver(todas, con_sueltos=False)
    uno = todas[:activas]
    otro = todas[activas:]
    turno = [0]

    def accion():
        # cada acción cambia la mitad de lo que está en uso, como al moverse a otra sala
        turno[0] += 1
        if turno[0] % 2:
            repo.marcar_en_uso(uno)
        else:
            repo.marcar_en_uso(otro)

    return promedio_us(accion, 200)


def ejecutar():
    print(" Fichas: memoria, disco y red ")
    with tempfile.TemporaryDirectory() as carpeta:
        print("\nCosto por ficha según de dónde sale (microsegundos):")
        print(f"{'fichas':>8}{'memoria':>12}{'disco':>12}{'red (cita)':>14}")
        for n in CANTIDADES:
            t_memoria, t_disco = medir_origen(n, carpeta)
            print(f"{n:>8}{t_memoria:>12.1f}{t_disco:>12.1f}{LATENCIA_RED_MS * 1000 / 10:>14.0f}")
        print("red (cita) = 400 ms por solicitud repartidos en un lote lleno de 10 fichas (400 ms si va una sola)")

        print("\nCosto de actualizar las fichas en uso en cada acción del jugador (microsegundos):")
        print(f"{'en uso':>8}{'por acción':>14}{'% de 20 ms':>13}")
        for k in ACTIVAS:
            t = medir_en_uso(k, carpeta)
            print(f"{k:>8}{t:>14.1f}{t / 20000 * 100:>12.2f}%")
    print("\npor acción = marcar_en_uso con la mitad de las fichas distintas (reconciliación)")


if __name__ == "__main__":
    ejecutar()