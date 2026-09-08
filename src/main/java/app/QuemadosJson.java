package app;

import app.DTO.AdministradorDTO;
import app.Datos.AdministradorDatos;
import app.DTO.FuncionarioDTO;
import app.Logica.FuncionarioLogica;

import java.util.ArrayList;
import java.util.List;

public class QuemadosJson {
    public static void main(String[] args) {
        AdministradorDatos datos = new AdministradorDatos();
        datos.setRutaArchivo("Data/administradores.json");

        AdministradorDTO admin = new AdministradorDTO("Administrador Principal", "admin", "hola123", "ADMIN");

        List<AdministradorDTO> lista = new ArrayList<>();
        lista.add(admin);

        datos.setListado(lista);
        datos.serializar();

        System.out.println("Administrador creado: id=admin, clave=hola123");


        FuncionarioLogica logica = new FuncionarioLogica();

        try {
            logica.agregar(new FuncionarioDTO("111", null, null, "Andrea Cordero", "60032423"));
            logica.agregar(new FuncionarioDTO("222", null, null, "Emily Benavides", "72223694"));
            logica.agregar(new FuncionarioDTO("333", null, null, "Jose Pablo Sanchez", "85692541"));
            System.out.println("Funcionarios creados correctamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}