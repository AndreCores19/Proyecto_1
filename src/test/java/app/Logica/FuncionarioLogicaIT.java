package app.Logica;

import app.DTO.FuncionarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioLogicaIT {

    private static final String RUTA_PRUEBA = "src/test/resources/funcionarios_prueba.json";

    private FuncionarioLogica funcionarioLogica;

    @BeforeEach
    void setUp() {
        funcionarioLogica = new FuncionarioLogica(RUTA_PRUEBA);
    }

    @Test
    void listarTodosDebeRetornarLosTresFuncionarios() {
        List<FuncionarioDTO> funcionarios = funcionarioLogica.listarTodos();
        assertEquals(3, funcionarios.size());
    }

    @Test
    void buscarPorIdDebeEncontrarElFuncionarioCorrecto() throws Exception {
        FuncionarioDTO funcionario = funcionarioLogica.buscarPorId("222");
        assertEquals("Emily Benavides", funcionario.getNombre());
    }

    @Test
    void buscarPorIdSiNoExisteDebeLanzarException() {
        assertThrows(Exception.class, () -> funcionarioLogica.buscarPorId("999"));
    }

    @Test
    void buscarPorNombreDebeEncontrarPorTextoParcialSinImportarMayusculas() {
        List<FuncionarioDTO> resultado = funcionarioLogica.buscarPorNombre("sanchez");
        assertEquals(1, resultado.size());
        assertEquals("333", resultado.get(0).getId());
    }

    @Test
    void agregarSiIdEsVacioDebeLanzarException() {
        FuncionarioDTO invalido = new FuncionarioDTO();
        invalido.setId("   ");
        invalido.setNombre("Alguien");
        invalido.setTelefono("11112222");

        assertThrows(Exception.class, () -> funcionarioLogica.agregar(invalido));
    }

    @Test
    void agregarSiYaExisteElIdDebeLanzarException() {
        FuncionarioDTO duplicado = new FuncionarioDTO();
        duplicado.setId("111"); // ya existe en el archivo de prueba
        duplicado.setNombre("Otro Nombre");
        duplicado.setTelefono("11112222");

        assertThrows(Exception.class, () -> funcionarioLogica.agregar(duplicado));
    }

    @Test
    void agregarDebeAsignarClaveIgualAlIdYRolFuncionario(@TempDir Path carpetaTemporal) throws Exception {
        Path copia = carpetaTemporal.resolve("funcionarios.json");
        Files.copy(Path.of(RUTA_PRUEBA), copia);
        FuncionarioLogica logicaDeEscritura = new FuncionarioLogica(copia.toString());

        FuncionarioDTO nuevo = new FuncionarioDTO();
        nuevo.setId("444");
        nuevo.setNombre("Persona Nueva");
        nuevo.setTelefono("88889999");
        logicaDeEscritura.agregar(nuevo);

        assertEquals("444", nuevo.getClave());
        assertEquals("FUNCIONARIO", nuevo.getRol());
    }

    @Test
    void modificarDebeActualizarNombreYTelefono(@TempDir Path carpetaTemporal) throws Exception {
        Path copia = carpetaTemporal.resolve("funcionarios.json");
        Files.copy(Path.of(RUTA_PRUEBA), copia);
        FuncionarioLogica logicaDeEscritura = new FuncionarioLogica(copia.toString());

        FuncionarioDTO actualizado = new FuncionarioDTO();
        actualizado.setId("111");
        actualizado.setNombre("Andrea Cordero Espinoza");
        actualizado.setTelefono("60000000");
        logicaDeEscritura.modificar(actualizado);

        FuncionarioDTO verificacion = logicaDeEscritura.buscarPorId("111");
        assertEquals("Andrea Cordero Espinoza", verificacion.getNombre());
        assertEquals("60000000", verificacion.getTelefono());
    }

    @Test
    void modificarSiElIdNoExisteDebeLanzarException() {
        FuncionarioDTO inexistente = new FuncionarioDTO();
        inexistente.setId("999");
        inexistente.setNombre("No importa");
        inexistente.setTelefono("00000000");

        assertThrows(Exception.class, () -> funcionarioLogica.modificar(inexistente));
    }

}