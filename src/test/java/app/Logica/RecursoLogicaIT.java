package app.Logica;

import app.DTO.RecursoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecursoLogicaIT {

    private static final String RUTA_PRUEBA = "src/test/resources/recursos_prueba.json";

    private RecursoLogica recursoLogica;

    @BeforeEach
    void setUp() {
        recursoLogica = new RecursoLogica(RUTA_PRUEBA);
    }

    @Test
    void listarTodosDebeRetornarLosCincoRecursos() {
        List<RecursoDTO> recursos = recursoLogica.listarTodos();
        assertEquals(5, recursos.size());
    }

    @Test
    void filtrarPorCategoriaDebeEncontrarLosDosDeCAT000001() {
        List<RecursoDTO> recursos = recursoLogica.filtrarPorCategoria("CAT-000001");
        assertEquals(2, recursos.size());
    }

    @Test
    void filtrarPorCategoriaDebeRetornarVacioSiNoHayRecursosDeEsaCategoria() {
        List<RecursoDTO> recursos = recursoLogica.filtrarPorCategoria("CAT-999999");
        assertTrue(recursos.isEmpty());
    }

    @Test
    void buscarPorNumActivoDebeEncontrarElRecursoCorrecto() throws Exception {
        RecursoDTO recurso = recursoLogica.buscarPorNumActivo("452784");
        assertEquals("Sala de juntas principal", recurso.getDescripcion());
    }

    @Test
    void buscarPorNumActivoDebeLanzarExcepcionSiNoExiste() {
        assertThrows(Exception.class, () -> recursoLogica.buscarPorNumActivo("000000"));
    }

    @Test
    void agregarDebeLanzarExcepcionSiNumActivoEsVacio() {
        RecursoDTO invalido = new RecursoDTO();
        invalido.setNumActivo("   ");
        invalido.setDescripcion("Algo");

        assertThrows(Exception.class, () -> recursoLogica.agregar(invalido));
    }

    @Test
    void agregarDebeLanzarExcepcionSiNoTieneCategoria() {
        RecursoDTO invalido = new RecursoDTO();
        invalido.setNumActivo("999999");
        invalido.setDescripcion("Algo");
        invalido.setCategoria(null);

        assertThrows(Exception.class, () -> recursoLogica.agregar(invalido));
    }

    @Test
    void modificarDebeActualizarLaDescripcion(@TempDir Path carpetaTemporal) throws Exception {
        Path copia = carpetaTemporal.resolve("recursos.json");
        Files.copy(Path.of(RUTA_PRUEBA), copia);
        RecursoLogica logicaDeEscritura = new RecursoLogica(copia.toString());

        RecursoDTO actualizado = new RecursoDTO();
        actualizado.setNumActivo("34343");
        actualizado.setDescripcion("Sala 1 primer piso - remodelada");
        logicaDeEscritura.modificar(actualizado);

        RecursoDTO verificacion = logicaDeEscritura.buscarPorNumActivo("34343");
        assertEquals("Sala 1 primer piso - remodelada", verificacion.getDescripcion());
    }

    @Test
    void modificarDebeLanzarExcepcionSiElNumActivoNoExiste() {
        RecursoDTO inexistente = new RecursoDTO();
        inexistente.setNumActivo("000000");
        inexistente.setDescripcion("No importa");

        assertThrows(Exception.class, () -> recursoLogica.modificar(inexistente));
    }

}
