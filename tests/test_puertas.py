from model.sala_logica import Sala, Salida
from model.jugador_logica import Jugador
from model.objeto_logica import Objeto
from model.partida_logica import Partida

def test_puerta_se_abre_y_cierra_por_los_dos_lados():
    sala1, sala2 = Sala(1, "A"), Sala(2, "B")
    ida = Salida("E", sala2, cerrada=True, llave="llave1", cierre_automatico=100)
    vuelta = Salida("O", sala1)
    sala1.agregar_salida(ida)
    sala2.agregar_salida(vuelta)
    ida.enlazar(vuelta)
    assert vuelta.cerrada is True and vuelta.llave == "llave1"

    jugador = Jugador(vida_max=50, ataque=10, defensa=0, velocidad=100, inventario_max=10, sala=sala1)
    jugador.inventario.agregar(Objeto(1, "llave1", "Llave", "llave", 1, 1))
    partida = Partida(jugador, semilla=1)

    partida.abrir_puerta("E")
    assert ida.cerrada is False and vuelta.cerrada is False

    partida.esperar_jugador()
    partida.esperar_jugador()
    assert ida.cerrada is True and vuelta.cerrada is True
