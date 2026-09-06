package app.DTO;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDTO {
    private String id;
    private String descripcion;
    private List<RecursoDTO> recursos;

    public CategoriaDTO(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        this.recursos = new ArrayList<>();
    }

    public CategoriaDTO() {
        this.recursos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<RecursoDTO> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoDTO> recursos) {
        this.recursos = recursos;
    }
}