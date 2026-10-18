from types import SimpleNamespace

from data.fabrica_modelo import FabricaModelo
from dto.ficha_dto import FichaEnemigoDTO, FichaTrampaDTO
from model.enemigo_logica import Enemigo
from model.jugador_logica import Jugador
from model.partida_logica import Partida
from model.sala_logica import Sala, Salida
from model.trampa_logica import Trampa

JSON_TRAMPA = {"id": "t1", "nombre": "Pinchos", "clase": "trampa", "daño": 5, "rearme": 100}
JSON_ENEMIGO = {"id": "rata", "nombre": "Rata", "clase": "enemigo", "vida_max": 10, "ataque": 2,
                "defensa": 0, "velocidad": 100, "comportamiento": "guardian"}


def fabrica():
    return FabricaModelo(repo_cripta=None, repo_catalogo=None)


def instancia(tipo):
    return SimpleNamespace(instancia="x1", tipo=tipo, vida=None)


def test_ficha_de_trampa_sin_veneno_no_envenena():
    ficha = FichaTrampaDTO.desde_json(JSON_TRAMPA)
    assert ficha.veneno == 0
    assert fabrica().crear_trampa(instancia("t1"), ficha).veneno == 0


def test_ficha_de_trampa_con_veneno_lo_pasa_a_la_trampa():
    ficha = FichaTrampaDTO.desde_json({**JSON_TRAMPA, "veneno": 3})
    trampa = fabrica().crear_trampa(instancia("t1"), ficha)
    assert trampa.veneno == 3


def test_ficha_de_enemigo_sin_regeneracion_no_se_regenera():
    ficha = FichaEnemigoDTO.desde_json(JSON_ENEMIGO)
    assert ficha.regeneracion == 0
    assert fabrica().crear_enemigo(instancia("rata"), ficha, Sala(1, "S")).regeneracion == 0


def test_ficha_de_enemigo_con_regeneracion_se_la_pasa_al_enemigo():
    ficha = FichaEnemigoDTO.desde_json({**JSON_ENEMIGO, "regeneracion": 2})
    enemigo = fabrica().crear_enemigo(instancia("rata"), ficha, Sala(1, "S"))
    assert enemigo.regeneracion == 2


def partida_con_trampa(veneno):
    inicio, destino = Sala(1, "Inicio"), Sala(2, "Trampa")
    inicio.agregar_salida(Salida("N", destino))
    destino.agregar_salida(Salida("S", inicio))
    destino.trampas.append(Trampa("t1", "Pinchos", dano=1, rearme=100, veneno=veneno))
    jugador = Jugador(vida_max=100, ataque=5, defensa=0, velocidad=100, inventario_max=5, sala=inicio)
    return Partida(jugador, semilla=1), jugador


def test_trampa_con_veneno_deja_al_jugador_envenenado():
    partida, jugador = partida_con_trampa(veneno=2)
    partida.mover("N")
    assert jugador.efecto_activo("veneno") is not None


def test_trampa_sin_veneno_no_envenena():
    partida, jugador = partida_con_trampa(veneno=0)
    partida.mover("N")
    assert jugador.efecto_activo("veneno") is None


def test_enemigo_con_regeneracion_recupera_vida_con_el_tiempo():
    sala = Sala(1, "S")
    jugador = Jugador(vida_max=100, ataque=5, defensa=0, velocidad=100, inventario_max=5, sala=sala)
    partida = Partida(jugador, semilla=1)
    enemigo = Enemigo("r1", "rata", "Rata", 3, 10, 0, 0, 100, "guardian", sala)
    enemigo.regeneracion = 2
    partida.activar_enemigo(enemigo)
    for _ in range(40):
        partida.esperar_jugador()
    assert enemigo.vida > 3


def test_enemigo_sin_regeneracion_no_recupera_vida():
    sala = Sala(1, "S")
    jugador = Jugador(vida_max=100, ataque=5, defensa=0, velocidad=100, inventario_max=5, sala=sala)
    partida = Partida(jugador, semilla=1)
    enemigo = Enemigo("r1", "rata", "Rata", 3, 10, 0, 0, 100, "guardian", sala)
    partida.activar_enemigo(enemigo)
    for _ in range(40):
        partida.esperar_jugador()
    assert enemigo.vida == 3