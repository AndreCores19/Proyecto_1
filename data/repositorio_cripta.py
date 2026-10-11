from dto.sala_dto import SalaDTO


class RepositorioCripta:
    def __init__(self, fuente):
        self.fuente = fuente
        self.datos = None
        self.salas = None

    def cargar_datos(self, id_cripta):
        datos = self.fuente.obtener_datos_cripta(id_cripta)
        self.datos = datos
        self.salas = {sala_datos["id"]: SalaDTO.desde_json(sala_datos)
                      for sala_datos in datos.get("salas", [])}