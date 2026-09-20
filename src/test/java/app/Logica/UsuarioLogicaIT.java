package app.Logica;

import app.DTO.UsuarioDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioLogicaIT {

    private static final String ADMINS = "src/test/resources/administradores_prueba.json";
    private static final String FUNCIONARIOS = "src/test/resources/funcionarios_prueba.json";

    private UsuarioLogica usuarioLogica;

    @BeforeEach
    void setUp() {
        usuarioLogica = new UsuarioLogica(ADMINS, FUNCIONARIOS);
    }

    private UsuarioLogica logicaSobreCopia(Path carpetaTemporal) throws Exception {
        Path copiaAdmins = carpetaTemporal.resolve("administradores.json");
        Path copiaFuncionarios = carpetaTemporal.resolve("funcionarios.json");
        Files.copy(Path.of(ADMINS), copiaAdmins);
        Files.copy(Path.of(FUNCIONARIOS), copiaFuncionarios);
        return new UsuarioLogica(copiaAdmins.toString(), copiaFuncionarios.toString());
    }

    @Test
    void iniciarSesionDebeAutenticarAlAdministrador() throws Exception {
        UsuarioDTO usuario = usuarioLogica.iniciarSesion("admin", "hola123");

        assertNotNull(usuario);
        assertEquals("admin", usuario.getId());
        assertEquals("ADMIN", usuario.getRol());
    }

    @Test
    void iniciarSesionDebeAutenticarAlFuncionario() throws Exception {
        UsuarioDTO usuario = usuarioLogica.iniciarSesion("111", "111");

        assertNotNull(usuario);
        assertEquals("111", usuario.getId());
        assertEquals("FUNCIONARIO", usuario.getRol());
    }

    @Test
    void iniciarSesionSiElIdEsVacioDebeLanzarException() {
        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.iniciarSesion("   ", "hola123"));
        assertEquals("Debe ingresar el id.", ex.getMessage());
    }

    @Test
    void iniciarSesionSiElIdEsNuloDebeLanzarException() {
        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.iniciarSesion(null, "hola123"));
        assertEquals("Debe ingresar el id.", ex.getMessage());
    }

    @Test
    void iniciarSesionSiLaClaveEsVaciaDebeLanzarException() {
        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.iniciarSesion("admin", "   "));
        assertEquals("Debe ingresar la clave.", ex.getMessage());
    }

    @Test
    void iniciarSesionSiLaClaveEsIncorrectaDebeLanzarException() {
        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.iniciarSesion("admin", "claveIncorrecta"));
        assertEquals("El id o la clave son incorrectos.", ex.getMessage());
    }

    @Test
    void iniciarSesionSiElIdNoExisteDebeLanzarException() {
        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.iniciarSesion("999", "999"));
        assertEquals("El id o la clave son incorrectos.", ex.getMessage());
    }

    @Test
    void cambiarClaveDebeActualizarLaClaveDelAdministrador(@TempDir Path carpetaTemporal) throws Exception {
        UsuarioLogica logica = logicaSobreCopia(carpetaTemporal);
        UsuarioDTO usuario = logica.iniciarSesion("admin", "hola123");

        logica.cambiarClave(usuario, "hola123", "adminNuevo123");

        // El DTO en memoria quedó actualizado
        assertEquals("adminNuevo123", usuario.getClave());
        // Y el cambio realmente se escribió: la clave nueva sirve...
        assertEquals("admin", logica.iniciarSesion("admin", "adminNuevo123").getId());
        // ...y la vieja ya no
        assertThrows(Exception.class, () -> logica.iniciarSesion("admin", "hola123"));
    }

    @Test
    void cambiarClaveDebeActualizarLaClaveDelFuncionario(@TempDir Path carpetaTemporal) throws Exception {
        UsuarioLogica logica = logicaSobreCopia(carpetaTemporal);
        UsuarioDTO usuario = logica.iniciarSesion("111", "111");

        logica.cambiarClave(usuario, "111", "nuevaClave111");

        assertEquals("nuevaClave111", usuario.getClave());
        assertEquals("111", logica.iniciarSesion("111", "nuevaClave111").getId());
        assertThrows(Exception.class, () -> logica.iniciarSesion("111", "111"));
    }

    @Test
    void cambiarClaveDelAdminNoDebeAfectarAlFuncionario(@TempDir Path carpetaTemporal) throws Exception {
        UsuarioLogica logica = logicaSobreCopia(carpetaTemporal);
        UsuarioDTO admin = logica.iniciarSesion("admin", "hola123");

        logica.cambiarClave(admin, "hola123", "otraClave456");

        assertEquals("111", logica.iniciarSesion("111", "111").getId());
    }

    @Test
    void cambiarClaveSiLaClaveActualNoCoincideDebeLanzarException() {
        UsuarioDTO usuario = new UsuarioDTO("admin", "hola123", "ADMIN");

        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.cambiarClave(usuario, "claveEquivocada", "nuevaClave"));
        assertEquals("La clave actual no coincide.", ex.getMessage());
    }

    @Test
    void cambiarClaveSiLaClaveNuevaEsVaciaDebeLanzarException() {
        UsuarioDTO usuario = new UsuarioDTO("admin", "hola123", "ADMIN");

        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.cambiarClave(usuario, "hola123", "   "));
        assertEquals("La clave nueva no puede estar vacía.", ex.getMessage());
    }

    @Test
    void cambiarClaveSiLaClaveNuevaEsNulaDebeLanzarException() {
        UsuarioDTO usuario = new UsuarioDTO("admin", "hola123", "ADMIN");

        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.cambiarClave(usuario, "hola123", null));
        assertEquals("La clave nueva no puede estar vacía.", ex.getMessage());
    }

    @Test
    void cambiarClaveSiLaClaveNuevaEsIgualALaActualDebeLanzarException() {
        UsuarioDTO usuario = new UsuarioDTO("admin", "hola123", "ADMIN");

        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.cambiarClave(usuario, "hola123", "hola123"));
        assertEquals("La clave nueva debe ser distinta a la actual.", ex.getMessage());
    }

    @Test
    void cambiarClaveSiElRolEsDesconocidoDebeLanzarException() {
        UsuarioDTO usuario = new UsuarioDTO("admin", "hola123", "INVITADO");

        Exception ex = assertThrows(Exception.class,
                () -> usuarioLogica.cambiarClave(usuario, "hola123", "claveNueva"));
        assertEquals("Rol desconocido.", ex.getMessage());
    }

    @Test
    void cambiarClaveSiElAdminNoExisteEnElArchivoDebeLanzarException(@TempDir Path carpetaTemporal) throws Exception {
        UsuarioLogica logica = logicaSobreCopia(carpetaTemporal);
        UsuarioDTO fantasma = new UsuarioDTO("adminFantasma", "hola123", "ADMIN");

        Exception ex = assertThrows(Exception.class,
                () -> logica.cambiarClave(fantasma, "hola123", "claveNueva"));
        assertEquals("No se encontró el administrador para actualizar la clave.", ex.getMessage());
    }

    @Test
    void cambiarClaveSiElFuncionarioNoExisteEnElArchivoDebeLanzarException(@TempDir Path carpetaTemporal) throws Exception {
        UsuarioLogica logica = logicaSobreCopia(carpetaTemporal);
        UsuarioDTO fantasma = new UsuarioDTO("999", "999", "FUNCIONARIO");

        Exception ex = assertThrows(Exception.class,
                () -> logica.cambiarClave(fantasma, "999", "claveNueva"));
        assertEquals("No se encontró el funcionario para actualizar la clave.", ex.getMessage());
    }
}