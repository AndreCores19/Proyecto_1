from adapters.cliente_http import ClienteHttp
from data.repositorio_cripta import RepositorioCripta
URL = "https://cripta-api.kad06a0zhgs84.us-east-2.cs.amazonlightsail.com/v1"


cliente = ClienteHttp(URL, timeout=10)
repo = RepositorioCripta(cliente)
dtos = repo.cargar_esqueleto("cripta-01")

repo.construir_salas(dtos)
for i in range(1, 7):
    sala = repo.salas.buscar(i)
    print(sala.id, sala.nombre)
    for s in sala.salidas:
        print("   ", s.direccion, "->", s.destino.nombre, "cerrada" if s.cerrada else "abierta")
        print(s.llave, s.cierre_automatico, "opuesta" if s.opuesta else "sin opuesta")

for s in dtos:
    print(s.id_sala, s.nombre, len(s.salidas))
print("Solicitudes:", cliente.solicitudes_realizadas())