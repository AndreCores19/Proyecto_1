package app.DTO;

public class FuncionarioDTO extends UsuarioDTO {
    private String nombre;
    private String telefono;

    public FuncionarioDTO(String id, String clave, String rol, String nombre, String telefono) {
        super(id, clave, rol);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public FuncionarioDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

