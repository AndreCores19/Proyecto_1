package app;
/*
import app.DAO.FuncionarioDAO;
import app.model.Funcionario;

import java.util.List;

public class PruebaXML {
    public static void main(String[] args) {
        FuncionarioDAO dao = new FuncionarioDAO();

        // Agregamos dos funcionarios de prueba
        dao.agregar(new Funcionario("111", "111", "FUNCIONARIO", "Juan Perez", "3323"));
        dao.agregar(new Funcionario("222", "222", "FUNCIONARIO", "Maria Perez", "222222"));

        // Intentamos agregar uno con id repetido (no debería duplicarse)
        dao.agregar(new Funcionario("111", "111", "FUNCIONARIO", "Juan Perez Otra Vez", "9999"));

        // Listamos todos
        System.out.println("Listado completo:");
        List<Funcionario> lista = dao.listar();
        for (Funcionario f : lista) {
            System.out.println(f.getId() + " - " + f.getNombre());
        }

        // Buscamos uno
        System.out.println("\nBuscando id 222:");
        Funcionario encontrado = dao.buscarPorId("222");
        System.out.println(encontrado != null ? encontrado.getNombre() : "No encontrado");

        // Borramos uno
        dao.borrar("111");
        System.out.println("\nListado después de borrar 111:");
        for (Funcionario f : dao.listar()) {
            System.out.println(f.getId() + " - " + f.getNombre());
        }
    }
}*/

import app.DAO.CategoriaDAO;
import app.model.Categoria;

import java.util.List;

public class PruebaXML {
    public static void main(String[] args) {
        CategoriaDAO dao = new CategoriaDAO();

        // Agregamos dos categorias de prueba
        dao.agregar(new Categoria("111", "Descripcion"));
        dao.agregar(new Categoria("CAT-000002", "Otra Descripcion"));

        // Intentamos agregar uno con id repetido (no debería duplicarse)
        dao.agregar(new Categoria("CAT-000001", "Descripcion Repetida"));

        // Listamos todos
        System.out.println("Listado completo:");
        List<Categoria> lista = dao.listar();
        for (Categoria f : lista) {
            System.out.println(f.getId() + " - " + f.getDescripcion());
        }

        // Buscamos uno
        System.out.println("\nBuscando id 222:");
        Categoria encontrada = dao.buscarPorId("CAT-000002");
        System.out.println(encontrada != null ? encontrada.getDescripcion() : "No encontrado");

        // Borramos uno
        dao.borrar("CAT-000001");
        System.out.println("\nListado después de borrar 111:");
        for (Categoria f : dao.listar()) {
            System.out.println(f.getId() + " - " + f.getDescripcion());
        }
    }
}