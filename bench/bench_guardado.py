import json
import os
import random
import tempfile
import time

from adapters.guardado_binario import CabeceraGuardado, GuardadoBinario
from dto.contenido_sala_dto import (ContenidoSalaDTO, InstanciaEnemigoDTO,
                                    InstanciaTrampaDTO)

TAMANOS = (50, 500, 5000)       # salas con contenido en el guardado
COMANDOS = 1000
TANDAS = 5
TIPOS_ENEMIGO = ("rata_gigante", "esqueleto", "goblin", "ent_guardian")
TIPOS_OBJETO = ("daga", "llave_bronce", "pocion_curacion", "antorcha", "coraza")
TIPOS_TRAMPA = ("trp_dardos", "trp_pinchos")

def armar_salas(n, azar):
    salas = []
    for i in range(1, n + 1):
        enemigos = []
        for j in range(azar.randint(0, 3)):
            vida = azar.choice((None, 12, 20))
            enemigos.append(InstanciaEnemigoDTO(
                f"e-{i}-{j}", azar.choice(TIPOS_ENEMIGO), vida))
        objetos = [azar.choice(TIPOS_OBJETO) for _ in range(azar.randint(0, 4))]
        trampas = [InstanciaTrampaDTO(f"t-{i}-{j}", azar.choice(TIPOS_TRAMPA))
                   for j in range(azar.randint(0, 2))]
        salas.append(ContenidoSalaDTO(i, enemigos, objetos, trampas))
    return salas

def a_json(dto):
    return {
        "sala": dto.id_sala,
        "enemigos": [{"instancia": e.instancia, "tipo": e.tipo, "vida": e.vida}
                     for e in dto.enemigos],
        "objetos": list(dto.objetos),
        "trampas": [{"instancia": t.instancia, "tipo": t.tipo}
                    for t in dto.trampas],
    }


def mejor_ms(funcion, repeticiones):
    mejor = None
    for _ in range(TANDAS):
        inicio = time.perf_counter()
        for _ in range(repeticiones):
            funcion()
        promedio = (time.perf_counter() - inicio) / repeticiones * 1000
        if mejor is None or promedio < mejor:
            mejor = promedio
    return mejor


def repeticiones_para(n):
    return max(3, 3000 // n)


# ---------- binario ----------
def guardar_binario(g, ruta, salas, comandos):
    cab = CabeceraGuardado("cripta-bench", "v1", "cat-1", 12345)
    g.guardar(ruta, cab, salas, comandos)


def cargar_todo_binario(g, ruta):
    with open(ruta, "rb") as archivo:
        cab = g.leer_cabecera(archivo)
        indice = g.leer_indice(archivo, cab)
        resultado = []
        for i in range(indice.tamano()):
            offset = indice.valores[i][0]
            archivo.seek(offset)
            resultado.append(g.leer_sala(archivo))
        return resultado


# ---------- JSON (alternativa descartada) ----------
def guardar_json(ruta, salas, comandos):
    datos = {"semilla": 12345, "salas": [a_json(s) for s in salas],
             "comandos": comandos}
    with open(ruta, "w", encoding="utf-8") as archivo:
        json.dump(datos, archivo)


def cargar_todo_json(ruta):
    with open(ruta, "r", encoding="utf-8") as archivo:
        datos = json.load(archivo)
    return [ContenidoSalaDTO.desde_json(s) for s in datos["salas"]]


def una_sala_json(ruta, id_sala):
    with open(ruta, "r", encoding="utf-8") as archivo:
        datos = json.load(archivo)
    for s in datos["salas"]:
        if s["sala"] == id_sala:
            return ContenidoSalaDTO.desde_json(s)
    return None


def ejecutar():
    g = GuardadoBinario()
    azar = random.Random(7)
    comandos = [azar.choice(("w", "a", "s", "d", "esperar", "atacar 1", "sig"))
                for _ in range(COMANDOS)]
    print("Guardado binario vs JSON (misma información en ambos)")
    print(f"{'salas':>6} | {'tamaño bin':>10} {'tamaño json':>11} | "
          f"{'guardar bin':>11} {'guardar json':>12} | "
          f"{'cargar todo bin':>15} {'cargar todo json':>16} | "
          f"{'1 sala bin':>10} {'1 sala json':>11}")
    with tempfile.TemporaryDirectory() as carpeta:
        ruta_bin = os.path.join(carpeta, "partida.sav")
        ruta_json = os.path.join(carpeta, "partida.json")
        for n in TAMANOS:
            salas = armar_salas(n, azar)
            objetivo = salas[n // 2].id_sala
            reps = repeticiones_para(n)

            t_g_bin = mejor_ms(lambda: guardar_binario(g, ruta_bin, salas, comandos), reps)
            t_g_json = mejor_ms(lambda: guardar_json(ruta_json, salas, comandos), reps)
            t_c_bin = mejor_ms(lambda: cargar_todo_binario(g, ruta_bin), reps)
            t_c_json = mejor_ms(lambda: cargar_todo_json(ruta_json), reps)
            t_1_bin = mejor_ms(lambda: g.leer_sala_de(ruta_bin, objetivo), reps * 5)
            t_1_json = mejor_ms(lambda: una_sala_json(ruta_json, objetivo), reps)

            assert cargar_todo_binario(g, ruta_bin)[n // 2].id_sala == objetivo
            assert g.leer_sala_de(ruta_bin, objetivo).id_sala == objetivo
            tam_bin = os.path.getsize(ruta_bin) / 1024
            tam_json = os.path.getsize(ruta_json) / 1024
            print(f"{n:>6} | {tam_bin:>8.1f}KB {tam_json:>9.1f}KB | "
                  f"{t_g_bin:>9.2f}ms {t_g_json:>10.2f}ms | "
                  f"{t_c_bin:>13.2f}ms {t_c_json:>14.2f}ms | "
                  f"{t_1_bin:>8.3f}ms {t_1_json:>9.3f}ms")


if __name__ == "__main__":
    ejecutar()