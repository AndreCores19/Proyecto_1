package app.DTO;

public class AdministradorDTO extends UsuarioDTO {
    private String nombre;

    public AdministradorDTO(String nombre, String id, String clave, String rol) {
        super(id, clave, rol);
        this.nombre = nombre;
    }

    public AdministradorDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}