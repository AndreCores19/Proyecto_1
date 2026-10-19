from dto.contenido_sala_dto import ContenidoSalaDTO
from dto.sala_dto import SalaDTO
from dto.salida_dto import SalidaDTO
from dto.datos_cripta_dto import DatosCriptaDTO, EstadisticasJugadorDTO
from dto.ficha_dto import FichaEnemigoDTO, FichaTrampaDTO, FichaArmaDTO, FichaPocionDTO, ficha_desde_json

salidas = {
    "S": {"sala": 1},
    "N": {"sala": 3},
    "E": {"sala": 5, "cerrada": True, "llave": "itm_llave_bronce", "cierre_automatico": 500},
}

for direccion, datos in salidas.items():
    s = SalidaDTO.desde_json(direccion, datos)
    print(s.direccion, s.id_destino, s.cerrada, s.llave, s.cierre_automatico)


sala_json = {
    "id": 2, "nombre": "Pasillo de las urnas",
    "salidas": {
        "S": {"sala": 1},
        "N": {"sala": 3},
        "E": {"sala": 5, "cerrada": True, "llave": "itm_llave_bronce", "cierre_automatico": 500},
    },
}

sala = SalaDTO.desde_json(sala_json)
print(sala.id_sala, sala.nombre, len(sala.salidas))
for s in sala.salidas:
    print(s.direccion, s.id_destino, s.cerrada)

respuesta_real = {
    "id": "cripta-01",
    "version": "fd58f7743b9e",
    "salas_total": 6,
    "paginas": 1,
    "salas_por_pagina": 50,
    "sala_inicial": 1,
    "sala_salida": 6,
    "llave_salida": "itm_llave_negra",
    "presupuesto_solicitudes": 9,
    "inventario_max": 10,
    "jugador": {"vida_max": 30, "ataque": 6, "defensa": 2, "velocidad": 100},
}

cripta = DatosCriptaDTO.desde_json(respuesta_real)
print(cripta.id_cripta, cripta.presupuesto_solicitudes, cripta.sala_inicial, cripta.jugador.vida_max)

sala2 = {'sala': 2,
         'enemigos': [{'instancia': 'e-201', 'tipo': 'ent_rata_gigante', 'vida': 12},
                      {'instancia': 'e-202', 'tipo': 'ent_rata_gigante', 'vida': 12}],
         'objetos': ['itm_daga_oxidada'],
         'trampas': [{'instancia': 't-17', 'tipo': 'trp_dardos'}]}

sala4 = {'sala': 4,
         'enemigos': [{'instancia': 'e-401', 'tipo': 'ent_guardian_oseo'}],
         'objetos': ['itm_llave_negra', 'itm_pocion_menor'],
         'trampas': []}

c2 = ContenidoSalaDTO.desde_json(sala2)
print(c2.id_sala, len(c2.enemigos), c2.objetos, c2.enemigos[0].vida)
# 2 2 ['itm_daga_oxidada'] 12

c4 = ContenidoSalaDTO.desde_json(sala4)
print(c4.id_sala, len(c4.enemigos), c4.objetos, c4.enemigos[0].vida)
# 4 1 ['itm_llave_negra', 'itm_pocion_menor'] None

rata = {'id': 'ent_rata_gigante', 'nombre': 'Rata gigante', 'clase': 'enemigo',
        'vida_max': 12, 'ataque': 5, 'defensa': 1, 'velocidad': 150,
        'comportamiento': 'rastreador', 'suelta': ['itm_pocion_menor']}

guardian = {'id': 'ent_guardian_oseo', 'nombre': 'Guardián óseo', 'clase': 'enemigo',
            'vida_max': 20, 'ataque': 7, 'defensa': 3, 'velocidad': 90,
            'comportamiento': 'guardián'}

ficha_rata = FichaEnemigoDTO.desde_json(rata)
ficha_guardian = FichaEnemigoDTO.desde_json(guardian)
print(ficha_rata.id_ficha, ficha_rata.nombre, ficha_rata.vida_max, ficha_rata.suelta)
print(ficha_guardian.id_ficha, ficha_guardian.nombre, ficha_guardian.vida_max, ficha_guardian.suelta)


dardos = {'id': 'trp_dardos', 'nombre': 'Dardos en la pared', 'clase': 'trampa',
          'daño': 4, 'rearme': 300}
t = FichaTrampaDTO.desde_json(dardos)
print(t.id_ficha, t.dano, t.rearme)    # trp_dardos 4 300

sin_rearme = {'id': 'trp_x', 'nombre': 'X', 'clase': 'trampa', 'daño': 2}
print(FichaTrampaDTO.desde_json(sin_rearme).rearme)   # None

daga = {'id': 'itm_daga_oxidada', 'nombre': 'Daga oxidada', 'clase': 'arma',
        'peso': 2, 'valor': 15, 'ataque_bonus': 3}
pocion = {'id': 'itm_pocion_menor', 'nombre': 'Poción menor', 'clase': 'pocion',
          'peso': 1, 'valor': 10, 'cura': 8}

print(FichaArmaDTO.desde_json(daga).ataque_bonus)   # 3
print(FichaPocionDTO.desde_json(pocion).cura)        # 8

respuesta_catalogo = {
    "entidades": [
        {'id': 'itm_daga_oxidada', 'nombre': 'Daga oxidada', 'clase': 'arma',
         'peso': 2, 'valor': 15, 'ataque_bonus': 3},
        {'id': 'ent_rata_gigante', 'nombre': 'Rata gigante', 'clase': 'enemigo',
         'vida_max': 12, 'ataque': 5, 'defensa': 1, 'velocidad': 150,
         'comportamiento': 'rastreador', 'suelta': ['itm_pocion_menor']},
        {'id': 'itm_llave_bronce', 'nombre': 'Llave de bronce', 'clase': 'llave',
         'peso': 1, 'valor': 0, 'abre': 'puerta_2_7'},
        {'id': 'itm_pocion_menor', 'nombre': 'Poción menor', 'clase': 'pocion',
         'peso': 1, 'valor': 10, 'cura': 8},
    ]
}

for ficha in respuesta_catalogo["entidades"]:
    dto = ficha_desde_json(ficha)
    print(dto.id_ficha, type(dto).__name__)