import random
import tempfile

from adapters.almacen_fichas import AlmacenFichas
from bench.bench_precarga import DatosFalsos, cuadricula
from data.repositorio_catalogo import RepositorioCatalogo
from data.repositorio_cripta import RepositorioCripta
from structures.cache_fichas import CacheFichas

ESCENARIOS = ((10, 150, 30), (30, 80, 30), (30, 200, 60))   # (lado, pasos, tipos distintos)
SEMILLAS = 10
CAPACIDAD = 25
MODOS = ("un id por solicitud", "por sala", "por lote de salas")


class FuenteContadora:
    """Genera contenidos con pocos tipos muy comunes y muchos raros, y cuenta las solicitudes."""

    def __init__(self, tipos):
        self.tipos = tipos
        self.sol_contenido = 0
        self.sol_catalogo = 0

    def version_catalogo(self):
        return {"version": "v1"}

    def contenido_salas(self, id_cripta, ids):
        self.sol_contenido += 1
        salas = []
        for i in ids:
            azar = random.Random(i)
            t = [self.tipos[min(int(azar.expovariate(0.15)), len(self.tipos) - 1)] for _ in range(4)]
            salas.append({"sala": i,
                          "enemigos": [{"instancia": f"e-{i}", "tipo": t[0], "vida": None}],
                          "objetos": [t[1], t[2]],
                          "trampas": [{"instancia": f"t-{i}", "tipo": t[3]}]})
        return {"contenido": salas}

    def catalogo(self, ids):
        self.sol_catalogo += 1
        return {"entidades": [{"id": i, "clase": "enemigo", "nombre": i, "vida_max": 1,
                               "ataque": 1, "defensa": 1, "velocidad": 1,
                               "comportamiento": "errante", "suelta": []} for i in ids]}


def caminata(lado, pasos, tipos_distintos, modo, semilla):
    tipos = [f"tipo_{k:03d}" for k in range(tipos_distintos)]
    fuente = FuenteContadora(tipos)
    with tempfile.TemporaryDirectory() as carpeta:
        cripta = RepositorioCripta(fuente)
        cripta.datos = DatosFalsos()
        cripta.construir_salas(cuadricula(lado))
        catalogo = RepositorioCatalogo(fuente, CacheFichas(CAPACIDAD), AlmacenFichas(carpeta))
        azar = random.Random(semilla)
        pobladas = [False] * cripta.salas.tamano()
        vistos = []

        def entrar(sala):
            pos = cripta.salas.posicion(sala.id)
            if pobladas[pos]:
                return
            if cripta.contenidos[pos] is None:
                nuevos = cripta.precargar(sala.id)
                if modo == "por lote de salas":
                    catalogo.resolver(catalogo.ids_del_contenido(nuevos))
            ids = catalogo.ids_del_contenido([cripta.contenidos[pos]])
            if modo != "un id por solicitud":
                catalogo.resolver(ids)
            for i in ids:
                if i not in vistos:
                    vistos.append(i)
                    if modo == "un id por solicitud":
                        fuente.sol_catalogo += 1
            pobladas[pos] = True

        actual = cripta.salas.buscar(1)
        entrar(actual)
        for _ in range(pasos):
            vecinas = [s.destino for s in actual.salidas]
            nuevas = [v for v in vecinas if not pobladas[cripta.salas.posicion(v.id)]]
            actual = azar.choice(nuevas if nuevas and azar.random() < 0.8 else vecinas)
            entrar(actual)
        return fuente.sol_catalogo, len(vistos)


def ejecutar():
    print(" Fichas del catálogo: solicitudes de red ")
    for lado, pasos, tipos in ESCENARIOS:
        print(f"\nCripta de {lado * lado} salas, {pasos} pasos, {tipos} tipos distintos "
              f"(promedio de {SEMILLAS} caminatas, caché de {CAPACIDAD}):")
        print(f"{'estrategia':>22}{'solicitudes de catálogo':>26}{'tipos vistos':>14}")
        for modo in MODOS:
            resultados = [caminata(lado, pasos, tipos, modo, s) for s in range(SEMILLAS)]
            print(f"{modo:>22}{sum(r[0] for r in resultados) / SEMILLAS:>26.1f}"
                  f"{sum(r[1] for r in resultados) / SEMILLAS:>14.1f}")
    print("\nun id por solicitud = una solicitud por cada tipo nuevo (alternativa descartada)")
    print("por sala           = resolver los ids de cada sala al entrar, en lotes de 10 (versión anterior)")
    print("por lote de salas  = resolver de una vez los ids de todas las salas que llegan en un lote (programa)")


if __name__ == "__main__":
    ejecutar()