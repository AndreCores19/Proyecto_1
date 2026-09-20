package app.Servicios;

import app.DTO.CeldaActividadDTO;
import app.Logica.FuncionarioLogica;
import app.Logica.LogicaReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// los datos usados en este test se encuentran en reservas_prueba.json y funcionarios_prueba.json, ambos en src/test/resources
class ServicioProgramacionIT {

    private ServicioProgramacion servicioProgramacion;

    @BeforeEach
    void setUp() {
        LogicaReservas logicaReservasDePrueba = new LogicaReservas("src/test/resources/reservas_prueba.json");
        FuncionarioLogica funcionarioLogicaDePrueba = new FuncionarioLogica("src/test/resources/funcionarios_prueba.json");
        servicioProgramacion = new ServicioProgramacion(logicaReservasDePrueba, funcionarioLogicaDePrueba);
    }

    @Test
    void matrizReservasDebeUbicarSesionDeJuntaEnLunesHoras9y10() {
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        assertEquals("Sesion de Junta Directiva", matriz[9][0].getNombreActividad());
        assertEquals("Sesion de Junta Directiva", matriz[10][0].getNombreActividad());
    }

    @Test
    void matrizReservasDebeAsociarElNombreDelFuncionarioCorrecto() {
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        // RES-000001 (lunes 9-10) la hizo el funcionario con id "111"
        assertEquals("Andrea Cordero", matriz[9][0].getNombreFuncionario());
        // RES-000002 (martes 8) la hizo el funcionario con id "222"
        assertEquals("Emily Benavides", matriz[8][1].getNombreFuncionario());
        // RES-000004 (jueves 06/08, hora 10) la hizo el funcionario con id "333"
        assertEquals("Jose Pablo Sanchez", matriz[10][3].getNombreFuncionario());
    }

    @Test
    void matrizReservasNoDebeIncluirLaReservaCancelada() {
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        assertNull(matriz[9][1]); // reserva cancelada en martes a las 9
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