package app.Servicios;

import app.DTO.ResultadoEstadisticaDTO;
import app.Logica.LogicaReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioEstadisticasIT {

    private ServicioEstadisticas servicioEstadisticas;

    @BeforeEach
    void setUp() {
        LogicaReservas logicaReservasDePrueba = new LogicaReservas("src/test/resources/reservas_prueba.json");
        servicioEstadisticas = new ServicioEstadisticas(logicaReservasDePrueba);
    }

    @Test
    void obtenerEstadisticaRecursosDebeContarLasTresCategoriasConDosCadaUna() {
        List<ResultadoEstadisticaDTO> resultado = servicioEstadisticas.obtenerEstadisticaRecursos(
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 9));

        assertEquals(3, resultado.size());
        for (ResultadoEstadisticaDTO r : resultado) {
            assertEquals(2, r.getCantidad());
        }
    }

    @Test
    void obtenerEstadisticaActividadesDebeAgruparLasCincoReservasEnUnaSolaSemana() {
        List<ResultadoEstadisticaDTO> resultado = servicioEstadisticas.obtenerEstadisticaActividades(
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 9));

        assertEquals(1, resultado.size());
        assertEquals("2026-08-03", resultado.get(0).getEtiqueta());
        assertEquals(5, resultado.get(0).getCantidad());
    }
}