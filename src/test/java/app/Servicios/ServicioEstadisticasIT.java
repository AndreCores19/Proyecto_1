package app.Servicios;

import app.DTO.ResultadoEstadisticaDTO;
import app.Logica.CategoriaLogica;
import app.Logica.LogicaReservas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// los datos usados en este test se encuentran en reservas_prueba.json y categorias_prueba.json, ambos en src/test/resources
class ServicioEstadisticasIT {

    private ServicioEstadisticas servicioEstadisticas;

    @BeforeEach
    void setUp() {
        LogicaReservas logicaReservasDePrueba = new LogicaReservas("src/test/resources/reservas_prueba.json");
        CategoriaLogica categoriaLogicaDePrueba = new CategoriaLogica("src/test/resources/categorias_prueba.json");
        servicioEstadisticas = new ServicioEstadisticas(logicaReservasDePrueba, categoriaLogicaDePrueba);
    }

    @Test
    void obtenerEstadisticaRecursosDebeContarLasTresCategoriasConDosCadaUna() {
        List<ResultadoEstadisticaDTO> resultado = servicioEstadisticas.obtenerEstadisticaRecursos(
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 9));

        assertEquals(3, resultado.size());
        for (ResultadoEstadisticaDTO r : resultado) {
            assertEquals(2, r.getCantidad());
        }

        List<String> etiquetas = resultado.stream().map(ResultadoEstadisticaDTO::getEtiqueta).toList();
        assertTrue(etiquetas.contains("Sala para 10 personas")); // CAT-000001
        assertTrue(etiquetas.contains("Laptop windows 11"));     // CAT-000002
        assertTrue(etiquetas.contains("Sala de Juntas"));        // CAT-000003
        assertFalse(etiquetas.contains("Desconocida"));
    }

    @Test
    void obtenerEstadisticaRecursosSiLaCategoriaNoExisteDebeMostrarDesconocida(@org.junit.jupiter.api.io.TempDir java.nio.file.Path carpetaTemporal) throws Exception {
        java.nio.file.Path archivoReservas = carpetaTemporal.resolve("reservas.json");
        java.nio.file.Files.writeString(archivoReservas, """
                [
                  {
                    "idReserva": "RES-999999",
                    "actividad": "Prueba",
                    "fecha": "03/08/2026",
                    "horaInicio": "09:00:00",
                    "horaFin": "10:00:00",
                    "idsCategoriaSolicitada": ["CAT-999999"],
                    "idsRecursosAsignados": [],
                    "idFuncionario": "111",
                    "estado": "ACTIVA"
                  }
                ]
                """);

        ServicioEstadisticas servicio = new ServicioEstadisticas(
                new LogicaReservas(archivoReservas.toString()),
                new CategoriaLogica("src/test/resources/categorias_prueba.json"));

        List<ResultadoEstadisticaDTO> resultado = servicio.obtenerEstadisticaRecursos(
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 9));

        assertEquals(1, resultado.size());
        assertEquals("Desconocida", resultado.get(0).getEtiqueta());
        assertEquals(1, resultado.get(0).getCantidad());
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