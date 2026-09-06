package app.Servicios;

import app.DTO.RecursoDTO;
import app.Logica.RecursoLogica;

import java.util.List;


public class ServicioRecurso {
    private RecursoLogica recursoLogica = new RecursoLogica();

    public List<RecursoDTO> listarTodos() {
        return recursoLogica.listarTodos();
    }

    public List<RecursoDTO> filtrarPorCategoria(String categoriaId) {
        return recursoLogica.filtrarPorCategoria(categoriaId);
    }

    public RecursoDTO buscarPorNumActivo(String numActivo) throws Exception {
        return recursoLogica.buscarPorNumActivo(numActivo);
    }

    public void agregar(RecursoDTO nuevo) throws Exception {
        recursoLogica.agregar(nuevo);
    }

    public void modificar(RecursoDTO actualizado) throws Exception {
        recursoLogica.modificar(actualizado);
    }

    public void eliminar(String id) throws Exception {
        recursoLogica.eliminar(id);
    }
}