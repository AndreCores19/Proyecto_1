package app.Servicios;

import app.DTO.CeldaActividadDTO;
import app.Logica.LogicaReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
//los datos usados en este test se encuentran en el archivo reservas_prueba.json, que se encuentra en src/test/resources
class ServicioProgramacionIT {

    private ServicioProgramacion servicioProgramacion;

    @BeforeEach
    void setUp() {
        LogicaReservas logicaReservasDePrueba = new LogicaReservas("src/test/resources/reservas_prueba.json");
        servicioProgramacion = new ServicioProgramacion(logicaReservasDePrueba);
    }

    @Test
    void matrizReservasDebeUbicarSesionDeJuntaEnLunesHoras9y10() {
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        assertEquals("Sesion de Junta Directiva", matriz[9][0].getNombreActividad());
        assertEquals("Sesion de Junta Directiva", matriz[10][0].getNombreActividad());
    }

    @Test
    void matrizReservasNoDebeIncluirLaReservaCancelada() {
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        assertNull(matriz[9][1]); //reserva cancelada en martes a las 9
    }

    @Test
    void matrizReservasNoDebeIncluirReservaDeOtraSemana() {
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        for (int h = 0; h < 24; h++) {
            for (int d = 0; d < 7; d++) {
                if (matriz[h][d] != null) {
                    assertNotEquals("Reunion clientes (semana anterior)", matriz[h][d].getNombreActividad());
                }
            }
        }
    }
}