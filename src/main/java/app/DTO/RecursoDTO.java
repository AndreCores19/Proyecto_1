package app.DTO;

public class RecursoDTO {
    private String numActivo;
    private CategoriaDTO categoria;
    private String descripcion;

    public RecursoDTO(String numActivo, CategoriaDTO categoria, String descripcion) {
        this.numActivo = numActivo;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public RecursoDTO() {
    }

    public String getNumActivo() {
        return numActivo;
    }

    public void setNumActivo(String numActivo) {
        this.numActivo = numActivo;
    }

    public CategoriaDTO getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDTO categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
