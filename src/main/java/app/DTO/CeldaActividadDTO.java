package app.DTO;

public class CeldaActividadDTO {
    private String nombreActividad;
    private String nombreFuncionario;

    public CeldaActividadDTO(String nombreActividad, String nombreFuncionario) {
        this.nombreActividad = nombreActividad;
        this.nombreFuncionario = nombreFuncionario;
    }

    public CeldaActividadDTO() {
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }

    public void setNombreFuncionario(String nombreFuncionario) {
        this.nombreFuncionario = nombreFuncionario;
    }
}
