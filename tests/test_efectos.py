from model.jugador_logica import Jugador
from model.sala_logica import Sala, Salida
from model.partida_logica import Partida

from model.trampa_logica import Trampa



def armar_mundo():
    """Dos salas conectadas: la 1 al norte de la 2. Trampa en la 2."""
    sala1 = Sala(1, "Entrada")
    sala2 = Sala(2, "Pasillo")
    sala1.agregar_salida(Salida("N", sala2))
    sala2.agregar_salida(Salida("S", sala1))
    jugador = Jugador(vida_max=50, ataque=10, defensa=0, velocidad=100, sala=sala1)
    return sala1, sala2, jugador


def test_trampa_quita_vida_y_se_desarma():
    sala1, sala2, jugador = armar_mundo()
    trampa = Trampa("pinchos", "Pinchos", dano=10, rearme=300)
    sala2.trampas.append(trampa)
    partida = Partida(jugador, semilla=1)

    partida.mover("N")

    assert jugador.vida == 40
    assert trampa.armada is False

def test_trampa_con_veneno_sigue_danando():
    sala1, sala2, jugador = armar_mundo()
    sala2.trampas.append(Trampa("pinchos", "Pinchos", dano=10, rearme=300, veneno=5))
    partida = Partida(jugador, semilla=1)

    partida.mover("N")
    assert jugador.efecto_activo("veneno") is not None
    assert jugador.vida == 35

    partida.esperar_jugador()
    assert jugador.vida == 30

def test_retroceder_deshace_trampa_y_veneno():
    sala1, sala2, jugador = armar_mundo()
    trampa = Trampa("pinchos", "Pinchos", dano=10, rearme=300, veneno=5)
    sala2.trampas.append(trampa)
    partida = Partida(jugador, semilla=1)

    partida.mover("N")
    partida.esperar_jugador()
    assert jugador.vida == 30

    partida.retroceder()                 # deshace la espera
    assert jugador.vida == 35
    assert partida.reloj == 100
    assert jugador.efecto_activo("veneno") is not None

    partida.retroceder()                 # deshace el movimiento completo
    assert jugador.vida == 50
    assert partida.reloj == 0
    assert jugador.efecto_activo("veneno") is None
    assert jugador.sala is sala1
    assert trampa.armada is True

from model.objeto_logica import Objeto


def test_pocion_de_velocidad_sube_y_luego_termina():
    sala1 = Sala(1, "Entrada")
    jugador = Jugador(vida_max=50, ataque=10, defensa=0, velocidad=100, sala=sala1)
    pocion = Objeto(1, "pocion_vel", "Poción de velocidad", "pocion", 1, 10,
                    modificador_velocidad=100, duracion=300)
    jugador.inventario.agregar(pocion)
    partida = Partida(jugador, semilla=1)

    partida.usar()
    assert jugador.velocidad == 200
    assert jugador.efecto_activo("velocidad") is not None
    assert len(jugador.inventario.a_lista()) == 0     # la poción se consumió

    while partida.reloj < 300:
        partida.esperar_jugador()

    assert jugador.velocidad == 100                   # el efecto terminó
    assert jugador.efecto_activo("velocidad") is None

def test_antidoto_cancela_el_veneno():
    sala1, sala2, jugador = armar_mundo()
    sala2.trampas.append(Trampa("pinchos", "Pinchos", dano=10, rearme=300, veneno=5))
    jugador.inventario.agregar(Objeto(1, "antidoto", "Antídoto", "antidoto", 1, 10))
    partida = Partida(jugador, semilla=1)

    partida.mover("N")
    assert jugador.vida == 35
    assert jugador.efecto_activo("veneno") is not None

    partida.usar()                                    # usa el antídoto
    assert jugador.efecto_activo("veneno") is None

    partida.esperar_jugador()
    partida.esperar_jugador()
    assert jugador.vida == 35                         # ya no pierde vida

    partida.retroceder()
    partida.retroceder()
    partida.retroceder()                              # deshace también el uso del antídoto
    assert jugador.efecto_activo("veneno") is not None
    assert len(jugador.inventario.a_lista()) == 1     # el antídoto volvió al inventario

def test_antorcha_se_enciende_y_se_apaga():
    sala1 = Sala(1, "Entrada")
    jugador = Jugador(vida_max=50, ataque=10, defensa=0, velocidad=100, sala=sala1)
    jugador.inventario.agregar(Objeto(1, "antorcha", "Antorcha", "antorcha", 1, 5, duracion=300))
    partida = Partida(jugador, semilla=1)

    partida.usar()
    assert jugador.efecto_activo("antorcha") is not None

    partida.esperar_jugador()
    partida.esperar_jugador()
    assert jugador.efecto_activo("antorcha") is not None   # todavía no pasan 300

    while partida.reloj < 300:
        partida.esperar_jugador()
    assert jugador.efecto_activo("antorcha") is None       # ya se apagó

from model.enemigo_logica import Enemigo


def test_enemigo_se_regenera_sin_pasar_de_su_vida_maxima():
    sala1 = Sala(1, "Entrada")
    sala2 = Sala(2, "Lejana")
    jugador = Jugador(vida_max=50, ataque=10, defensa=0, velocidad=100, sala=sala1)
    goblin = Enemigo("g1", "goblin", "Goblin", 10, 30, 5, 0, 100, "estatico", sala2)
    goblin.regeneracion = 5
    partida = Partida(jugador, semilla=1)
    partida.activar_enemigo(goblin)

    for _ in range(3):
        partida.esperar_jugador()          # reloj = 300: pasó el pulso de t=200
    assert goblin.vida == 15

    for _ in range(2):
        partida.esperar_jugador()          # reloj = 500: pasó también el de t=400
    assert goblin.vida == 20

    for _ in range(20):
        partida.esperar_jugador()
    assert goblin.vida == 30               # nunca supera su vida máxima




