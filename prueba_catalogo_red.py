from adapters.cliente_http import ClienteHttp
from dto.contenido_sala_dto import ContenidoSalaDTO
from structures.cache_fichas import CacheFichas
from data.repositorio_catalogo import RepositorioCatalogo

URL = "https://cripta-api.kad06a0zhgs84.us-east-2.cs.amazonlightsail.com/v1"

cliente = ClienteHttp(URL, timeout=10)
datos = cliente.datos_cripta("cripta-01")
cliente.asignar_presupuesto(datos["presupuesto_solicitudes"])
ids_salas = [s["id"] for s in cliente.pagina_esqueleto("cripta-01", 1)["salas"]]
respuesta = cliente.contenido_salas("cripta-01", ids_salas)
contenidos = [ContenidoSalaDTO.desde_json(c) for c in respuesta["contenido"]]

cache = CacheFichas(25)
repo = RepositorioCatalogo(cliente, cache)
ids = repo.ids_del_contenido(contenidos)

fichas = repo.resolver(ids)
print(len(fichas), cliente.solicitudes_realizadas())   # 11 fichas y 5 solicitudes

repo.resolver(ids)
print(cliente.solicitudes_realizadas())                # sigue en 5