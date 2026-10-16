from dto.ficha_dto import FichaEnemigoDTO, ficha_desde_json
from structures.vector_busqueda import VectorBusqueda
from adapters.almacen_fichas import AlmacenFichas

class RepositorioCatalogo:
    def __init__(self, fuente, cache, almacen):
        self.fuente = fuente
        self.cache = cache
        self.almacen = almacen
        self._validado = False
        self.aciertos_memoria = 0
        self.aciertos_disco = 0
        self.fallos = 0

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
        vistos = VectorBusqueda()
        pendientes = VectorBusqueda()
        for id_ficha in ids:
            if vistos.buscar(id_ficha) is not None:
                continue
            vistos.insertar_ordenado(id_ficha, id_ficha)
            if self.cache.contiene(id_ficha):
                self.aciertos_memoria += 1
            else:
                pendientes.insertar_ordenado(id_ficha, id_ficha)
        return pendientes.ids

    def resolver(self, ids, con_sueltos=True):
        if not self._validado:
            self.validar_version()

        pendientes = self.faltantes(ids)
        resueltas = []

        del_disco = []
        para_red = []
        for id_ficha in pendientes:
            json_ficha = self.almacen.leer(id_ficha)
            if json_ficha is not None:
                del_disco.append(json_ficha)
                self.aciertos_disco += 1
            else:
                para_red.append(id_ficha)
                self.fallos += 1

        for json_ficha in del_disco:
            ficha = ficha_desde_json(json_ficha)
            self.cache.insertar(ficha.id_ficha, ficha)
            resueltas.append(ficha)

        i = 0
        while i < len(para_red):
            lote = para_red[i: i + 10]
            for ficha in self._traer(lote):
                self.cache.insertar(ficha.id_ficha, ficha)
                resueltas.append(ficha)
            i += 10

        sueltos = []
        for ficha in resueltas:
            if isinstance(ficha, FichaEnemigoDTO):
                sueltos.extend(ficha.suelta)
        if con_sueltos and sueltos:
            resueltas.extend(self.resolver(sueltos, con_sueltos=False))

        return resueltas

    def validar_version(self):
        version_servidor = self.fuente.version_catalogo()["version"]
        if self.almacen.version() != version_servidor:
            self.almacen.borrar_todo()
            self.almacen.guardar_version(version_servidor)
        self._validado = True

    def _traer(self, lote):
        fichas = []
        respuesta = self.fuente.catalogo(lote)
        for elemento in respuesta["entidades"]:
            ficha = ficha_desde_json(elemento)
            self.almacen.guardar(elemento["id"], elemento)
            fichas.append(ficha)
        if len(fichas) != len(lote):
            raise ValueError("llegaron menos fichas de las pedidas")
        return fichas






