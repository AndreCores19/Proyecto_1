package app;

import app.DTO.AdministradorDTO;
import app.DTO.RecursoDTO;
import app.Datos.AdministradorDatos;
import app.DTO.FuncionarioDTO;
import app.Logica.CategoriaLogica;
import app.Logica.FuncionarioLogica;
import app.Logica.RecursoLogica;
import app.DTO.CategoriaDTO;


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
        try {
            categoriaLogica.agregar(new CategoriaDTO(null, "Sala para 10 personas"));
            categoriaLogica.agregar(new CategoriaDTO(null, "Laptop windows 11"));
            categoriaLogica.agregar(new CategoriaDTO(null, "Proyector"));
            System.out.println("Categorías creadas correctamente.");
        } catch (Exception e) {
            System.out.println("Error creando categorías: " + e.getMessage());
        }

        RecursoLogica recursoLogica = new RecursoLogica();
        try {
            CategoriaDTO catSala = categoriaLogica.buscarPorId("CAT-000001");
            CategoriaDTO catLaptop = categoriaLogica.buscarPorId("CAT-000002");

            recursoLogica.agregar(new RecursoDTO("34343", catSala, "Sala 1 primer piso"));
            recursoLogica.agregar(new RecursoDTO("238715", catLaptop, "Laptop #238715"));
            recursoLogica.agregar(new RecursoDTO("45238", catLaptop, "Laptop #45238"));
            System.out.println("Recursos creados correctamente.");
        } catch (Exception e) {
            System.out.println("Error creando recursos: " + e.getMessage());
        }
    }
}