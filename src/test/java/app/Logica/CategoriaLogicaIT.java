package app.Logica;

import app.DTO.CategoriaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaLogicaIT {

    private CategoriaLogica categoriaLogica;

    @BeforeEach
    void setUp() {
        categoriaLogica = new CategoriaLogica("src/test/resources/categorias_prueba.json");
    }

    // Tests de lectura: usan directamente el archivo fijo, sin @TempDir

    @Test
    void listarTodasDebeRetornarLasTresCategorias() {
        List<CategoriaDTO> categorias = categoriaLogica.listarTodas();
        assertEquals(3, categorias.size());
    }

    @Test
    void buscarPorIdDebeEncontrarLaCategoriaCorrecta() throws Exception {
        CategoriaDTO categoria = categoriaLogica.buscarPorId("CAT-000003");
        assertEquals("Sala de Juntas", categoria.getDescripcion());
    }

    @Test
    void buscarPorIdSiNoExisteDebeLanzarExcepcion() {
        assertThrows(Exception.class, () -> categoriaLogica.buscarPorId("CAT-999999"));
    }

    @Test
    void buscarPorDescripcionDebeEncontrarLasDosQueContienenSala() {
        List<CategoriaDTO> resultado = categoriaLogica.buscarPorDescripcion("sala");
        assertEquals(2, resultado.size());
    }

    // --- Tests de escritura: cada test trabaja sobre su propia copia temporal ---

    @Test
    void agregarDebeAsignarUnIdConFormatoCorrecto(@TempDir Path carpetaTemporal) throws Exception {
        Path copia = carpetaTemporal.resolve("categorias.json");
        Files.copy(Path.of("src/test/resources/categorias_prueba.json"), copia);
        CategoriaLogica logicaDeEscritura = new CategoriaLogica(copia.toString());

        CategoriaDTO nueva = new CategoriaDTO();
        nueva.setDescripcion("Sala de prueba");
        logicaDeEscritura.agregar(nueva);

        assertEquals("CAT-000004", nueva.getId());
    }

    @Test
    void agregarSiDescripcionEsVaciaDebeLanzarExcepcion() {
        CategoriaDTO invalida = new CategoriaDTO();
        invalida.setDescripcion("   ");

        assertThrows(Exception.class, () -> categoriaLogica.agregar(invalida));
    }

    @Test
    void modificarDebeActualizarLaDescripcion(@TempDir Path carpetaTemporal) throws Exception {
        Path copia = carpetaTemporal.resolve("categorias.json");
        Files.copy(Path.of("src/test/resources/categorias_prueba.json"), copia);
        CategoriaLogica logicaDeEscritura = new CategoriaLogica(copia.toString());

        CategoriaDTO actualizada = new CategoriaDTO();
        actualizada.setId("CAT-000001");
        actualizada.setDescripcion("Sala renovada para 12 personas");
        logicaDeEscritura.modificar(actualizada);

        CategoriaDTO verificacion = logicaDeEscritura.buscarPorId("CAT-000001");
        assertEquals("Sala renovada para 12 personas", verificacion.getDescripcion());
    }

    @Test
    void modificarSiElIdNoExisteDebeLanzarExcepcion() {
        CategoriaDTO inexistente = new CategoriaDTO();
        inexistente.setId("CAT-999999");
        inexistente.setDescripcion("No importa");

        assertThrows(Exception.class, () -> categoriaLogica.modificar(inexistente));
    }
}