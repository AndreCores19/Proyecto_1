package app;

import app.DTO.AdministradorDTO;
import app.DTO.RecursoDTO;
import app.Datos.AdministradorDatos;
import app.DTO.FuncionarioDTO;
import app.Logica.CategoriaLogica;
import app.Logica.FuncionarioLogica;
import app.Logica.RecursoLogica;


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

        CategoriaLogica categoriaLogica = new CategoriaLogica();

        app.DTO.CategoriaDTO cat1 = null;
        app.DTO.CategoriaDTO cat2 = null;
        app.DTO.CategoriaDTO cat3 = null;
        try{
            cat1 = new app.DTO.CategoriaDTO("1", "Categoria 1");
            cat2 = new app.DTO.CategoriaDTO("2", "Categoria 2");
            cat3 = new app.DTO.CategoriaDTO("3", "Categoria 3");
            categoriaLogica.agregar(cat1);
            categoriaLogica.agregar(cat2);
            categoriaLogica.agregar(cat3);
            System.out.println("Categorias creadas correctamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}