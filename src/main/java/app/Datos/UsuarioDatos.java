package app.Datos;

import app.DTO.AdministradorDTO;
import app.DTO.FuncionarioDTO;
import app.DTO.UsuarioDTO;

public class UsuarioDatos {

    private String rutaArchivoAdmins;
    private String rutaArchivoFuncionarios;

    public UsuarioDatos(String rutaArchivoAdmins, String rutaArchivoFuncionarios) {
        this.rutaArchivoAdmins = rutaArchivoAdmins;
        this.rutaArchivoFuncionarios = rutaArchivoFuncionarios;
    }

    public UsuarioDatos() {
    }

    public String getRutaArchivoAdmins() {
        return rutaArchivoAdmins;
    }

    public void setRutaArchivoAdmins(String rutaArchivoAdmins) {
        this.rutaArchivoAdmins = rutaArchivoAdmins;
    }

    public String getRutaArchivoFuncionarios() {
        return rutaArchivoFuncionarios;
    }

    public void setRutaArchivoFuncionarios(String rutaArchivoFuncionarios) {
        this.rutaArchivoFuncionarios = rutaArchivoFuncionarios;
    }

    // Busca los atributos en los dos  archivos y devuelve el UsuarioDTO si coincide
    public UsuarioDTO iniciarSesion(String id, String clave) {
        AdministradorDatos adminDatos = new AdministradorDatos(rutaArchivoAdmins);
        adminDatos.deserializar();
        for (AdministradorDTO admin : adminDatos.getListado()) {
            if (admin.getId().equals(id) && admin.getClave().equals(clave)) {
                return admin;
            }
        }

        FuncionarioDatos funcionarioDatos = new FuncionarioDatos(rutaArchivoFuncionarios);
        funcionarioDatos.deserializar();
        for (FuncionarioDTO funcionario : funcionarioDatos.getListado()) {
            if (funcionario.getId().equals(id) && funcionario.getClave().equals(clave)) {
                return funcionario;
            }
        }
        return null;
    }
}