from adapters.cliente_http import ClienteHttp
from adapters.almacen_fichas import AlmacenFichas
from data.repositorio_cripta import RepositorioCripta
from data.repositorio_catalogo import RepositorioCatalogo
from data.fabrica_modelo import FabricaModelo
from structures.cache_fichas import CacheFichas

URL = "https://cripta-api.kad06a0zhgs84.us-east-2.cs.amazonlightsail.com/v1"

cliente = ClienteHttp(URL, timeout=10)
repo_cripta = RepositorioCripta(cliente)
dtos = repo_cripta.cargar_esqueleto("cripta-02")
repo_cripta.construir_salas(dtos)

repo_catalogo = RepositorioCatalogo(cliente, CacheFichas(25), AlmacenFichas("cache/catalogo"))
fabrica = FabricaModelo(repo_cripta, repo_catalogo)
fabrica.iniciar()

# 1. Poblar la sala inicial
sala = repo_cripta.salas.buscar(repo_cripta.datos.sala_inicial)
fabrica.poblar_sala(sala)
print("Sala", sala.id, sala.nombre)
print("  objetos:", [(o.id_instancia, o.id_catalogo, o.clase) for o in sala.objetos])
print("  enemigos inactivos:", [(e.id_enemigo, e.tipo, e.vida, e.vida_max) for e in sala.inactivos])
print("  trampas:", [(t.id_instancia, t.nombre, t.dano, t.rearme) for t in sala.trampas])
antes = cliente.solicitudes_realizadas()
print("Solicitudes:", antes)

# 2. Poblarla otra vez no debe duplicar nada ni gastar solicitudes
cantidad = len(sala.objetos) + len(sala.inactivos) + len(sala.trampas)
fabrica.poblar_sala(sala)
assert len(sala.objetos) + len(sala.inactivos) + len(sala.trampas) == cantidad, "se duplicó el contenido"
assert cliente.solicitudes_realizadas() == antes, "gastó solicitudes de más"
print("OK: segunda llamada no duplica ni gasta solicitudes")

# 3. Poblar todas las salas
for id_sala in range(1, 21):
    fabrica.poblar_sala(repo_cripta.salas.buscar(id_sala))
print("\nTodas las salas:")
for id_sala in range(1, 21):
    s = repo_cripta.salas.buscar(id_sala)
    print(s.id, s.nombre,
          "| objetos:", len(s.objetos),
          "| enemigos:", len(s.inactivos),
          "| trampas:", len(s.trampas))

# 4. Los id_instancia de los objetos no se repiten
ids = []
for id_sala in range(1, 21):
    for o in repo_cripta.salas.buscar(id_sala).objetos:
        ids.append(o.id_instancia)
assert len(ids) == len(set(ids)), "hay id_instancia repetidos"   # set solo en la prueba
print("OK: id_instancia únicos:", ids)
print("Solicitudes totales:", cliente.solicitudes_realizadas())