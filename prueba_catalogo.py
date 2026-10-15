from dto.contenido_sala_dto import ContenidoSalaDTO
from dto.ficha_dto import FichaEnemigoDTO
from structures.cache_fichas import CacheFichas
from data.repositorio_catalogo import RepositorioCatalogo

# ---- Datos reales de cripta-01 (contenido de las 6 salas y catálogo) ----
CONTENIDO = [
    {'sala': 1, 'enemigos': [], 'objetos': ['itm_antorcha'], 'trampas': []},
    {'sala': 2, 'enemigos': [{'instancia': 'e-201', 'tipo': 'ent_rata_gigante', 'vida': 12},
                             {'instancia': 'e-202', 'tipo': 'ent_rata_gigante', 'vida': 12}],
     'objetos': ['itm_daga_oxidada'], 'trampas': [{'instancia': 't-17', 'tipo': 'trp_dardos'}]},
    {'sala': 3, 'enemigos': [], 'objetos': ['itm_llave_bronce', 'itm_coraza_cuero'], 'trampas': []},
    {'sala': 4, 'enemigos': [{'instancia': 'e-401', 'tipo': 'ent_guardian_oseo'}],
     'objetos': ['itm_llave_negra', 'itm_pocion_menor'], 'trampas': []},
    {'sala': 5, 'enemigos': [], 'objetos': ['itm_pergamino_retroceso', 'itm_antidoto'], 'trampas': []},
    {'sala': 6, 'enemigos': [], 'objetos': [], 'trampas': []},
]
CATALOGO = [
    {'id': 'itm_antorcha', 'nombre': 'Antorcha', 'clase': 'antorcha', 'peso': 1, 'valor': 5, 'duracion': 2000},
    {'id': 'itm_pergamino_retroceso', 'nombre': 'Pergamino de retroceso', 'clase': 'pergamino_retroceso', 'peso': 1, 'valor': 40},
    {'id': 'ent_rata_gigante', 'nombre': 'Rata gigante', 'clase': 'enemigo', 'vida_max': 12, 'ataque': 5,
     'defensa': 1, 'velocidad': 150, 'comportamiento': 'rastreador', 'suelta': ['itm_pocion_menor']},
    {'id': 'itm_llave_bronce', 'nombre': 'Llave de bronce', 'clase': 'llave', 'peso': 1, 'valor': 0, 'abre': 'puerta_2_7'},
    {'id': 'ent_guardian_oseo', 'nombre': 'Guardián óseo', 'clase': 'enemigo', 'vida_max': 20, 'ataque': 7,
     'defensa': 3, 'velocidad': 90, 'comportamiento': 'guardián'},
    {'id': 'itm_llave_negra', 'nombre': 'Llave negra', 'clase': 'llave', 'peso': 1, 'valor': 0, 'abre': 'puerta_salida'},
    {'id': 'itm_daga_oxidada', 'nombre': 'Daga oxidada', 'clase': 'arma', 'peso': 2, 'valor': 15, 'ataque_bonus': 3},
    {'id': 'itm_pocion_menor', 'nombre': 'Poción menor', 'clase': 'pocion', 'peso': 1, 'valor': 10, 'cura': 8},
    {'id': 'itm_antidoto', 'nombre': 'Antídoto', 'clase': 'antidoto', 'peso': 1, 'valor': 12},
    {'id': 'itm_coraza_cuero', 'nombre': 'Coraza de cuero', 'clase': 'armadura', 'peso': 4, 'valor': 20, 'defensa_bonus': 2},
    {'id': 'trp_dardos', 'nombre': 'Dardos en la pared', 'clase': 'trampa', 'daño': 4, 'rearme': 300},
]


class FuenteFalsa:
    """Imita el método catalogo() del cliente, sin red, y anota cada lote pedido."""
    def __init__(self):
        self.lotes = []

    def catalogo(self, ids):
        self.lotes.append(list(ids))
        return {"entidades": [f for f in CATALOGO if f["id"] in ids]}


contenidos = [ContenidoSalaDTO.desde_json(c) for c in CONTENIDO]

# 1. ids_del_contenido: con repetidos
fuente = FuenteFalsa(); cache = CacheFichas(25)
repo = RepositorioCatalogo(fuente, cache)
ids = repo.ids_del_contenido(contenidos)
assert len(ids) == 12 and ids.count("ent_rata_gigante") == 2, ids
print("OK   ids_del_contenido:", len(ids), "ids (con repetidos)")

# 2. faltantes: sin repetidos y ordenados
f = repo.faltantes(ids)
assert len(f) == 11 and f == sorted(f), f
print("OK   faltantes con caché vacía:", len(f))

# 3. faltantes: descarta los que ya están en caché
cache.insertar("itm_antorcha", "x")
cache.insertar("trp_dardos", "x")
assert len(repo.faltantes(ids)) == 9
print("OK   faltantes con 2 en caché: 9")

# 4. resolver: 11 fichas en 2 lotes (10 y 1)
fuente = FuenteFalsa(); cache = CacheFichas(25)
repo = RepositorioCatalogo(fuente, cache)
fichas = repo.resolver(ids)
assert len(fichas) == 11 and [len(l) for l in fuente.lotes] == [10, 1], [len(l) for l in fuente.lotes]
assert cache.indice.ids == sorted(cache.indice.ids) and len(cache.indice.ids) == 11
assert isinstance(cache.indice.buscar("ent_rata_gigante").ficha, FichaEnemigoDTO)
print("OK   resolver: 11 fichas, lotes", [len(l) for l in fuente.lotes])

# 5. resolver otra vez: ya está todo, no pide nada
fichas = repo.resolver(ids)
assert fichas == [] and len(fuente.lotes) == 2
print("OK   resolver repetido: 0 solicitudes nuevas")

# 6. segunda ronda: la poción que suelta la rata no estaba en el contenido
fuente = FuenteFalsa(); cache = CacheFichas(25)
repo = RepositorioCatalogo(fuente, cache)
fichas = repo.resolver(["ent_rata_gigante"])
assert [f.id_ficha for f in fichas] == ["ent_rata_gigante", "itm_pocion_menor"], [f.id_ficha for f in fichas]
assert len(fuente.lotes) == 2
print("OK   segunda ronda: pidió la rata y luego la poción que suelta")

# 7. id inexistente: debe fallar con un error claro
fuente = FuenteFalsa(); cache = CacheFichas(25)
repo = RepositorioCatalogo(fuente, cache)
try:
    repo.resolver(["itm_no_existe"])
    raise AssertionError("debía lanzar ValueError")
except ValueError as e:
    print("OK   id inexistente ->", e)

print("\nTodas las pruebas sin red pasaron")