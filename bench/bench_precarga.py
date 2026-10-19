import random

from data.repositorio_cripta import RepositorioCripta
from dto.sala_dto import SalaDTO

ESCENARIOS = ((10, 150), (30, 40), (30, 200), (50, 100))      # (lado de la cuadrícula, pasos de la caminata)
SEMILLAS = 20
ESTRATEGIAS = ("una sala por solicitud", "anticipar en cada sala", "lote solo si falta")


class FuenteContadora:
    """Responde como el servicio de contenido, pero solo cuenta las solicitudes."""

    def __init__(self):
        self.solicitudes = 0
        self.salas_pedidas = 0

    def contenido_salas(self, id_cripta, ids):
        self.solicitudes += 1
        self.salas_pedidas += len(ids)
        return {"contenido": [{"sala": i, "enemigos": [], "objetos": [], "trampas": []}
                              for i in ids]}


class DatosFalsos:
    id_cripta = "bench"


def cuadricula(lado):
    dtos = []
    for f in range(lado):
        for c in range(lado):
            i = f * lado + c + 1
            salidas = {}
            if f > 0:
                salidas["N"] = {"sala": i - lado}
            if f < lado - 1:
                salidas["S"] = {"sala": i + lado}
            if c > 0:
                salidas["O"] = {"sala": i - 1}
            if c < lado - 1:
                salidas["E"] = {"sala": i + 1}
            dtos.append(SalaDTO.desde_json({"id": i, "nombre": f"s{i}", "salidas": salidas}))
    return dtos


def caminata(lado, pasos, estrategia, semilla):
    fuente = FuenteContadora()
    repo = RepositorioCripta(fuente)
    repo.datos = DatosFalsos()
    repo.construir_salas(cuadricula(lado))
    azar = random.Random(semilla)
    pobladas = [False] * repo.salas.tamano()
    visitadas = [0]

    def entrar(sala):
        pos = repo.salas.posicion(sala.id)
        if pobladas[pos]:
            return
        visitadas[0] += 1
        if estrategia == "anticipar en cada sala":
            repo.precargar(sala.id)
        elif repo.contenidos[pos] is None:
            if estrategia == "lote solo si falta":
                repo.precargar(sala.id)
            else:
                fuente.solicitudes += 1
                fuente.salas_pedidas += 1
                repo.contenidos[pos] = True
        pobladas[pos] = True

    actual = repo.salas.buscar(1)
    entrar(actual)
    for _ in range(pasos):
        vecinas = [s.destino for s in actual.salidas]
        nuevas = [v for v in vecinas if not pobladas[repo.salas.posicion(v.id)]]
        actual = azar.choice(nuevas if nuevas and azar.random() < 0.8 else vecinas)
        entrar(actual)
    return fuente.solicitudes, fuente.salas_pedidas, visitadas[0]


def ejecutar():
    print(" Contenido de salas: solicitudes de red ")
    for lado, pasos in ESCENARIOS:
        print(f"\nCripta de {lado * lado} salas, caminata de {pasos} pasos "
              f"(promedio de {SEMILLAS} caminatas):")
        print(f"{'estrategia':>24}{'solicitudes':>13}{'salas pedidas':>15}{'salas visitadas':>17}")
        for estrategia in ESTRATEGIAS:
            total = [0, 0, 0]
            for semilla in range(SEMILLAS):
                r = caminata(lado, pasos, estrategia, semilla)
                for k in range(3):
                    total[k] += r[k]
            print(f"{estrategia:>24}{total[0] / SEMILLAS:>13.1f}"
                  f"{total[1] / SEMILLAS:>15.1f}{total[2] / SEMILLAS:>17.1f}")
    print("\nuna sala por solicitud = pedir solo la sala a la que se entra (alternativa descartada)")
    print("anticipar en cada sala = lote de 10 salas cercanas al entrar a cada sala nueva (versión anterior)")
    print("lote solo si falta     = lote de 10 salas cercanas, solo cuando falta la sala de destino (programa)")
    print("Cada solicitud real cuesta ~400 ms de red y una unidad del presupuesto.")


if __name__ == "__main__":
    ejecutar()