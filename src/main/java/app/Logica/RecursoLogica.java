package app.Logica;

import app.DTO.RecursoDTO;
import app.DTO.CategoriaDTO;
import app.Datos.RecursoDatos;

import java.util.ArrayList;
import java.util.List;

public class RecursoLogica {

    private String rutaArchivo;
    private CategoriaLogica categoriaLogica;
    private LogicaReservas reservaLogica;

    public RecursoLogica() {
        this.rutaArchivo = "Data/recursos.json";
        this.categoriaLogica = new CategoriaLogica();
        this.reservaLogica = new LogicaReservas();
    }

    public List<RecursoDTO> listarTodos() {
        RecursoDatos datos = new RecursoDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();
        return datos.getListado();
    }

    public List<RecursoDTO> filtrarPorCategoria(String categoriaId) {
        List<RecursoDTO> resultado = new ArrayList<>();
        for (RecursoDTO r : listarTodos()) {
            if (r.getCategoria() != null && r.getCategoria().getId().equals(categoriaId)) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public RecursoDTO buscarPorNumActivo(String numActivo) throws Exception {
        for (RecursoDTO r : listarTodos()) {
            if (r.getNumActivo().equals(numActivo)) {
                return r;
            }
        }
        throw new Exception("No existe un recurso con número de activo: " + numActivo);
    }

    public void agregar(RecursoDTO nuevo) throws Exception {
        if (nuevo.getNumActivo() == null || nuevo.getNumActivo().trim().isEmpty()) {
            throw new Exception("El número de activo no puede estar vacío.");
        }
        if (nuevo.getDescripcion() == null || nuevo.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción no puede estar vacía.");
        }
        if (nuevo.getCategoria() == null || nuevo.getCategoria().getId() == null) {
            throw new Exception("Debe seleccionar una categoría.");
        }

        CategoriaDTO categoriaReal = categoriaLogica.buscarPorId(nuevo.getCategoria().getId());
        nuevo.setCategoria(categoriaReal);
        RecursoDatos datos = new RecursoDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();
        for (RecursoDTO r : datos.getListado()) {
            if (r.getNumActivo().equals(nuevo.getNumActivo())) {
                throw new Exception("Ya existe un recurso con ese número de activo.");
            }
        }
        datos.getListado().add(nuevo);
        datos.serializar();
        categoriaLogica.agregarRecursoALista(categoriaReal.getId(), nuevo);
    }

    public void modificar(RecursoDTO actualizado) throws Exception {
        if (actualizado.getDescripcion() == null || actualizado.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción no puede estar vacía.");
        }
        RecursoDatos datos = new RecursoDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();
        boolean encontrado = false;
        for (RecursoDTO r : datos.getListado()) {
            if (r.getNumActivo().equals(actualizado.getNumActivo())) {
                r.setDescripcion(actualizado.getDescripcion());
                r.setCategoria(actualizado.getCategoria());
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new Exception("No existe un recurso con ese número de activo.");
        }
        datos.serializar();
    }

    public void eliminar(String numActivo) throws Exception {
        if (reservaLogica.tieneReservasActivasPorRecurso(numActivo)) {
            throw new Exception("No se puede eliminar un recurso que tiene reservas activas.");
        }

        RecursoDatos datos = new RecursoDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        RecursoDTO encontrado = null;
        for (RecursoDTO r : datos.getListado()) {
            if (r.getNumActivo().equals(numActivo)) {
                encontrado = r;
                break;
            }
        }
        if (encontrado == null) {
            throw new Exception("No existe un recurso con ese número de activo.");
        }
        datos.getListado().remove(encontrado);
        datos.serializar();
        categoriaLogica.quitarRecursoDeLista(encontrado.getCategoria().getId(), numActivo);
    }
}