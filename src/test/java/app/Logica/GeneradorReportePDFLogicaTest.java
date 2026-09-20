package app.Logica;

import app.DTO.ResultadoEstadisticaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneradorReportePDFLogicaTest {

    private List<String> encabezadosValidos;
    private List<List<String>> filasValidas;

    @BeforeEach
    void setUp() {
        encabezadosValidos = List.of("Categoría", "Cantidad");
        filasValidas = List.of(List.of("Sala de Juntas", "2"));
    }

    @Test
    void generarConEncabezadoNullDebeLanzarException() {
        assertThrows(IllegalArgumentException.class, () ->
                GeneradorReportePDFLogica.generar("Titulo", null, filasValidas, "Data_Test/test_salida.pdf"));
    }

    @Test
    void generarConEncabezadosVaciosDebeLanzarException() {
        assertThrows(IllegalArgumentException.class, () ->
                GeneradorReportePDFLogica.generar("Titulo", List.of(), filasValidas, "Data_Test/test_salida.pdf"));
    }

    @Test
    void generarConFilasNullDebeLanzarException() {
        assertThrows(IllegalArgumentException.class, () ->
                GeneradorReportePDFLogica.generar("Titulo", encabezadosValidos, null, "Data_Test/test_salida.pdf"));
    }

    @Test
    void generarConFilaMalFormadaDebeLanzarException() {
        List<List<String>> filaMalFormada = List.of(List.of("Solo una columna"));

        assertThrows(IllegalArgumentException.class, () ->
                GeneradorReportePDFLogica.generar("Titulo", encabezadosValidos, filaMalFormada, "Data_Test/test_salida.pdf"));
    }
}