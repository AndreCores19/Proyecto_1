package app.Logica;

import app.DTO.ReservaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogicaReservasIT {

    private static final String RUTA_PRUEBA = "src/test/resources/reservas_prueba.json";

    private LogicaReservas logicaReservas;

    @BeforeEach
    void setUp() {
        logicaReservas = new LogicaReservas(RUTA_PRUEBA);
    }

    @Test
    void tieneReservasActivasDebeSerTrueSiElFuncionarioTieneAlMenosUnaActiva() {
        assertTrue(logicaReservas.tieneReservasActivas("111"));
    }

    @Test
    void tieneReservasActivasDebeSerFalseSiElFuncionarioNoTieneNinguna() {
        assertFalse(logicaReservas.tieneReservasActivas("999"));
    }

    @Test
    void listarReservasPorFuncionarioDebeIncluirActivasYCanceladas() {
        // el funcionario 222 tiene RES-000002 (activa), RES-000005 (activa) y RES-000007 (cancelada)
        List<ReservaDTO> reservas = logicaReservas.listarReservasPorFuncionario("222");
        assertEquals(3, reservas.size());
    }

    @Test
    void listarReservasPorFuncionarioDebeRetornarVacioSiNoTieneNinguna() {
        List<ReservaDTO> reservas = logicaReservas.listarReservasPorFuncionario("999");
        assertTrue(reservas.isEmpty());
    }

    @Test
    void cancelarReservaSiYaEstaCanceladaDebeLanzarException() {
        // RES-000007 ya tiene estado CANCELADA en el archivo de prueba
        assertThrows(Exception.class, () -> logicaReservas.cancelarReserva("RES-000007"));
    }

    @Test
    void cancelarReservaSiLaFechaYaPasoDebeLanzarException() {
        // RES-000001 es del 03/08/2026, ya pasada respecto a la fecha real de hoy
        assertThrows(Exception.class, () -> logicaReservas.cancelarReserva("RES-000001"));
    }

    @Test
    void cancelarReservaSiElIdNoExisteDebeLanzarException() {
        assertThrows(Exception.class, () -> logicaReservas.cancelarReserva("RES-999999"));
    }

    @Test
    void cancelarReservaDebeCambiarElEstadoAcanceladaSiEsFuturaYEstaActiva(@TempDir Path carpetaTemporal) throws Exception {
        Path archivoConReservaFutura = carpetaTemporal.resolve("reservas_futuras.json");
        String fechaFutura = LocalDate.now().plusMonths(1).format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String json = "[{"
            + "\"idReserva\":\"RES-FUTURA\","
            + "\"actividad\":\"Reunion de prueba\","
            + "\"fecha\":\"" + fechaFutura + "\","
            + "\"horaInicio\":\"09:00:00\","
            + "\"horaFin\":\"10:00:00\","
            + "\"idsCategoriaSolicitada\":[\"CAT-000001\"],"
            + "\"idsRecursosAsignados\":[\"34343\"],"
            + "\"idFuncionario\":\"111\","
            + "\"estado\":\"ACTIVA\""
            + "}]";
        Files.writeString(archivoConReservaFutura, json);

        LogicaReservas logicaDeEscritura = new LogicaReservas(archivoConReservaFutura.toString());
        logicaDeEscritura.cancelarReserva("RES-FUTURA");

        List<ReservaDTO> reservas = logicaDeEscritura.listarReservasPorFuncionario("111");
        assertEquals("CANCELADA", reservas.get(0).getEstado());
    }
}
