from types import SimpleNamespace

from data.fabrica_modelo import FabricaModelo
from model.enemigo_logica import Enemigo
from model.jugador_logica import Jugador
from model.objeto_logica import Objeto, PERGAMINO_RETROCESO
from model.partida_logica import Partida
from model.sala_logica import Sala


def armar(suelta, creador=True):
    sala = Sala(1, "Sala")
    jugador = Jugador(vida_max=50, ataque=10, defensa=0, velocidad=100, inventario_max=10, sala=sala)
    partida = Partida(jugador, semilla=1)
    rata = Enemigo("r1", "rata", "Rata", 5, 5, 1, 0, 100, "guardian", sala, suelta=suelta)
    partida.activar_enemigo(rata)
    contador = [100]

    def crear(id_ficha):
        contador[0] += 1
        clase = PERGAMINO_RETROCESO if id_ficha == "perg" else "arma"
        return Objeto(contador[0], id_ficha, id_ficha.capitalize(), clase, 1, 1, ataque_bonus=1)

    if creador:
        partida.creador_objetos = crear
    return partida, sala, rata


def test_enemigo_suelta_sus_objetos_al_morir():
    partida, sala, rata = armar(["espada", "daga"])
    assert sala.objetos == []
    partida.atacar(rata)
    assert not rata.esta_vivo()
    assert [o.id_catalogo for o in sala.objetos] == ["espada", "daga"]


def test_enemigo_sin_suelta_no_deja_nada():
    partida, sala, rata = armar([])
    partida.atacar(rata)
    assert sala.objetos == []


def test_enemigo_creado_sin_suelta_usa_lista_vacia():
    sala = Sala(1, "Sala")
    rata = Enemigo("r1", "rata", "Rata", 5, 5, 1, 0, 100, "guardian", sala)
    assert rata.suelta == []


def test_retroceder_quita_el_botin_y_revive_al_enemigo():
    partida, sala, rata = armar(["espada", "perg"])
    partida.atacar(rata)
    assert len(sala.objetos) == 2
    partida.retroceder()
    assert sala.objetos == []
    assert rata.esta_vivo() and rata.vida == 5


def test_sin_creador_de_objetos_no_se_cae():
    partida, sala, rata = armar(["espada"], creador=False)
    partida.atacar(rata)
    assert not rata.esta_vivo() and sala.objetos == []


class CatalogoFalso:
    def __init__(self):
        self.guardadas = {}
        self.resueltas = []
        self.cache = SimpleNamespace(contiene=lambda i: i in self.guardadas,
                                     obtener=lambda i: self.guardadas.get(i))

    def resolver(self, ids, con_sueltos=True):
        for i in ids:
            self.resueltas.append(i)
            self.guardadas[i] = SimpleNamespace(id_ficha=i, nombre="Espada " + i, clase="arma",
                                                peso=3, valor=9, ataque_bonus=2)
        return []


def test_fabrica_crea_objetos_nuevos_con_ids_distintos_y_resuelve_si_falta():
    catalogo = CatalogoFalso()
    fabrica = FabricaModelo(repo_cripta=None, repo_catalogo=catalogo)
    a = fabrica.objeto_desde_id("e1")
    b = fabrica.objeto_desde_id("e1")
    assert a.id_instancia != b.id_instancia
    assert a.id_catalogo == "e1" and a.ataque_bonus == 2
    assert catalogo.resueltas == ["e1"]