## Este main es provisional, solo para probar la vista y el controlador

import sys

from adapters.bitacora_partida import BitacoraPartida
from adapters.cliente_http import ClienteHttp
from adapters.almacen_fichas import AlmacenFichas
from data.repositorio_cripta import RepositorioCripta
from data.repositorio_catalogo import RepositorioCatalogo
from data.fabrica_modelo import FabricaModelo
from structures.cache_fichas import CacheFichas
from service.juego_service import JuegoService
from view.vista_consola import VistaConsola
from controller.controlador import Controlador


URL = "https://cripta-api.kad06a0zhgs84.us-east-2.cs.amazonlightsail.com/v1"

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Uso: python main.py <ID_CRIPTA>")
        sys.exit(1)

    ID_CRIPTA = sys.argv[1]

    cliente = ClienteHttp(URL, timeout=10)
    repo_cripta = RepositorioCripta(cliente)
    dtos = repo_cripta.cargar_esqueleto(ID_CRIPTA)
    repo_cripta.construir_salas(dtos)

    cache = CacheFichas(25)
    repo_catalogo = RepositorioCatalogo(cliente, cache, AlmacenFichas("cache/catalogo"))
    fabrica = FabricaModelo(repo_cripta, repo_catalogo)
    fabrica.iniciar()

    partida = fabrica.nueva_partida(semilla=None)
    bitacora = BitacoraPartida("saves")
    servicio = JuegoService(partida, fabrica)
    vista = VistaConsola()
    try:
        bitacora.abrir(repo_cripta.datos.id_cripta, repo_cripta.datos.version, repo_catalogo.version(), partida.semilla)
        controlador = Controlador(servicio, vista, bitacora)
        controlador.jugar()
    finally:
        vista.mostrar_reporte(
            cliente.solicitudes, cliente.presupuesto,
            repo_catalogo.aciertos_memoria + repo_catalogo.aciertos_disco,
            repo_catalogo.fallos)
        bitacora.cerrar()
