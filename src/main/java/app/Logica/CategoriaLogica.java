package app.Logica;

import app.DTO.CategoriaDTO;
import app.DTO.RecursoDTO;
import app.Datos.CategoriaDatos;

import java.util.ArrayList;
import java.util.List;

public class CategoriaLogica {

    private String rutaArchivo;

    public CategoriaLogica() {
        this.rutaArchivo = "src/Data/categorias.json";
    }

    public List<CategoriaDTO> listarTodas() {
        CategoriaDatos datos = new CategoriaDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();
        return datos.getListado();
    }

    public CategoriaDTO buscarPorId(String id) throws Exception {
        for (CategoriaDTO c : listarTodas()) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        throw new Exception("No existe una categoría con id: " + id);
    }

    public List<CategoriaDTO> buscarPorDescripcion(String texto) {
        List<CategoriaDTO> resultado = new ArrayList<>();
        for (CategoriaDTO c : listarTodas()) {
            if (c.getDescripcion().toLowerCase().contains(texto.toLowerCase())) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public void agregar(CategoriaDTO nueva) throws Exception {
        if (nueva.getDescripcion() == null || nueva.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción no puede estar vacía.");
        }

        CategoriaDatos datos = new CategoriaDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        String nuevoId = generarSiguienteId(datos.getListado());
        nueva.setId(nuevoId);

        if (nueva.getRecursos() == null) {
            nueva.setRecursos(new ArrayList<>());
        }

        datos.getListado().add(nueva);
        datos.serializar();
    }

    public void modificar(CategoriaDTO actualizada) throws Exception {
        if (actualizada.getDescripcion() == null || actualizada.getDescripcion().trim().isEmpty()) {
            throw new Exception("La descripción no puede estar vacía.");
        }

        CategoriaDatos datos = new CategoriaDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        boolean encontrada = false;
        for (CategoriaDTO c : datos.getListado()) {
            if (c.getId().equals(actualizada.getId())) {
                c.setDescripcion(actualizada.getDescripcion());
                encontrada = true;
                break;
            }
        }

        if (!encontrada) {
            throw new Exception("No existe una categoría con ese id.");
        }
        datos.serializar();
    }

    public void eliminar(String id) throws Exception {
        CategoriaDatos datos = new CategoriaDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        CategoriaDTO encontrada = null;
        for (CategoriaDTO c : datos.getListado()) {
            if (c.getId().equals(id)) {
                encontrada = c;
                break;
            }
        }

        if (encontrada == null) {
            throw new Exception("No existe una categoría con ese id.");
        }

        // Regla de negocio: no se puede borrar una categoría que todavía tiene recursos asociados
        if (encontrada.getRecursos() != null && !encontrada.getRecursos().isEmpty()) {
            throw new Exception("No se puede eliminar: la categoría tiene recursos asociados.");
        }

        datos.getListado().remove(encontrada);
        datos.serializar();
    }

    // Método de apoyo para RecursoLogica: agrega un RecursoDTO a la lista interna de su categoría
    public void agregarRecursoALista(String categoriaId, RecursoDTO recurso) throws Exception {
        CategoriaDatos datos = new CategoriaDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        for (CategoriaDTO c : datos.getListado()) {
            if (c.getId().equals(categoriaId)) {
                if (c.getRecursos() == null) {
                    c.setRecursos(new ArrayList<>());
                }
                c.getRecursos().add(recurso);
                datos.serializar();
                return;
            }
        }
        throw new Exception("No existe una categoría con id: " + categoriaId);
    }

    // Método de apoyo para RecursoLogica: quita un RecursoDTO de la lista interna de su categoría
    public void quitarRecursoDeLista(String categoriaId, String numActivo) throws Exception {
        CategoriaDatos datos = new CategoriaDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        for (CategoriaDTO c : datos.getListado()) {
            if (c.getId().equals(categoriaId) && c.getRecursos() != null) {
                c.getRecursos().removeIf(r -> r.getNumActivo().equals(numActivo));
                datos.serializar();
                return;
            }
        }
        throw new Exception("No existe una categoría con id: " + categoriaId);
    }

    private String generarSiguienteId(List<CategoriaDTO> lista) {
        int maxNumero = 0;
        for (CategoriaDTO c : lista) {
            String soloNumero = c.getId().replace("CAT-", "");
            int numero = Integer.parseInt(soloNumero);
            if (numero > maxNumero) {
                maxNumero = numero;
            }
        }
        int siguiente = maxNumero + 1;
        return String.format("CAT-%06d", siguiente);
    }
}