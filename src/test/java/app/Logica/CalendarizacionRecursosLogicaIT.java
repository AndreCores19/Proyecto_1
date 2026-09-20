package app.Logica;

import app.DTO.MatrizDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// los datos usados en este test se encuentran en reservas_prueba.json, categorias_prueba.json y recursos_prueba.json, los tres en src/test/resources
class CalendarizacionRecursosLogicaIT {

    private static final String RESERVAS = "src/test/resources/reservas_prueba.json";
    private static final String CATEGORIAS = "src/test/resources/categorias_prueba.json";
    private static final String RECURSOS = "src/test/resources/recursos_prueba.json";

    private CalendarizacionRecursosLogica calendarizacion;

    @BeforeEach
    void setUp() {
        LogicaReservas logicaReservas = new LogicaReservas(RESERVAS);
        CategoriaLogica categoriaLogica = new CategoriaLogica(CATEGORIAS);
        RecursoLogica recursoLogica = new RecursoLogica(RECURSOS);
        calendarizacion = new CalendarizacionRecursosLogica(logicaReservas, categoriaLogica, recursoLogica);
    }

    private MatrizDTO bloque(List<MatrizDTO> matriz, String hora) {
        return matriz.stream()
                .filter(m -> m.getHora().equals(hora))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No se encontró el bloque de hora " + hora));
    }

    @Test
    void construirMatrizDebeTenerVeintiochoBloquesDeMediaHoraEntreLasSieteYLasNueveDeLaNoche() throws Exception {
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000001", LocalDate.of(2026, 8, 3));

        assertEquals(28, matriz.size());
        assertEquals("07:00", matriz.get(0).getHora());
        assertEquals("20:30", matriz.get(matriz.size() - 1).getHora());
    }

    @Test
    void construirMatrizDebeIncluirUnaColumnaPorCadaRecursoDeLaCategoria() throws Exception {
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000001", LocalDate.of(2026, 8, 3));

        // CAT-000001 tiene dos recursos en recursos_prueba.json: 34343 y 283102
        assertEquals(2, matriz.get(0).getEstadoPorRecurso().size());
        assertTrue(matriz.get(0).getEstadoPorRecurso().containsKey("34343"));
        assertTrue(matriz.get(0).getEstadoPorRecurso().containsKey("283102"));
    }

    @Test
    void construirMatrizDebeMarcarOcupadoSoloElRangoExactoDeLaReserva() throws Exception {
        // RES-000001: lunes 03/08, recurso 452784 (categoría CAT-000003), 09:00 a 11:00
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000003", LocalDate.of(2026, 8, 3));

        assertEquals("Libre", bloque(matriz, "08:30").getEstadoPorRecurso().get("452784"));
        assertEquals("Ocupado", bloque(matriz, "09:00").getEstadoPorRecurso().get("452784"));
        assertEquals("Ocupado", bloque(matriz, "10:30").getEstadoPorRecurso().get("452784"));
        // La reserva termina a las 11:00 en punto: ese bloque ya no debe marcarse ocupado
        assertEquals("Libre", bloque(matriz, "11:00").getEstadoPorRecurso().get("452784"));
    }

    @Test
    void construirMatrizNoDebeMezclarLaOcupacionEntreRecursosDeLaMismaCategoria() throws Exception {
        // RES-000004: jueves 06/08, ocupa solo el recurso 34343 (CAT-000001), de 10:00 a 11:00
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000001", LocalDate.of(2026, 8, 6));

        assertEquals("Ocupado", bloque(matriz, "10:00").getEstadoPorRecurso().get("34343"));
        // 283102 es de la misma categoría pero nadie lo reservó ese día
        assertEquals("Libre", bloque(matriz, "10:00").getEstadoPorRecurso().get("283102"));
    }

    @Test
    void construirMatrizSoloDebeConsiderarReservasDeLaFechaSolicitada() throws Exception {
        // RES-000004 ocupa 34343 el jueves 06/08; un día distinto debe verse todo libre
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000001", LocalDate.of(2026, 8, 3));

        for (MatrizDTO fila : matriz) {
            assertEquals("Libre", fila.getEstadoPorRecurso().get("34343"));
        }
    }

    @Test
    void construirMatrizNoDebeConsiderarUnaReservaCancelada() throws Exception {
        // RES-000007: martes 04/08, recurso 238715 (CAT-000002), 09:00 a 10:00, pero está CANCELADA
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000002", LocalDate.of(2026, 8, 4));

        assertEquals("Libre", bloque(matriz, "09:00").getEstadoPorRecurso().get("238715"));
    }

    @Test
    void construirMatrizDebeConsiderarLaOtraReservaActivaDelMismoDia() throws Exception {
        // RES-000002: martes 04/08, recurso 238715 (CAT-000002), 08:00 a 09:00, sí está ACTIVA
        List<MatrizDTO> matriz = calendarizacion.construirMatriz("CAT-000002", LocalDate.of(2026, 8, 4));

        assertEquals("Ocupado", bloque(matriz, "08:00").getEstadoPorRecurso().get("238715"));
        assertEquals("Libre", bloque(matriz, "08:00").getEstadoPorRecurso().get("45238"));
    }

    @Test
    void construirMatrizSiLaCategoriaNoExisteDebeLanzarException() {
        assertThrows(Exception.class,
                () -> calendarizacion.construirMatriz("CAT-999999", LocalDate.of(2026, 8, 3)));
    }

    @Test
    void construirMatrizSiLaCategoriaNoTieneRecursosDebeDevolverFilasSinColumnas(@TempDir Path carpetaTemporal) throws Exception {
        // categorias_prueba.json si trae CAT-000002, pero este RecursoLogica de prueba apunta a un archivo sin ningún recurso de esa categoría.
        Path recursosSinLaptops = carpetaTemporal.resolve("recursos.json");
        Files.writeString(recursosSinLaptops, "[]");

        CalendarizacionRecursosLogica calendarizacionSinRecursos = new CalendarizacionRecursosLogica(
                new LogicaReservas(RESERVAS),
                new CategoriaLogica(CATEGORIAS),
                new RecursoLogica(recursosSinLaptops.toString()));

        List<MatrizDTO> matriz = calendarizacionSinRecursos.construirMatriz("CAT-000002", LocalDate.of(2026, 8, 3));

        assertEquals(28, matriz.size());
        assertTrue(matriz.get(0).getEstadoPorRecurso().isEmpty());
    }
}