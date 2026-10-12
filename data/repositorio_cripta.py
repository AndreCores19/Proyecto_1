from dto.datos_cripta_dto import DatosCriptaDTO
from dto.sala_dto import SalaDTO
from structures.vector_busqueda import VectorBusqueda
from model.sala_logica import Sala, Salida

## funcion auxiliar de direccion opuesta
def direccion_opuesta(direccion):
    if direccion == "N":
        return "S"
    if direccion == "S":
        return "N"
    if direccion == "E":
        return "O"
    if direccion == "O":
        return "E"
    return None



class RepositorioCripta:
    def __init__(self, fuente):
        self.fuente = fuente
        self.datos = None
        self.salas = None

    def cargar_datos(self, id_cripta):
        datos = DatosCriptaDTO.desde_json(self.fuente.datos_cripta(id_cripta))
        self.datos = datos
        self.fuente.asignar_presupuesto(datos.presupuesto_solicitudes)

    def cargar_esqueleto(self, id_cripta):
        if self.datos is None or self.datos.id_cripta != id_cripta:
            self.cargar_datos(id_cripta)

        salas = []
        for pagina in range(1, self.datos.paginas + 1):
            respuesta = self.fuente.pagina_esqueleto(id_cripta, pagina)
            for sala_json in respuesta["salas"]:
                salas.append(SalaDTO.desde_json(sala_json))

        return salas

    def construir_salas(self, dtos):
        vector = VectorBusqueda()
        creadas = []

        ## Primer ciclo crea las salas
        for dto in dtos:
            sala = Sala(dto.id_sala, dto.nombre)
            vector.agregar(dto.id_sala, sala)
            creadas.append(sala)
        vector.preparar()

        ## Segundo ciclo enlaza las salidas
        for i in range(len(dtos)):
            dto = dtos[i]
            origen = creadas[i]
            for salida_dto in dto.salidas:
                destino = vector.buscar(salida_dto.id_destino)
                if destino is None:
                    raise ValueError(
                        f"La sala {dto.id_sala} tiene una salida {salida_dto.direccion} "
                        f"hacia la sala {salida_dto.id_destino}, que no existe"
                    )
                origen.agregar_salida(Salida(
                    direccion=salida_dto.direccion,
                    destino=destino,
                    cerrada=salida_dto.cerrada,
                    llave=salida_dto.llave,
                    cierre_automatico=salida_dto.cierre_automatico,
                ))
        ## tercer ciclo enlaza las salidas opuestas
        for sala in creadas:
            for salida in sala.salidas:
                if salida.opuesta is not None:
                    continue
                otra = salida.destino.salida(direccion_opuesta(salida.direccion))
                if otra is None:
                    continue
                if otra.destino is not sala:
                    raise ValueError(
                        f"Puerta inconsistente: la salida {salida.direccion} de la sala "
                        f"{sala.id} va a la sala {salida.destino.id}, pero la salida "
                        f"{otra.direccion} de esa sala va a la sala {otra.destino.id}"
                    )
                salida.enlazar(otra)
        self.salas = vector
