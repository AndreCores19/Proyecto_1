package app.Logica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogicaReservasHayConflictoHorasTest {

    private LogicaReservas logicaReservas;

    @BeforeEach
    void setUp() {
        logicaReservas = new LogicaReservas();
    }

    @Test
    void debeDetectarConflictoSiLosRangosSeSolapanParcialmente() {
        // A: 9:00-11:00, B: 10:00-12:00 -> se solapan entre 10:00 y 11:00
        boolean resultado = logicaReservas.hayConflictoHoras(
            LocalTime.of(9, 0), LocalTime.of(11, 0),
            LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertTrue(resultado);
    }

    @Test
    void noDebeDetectarConflictoSiHayUnHuecoEntreLosRangos() {
        // A: 8:00-9:00, B: 10:00-11:00 -> no se tocan
        boolean resultado = logicaReservas.hayConflictoHoras(
            LocalTime.of(8, 0), LocalTime.of(9, 0),
            LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertFalse(resultado);
    }

    @Test
    void noDebeDetectarConflictoSiUnRangoTerminaExactoCuandoElOtroEmpieza() {
        // A: 8:00-9:00, B: 9:00-10:00 -> se tocan en el límite, pero no se solapan de verdad
        boolean resultado = logicaReservas.hayConflictoHoras(
            LocalTime.of(8, 0), LocalTime.of(9, 0),
            LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertFalse(resultado);
    }

    @Test
    void debeDetectarConflictoSiUnRangoEstaCompletamenteContenidoEnElOtro() {
        // A: 9:00-12:00, B: 10:00-11:00 -> B cabe adentro de A
        boolean resultado = logicaReservas.hayConflictoHoras(
            LocalTime.of(9, 0), LocalTime.of(12, 0),
            LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertTrue(resultado);
    }

    @Test
    void debeDetectarConflictoSiLosDosRangosSonIdenticos() {
        boolean resultado = logicaReservas.hayConflictoHoras(
            LocalTime.of(9, 0), LocalTime.of(10, 0),
            LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertTrue(resultado);
    }
}
