from adapters.almacen_fichas import AlmacenFichas
from adapters.bitacora_partida import BitacoraPartida
from adapters.cliente_http import ClienteHttp
from controller.controlador import Controlador
from data.fabrica_modelo import FabricaModelo
from data.repositorio_catalogo import RepositorioCatalogo
from data.repositorio_cripta import RepositorioCripta
from service.juego_service import JuegoService
from structures.cache_fichas import CacheFichas
from view.vista_consola import VistaConsola
from view.vista_replay import VistaReplay

## La clase aplicacion arma todas las piezas y permite jugar o reproducir una partida grabada.
class Aplicacion:
    def __init__(self, url, id_cripta, tamano_cache=25):
        self.cliente = ClienteHttp(url, timeout=10)
        self.repo_cripta = RepositorioCripta(self.cliente)
        dtos = self.repo_cripta.cargar_esqueleto(id_cripta)
        self.repo_cripta.construir_salas(dtos)

        self.cache = CacheFichas(tamano_cache)
        self.repo_catalogo = RepositorioCatalogo(
            self.cliente, self.cache, AlmacenFichas("cache/catalogo"))
        self.fabrica = FabricaModelo(self.repo_cripta, self.repo_catalogo)
        self.fabrica.iniciar()

    def jugar(self):
        partida = self.fabrica.nueva_partida(semilla=None)
        servicio = JuegoService(partida, self.fabrica)
        vista = VistaConsola()
        bitacora = BitacoraPartida("saves")
        controlador = Controlador(servicio, vista, bitacora)
        datos = self.repo_cripta.datos
        try:
            bitacora.abrir(datos.id_cripta, datos.version,
                           self.repo_catalogo.version(), partida.semilla)
            controlador.jugar()
        finally:
            self._reportar(vista)
            bitacora.cerrar()

    def reproducir(self, lector):
        vista = VistaReplay()
        if not self._versiones_coinciden(lector):
            vista.mostrar_mensaje(
                "No se puede garantizar una reproducción fiel: la versión de la "
                "cripta o del catálogo cambió desde que se grabó el log.")
            return

        partida = self.fabrica.nueva_partida(semilla=lector.semilla)
        servicio = JuegoService(partida, self.fabrica)
        controlador = Controlador(servicio, vista, None)
        for comando in lector.comandos:
            if partida.terminada:
                break
            controlador.ejecutar(comando)
        vista.mostrar_final(partida)

    def _versiones_coinciden(self, lector):
        return (lector.version_cripta == self.repo_cripta.datos.version
                and lector.version_catalogo == self.repo_catalogo.version())

    def _reportar(self, vista):
        vista.mostrar_reporte(
            self.cliente.solicitudes, self.cliente.presupuesto,
            self.repo_catalogo.aciertos_memoria + self.repo_catalogo.aciertos_disco,
            self.repo_catalogo.fallos)