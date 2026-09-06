package app.Controllers;

import app.DTO.UsuarioDTO;

public class SesionActual {

    private static UsuarioDTO usuarioActual;

    public static UsuarioDTO getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(UsuarioDTO usuario) {
        usuarioActual = usuario;
    }
}