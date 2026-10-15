from dto.ficha_dto import FichaEnemigoDTO, ficha_desde_json
from structures.vector_busqueda import VectorBusqueda
from structures.cache_fichas import CacheFichas


class RepositorioCatalogo:
    def __init__(self, fuente, cache):
        self.fuente = fuente
        self.cache = cache

    def ids_del_contenido(self, contenidos):
        ids = []
        for contenido in contenidos:
            for enemigo in contenido.enemigos:
                ids.append(enemigo.tipo)
            for trampa in contenido.trampas:
                ids.append(trampa.tipo)
            ids.extend(contenido.objetos)
        return ids

    def faltantes(self, ids):
        pendientes = VectorBusqueda()
        for id in ids:
            if self.cache.contiene(id):
                continue
            if pendientes.buscar(id) is not None:
                continue
            pendientes.agregar(id, id)
        return pendientes.ids

    def resolver(self, ids, con_sueltos=True):
        pendientes = self.faltantes(ids)
        resueltas = []
        i = 0
        while i < len(pendientes):
            lote = pendientes[i : i + 10]
            for ficha in self._traer(lote):
                self.cache.insertar(ficha.id_ficha, ficha)
                resueltas.append(ficha)
            i += 10

        sueltos = []
        for ficha in resueltas:
            if ficha is FichaEnemigoDTO:
                sueltos.extend(ficha.suelta)
        if sueltos is not None:
            resueltas.extend(self.resolver(sueltos, con_sueltos=False))

        return resueltas

    def _traer(self, lote):
        fichas = []
        respuesta = self.fuente.catalogo(lote)
        for elemento in respuesta["entidades"]:
            fichas.append(ficha_desde_json(elemento))
        if len(fichas) != len(lote):
            raise ValueError("llegaron menos fichas de las pedidas")
        return fichas






