package app;

import app.DTO.AdministradorDTO;
import app.Datos.AdministradorDatos;

import java.util.ArrayList;
import java.util.List;

public class PrimerAdmin {
    public static void main(String[] args) {
        AdministradorDatos datos = new AdministradorDatos();
        datos.setRutaArchivo("administradores.json");

        AdministradorDTO admin = new AdministradorDTO("Administrador Principal", "admin", "hola123", "ADMIN");

        List<AdministradorDTO> lista = new ArrayList<>();
        lista.add(admin);

        datos.setListado(lista);
        datos.serializar();

        System.out.println("Administrador creado: id=admin, clave=hola123");
    }
}