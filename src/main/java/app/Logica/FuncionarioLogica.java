package app.Logica;

import app.DTO.FuncionarioDTO;
import app.Datos.FuncionarioDatos;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioLogica {

    private LogicaReservas reservaLogica;
    private String rutaArchivo;

    public FuncionarioLogica() {
        this.rutaArchivo = "src/Data/funcionarios.json";
        this.reservaLogica = new LogicaReservas();
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public List<FuncionarioDTO> listarTodos() {
        FuncionarioDatos datos = new FuncionarioDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();
        return datos.getListado();
    }

    public FuncionarioDTO buscarPorId(String id) throws Exception {
        for (FuncionarioDTO f : listarTodos()) {
            if (f.getId().equals(id)) {
                return f;
            }
        }
        throw new Exception("No existe un funcionario con id: " + id);
    }

    public List<FuncionarioDTO> buscarPorNombre(String texto) {
        List<FuncionarioDTO> resultado = new ArrayList<>();
        for (FuncionarioDTO f : listarTodos()) {
            if (f.getNombre() != null && f.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    public void agregar(FuncionarioDTO nuevo) throws Exception {
        if (nuevo.getId() == null || nuevo.getId().trim().isEmpty()) {
            throw new Exception("El id del funcionario no puede estar vacío.");
        }
        if (nuevo.getNombre() == null || nuevo.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacío.");
        }
        if (nuevo.getTelefono() == null || nuevo.getTelefono().trim().isEmpty()) {
            throw new Exception("El teléfono no puede estar vacío.");
        }

        FuncionarioDatos datos = new FuncionarioDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        for (FuncionarioDTO f : datos.getListado()) {
            if (f.getId().equals(nuevo.getId())) {
                throw new Exception("Ya existe un funcionario con ese id.");
            }
        }

        nuevo.setClave(nuevo.getId());
        nuevo.setRol("FUNCIONARIO");

        datos.getListado().add(nuevo);
        datos.serializar();
    }

    public void modificar(FuncionarioDTO actualizado) throws Exception {
        if (actualizado.getNombre() == null || actualizado.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacío.");
        }
        if (actualizado.getTelefono() == null || actualizado.getTelefono().trim().isEmpty()) {
            throw new Exception("El teléfono no puede estar vacío.");
        }

        FuncionarioDatos datos = new FuncionarioDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        boolean encontrado = false;
        for (FuncionarioDTO f : datos.getListado()) {
            if (f.getId().equals(actualizado.getId())) {
                f.setNombre(actualizado.getNombre());
                f.setTelefono(actualizado.getTelefono());
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new Exception("No existe un funcionario con ese id.");
        }
        datos.serializar();
    }

    public void eliminar(String id) throws Exception {
        if (reservaLogica.tieneReservasActivas(id)) {
             throw new Exception("No se puede eliminar: el funcionario tiene reservas activas .");
        }

        FuncionarioDatos datos = new FuncionarioDatos();
        datos.setRutaArchivo(rutaArchivo);
        datos.deserializar();

        boolean eliminado = datos.getListado().removeIf(f -> f.getId().equals(id));

        if (!eliminado) {
            throw new Exception("No existe un funcionario con ese id.");
        }
        datos.serializar();
    }
}