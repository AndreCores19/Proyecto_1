package app.DTO;

public class ResultadoEstadisticaDTO {
    private String etiqueta;
    private int cantidad;

    public ResultadoEstadisticaDTO(String etiqueta, int cantidad) {
        this.etiqueta = etiqueta;
        this.cantidad = cantidad;
    }

    public ResultadoEstadisticaDTO() {
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
