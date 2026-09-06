package app.Servicios;

import app.DTO.CategoriaDTO;
import app.Logica.CategoriaLogica;

import java.util.List;

public class ServicioCategoria {
    private CategoriaLogica categoriaLogica = new CategoriaLogica();

    public List<CategoriaDTO> listarTodos() {
        return categoriaLogica.listarTodas();
    }

    public CategoriaDTO buscarPorId(String id) throws Exception {
        return categoriaLogica.buscarPorId(id);
    }

    public List<CategoriaDTO> buscarPorNombre(String texto) {
        return categoriaLogica.buscarPorDescripcion(texto);
    }

    public void agregar(CategoriaDTO nuevo) throws Exception {
        categoriaLogica.agregar(nuevo);
    }

    public void modificar(CategoriaDTO actualizado) throws Exception {
        categoriaLogica.modificar(actualizado);
    }

    public void eliminar(String id) throws Exception {
        categoriaLogica.eliminar(id);
    }
}