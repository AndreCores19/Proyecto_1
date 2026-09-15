package app.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ExtraccionIADTO {
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> categoriasSugeridas;

    public ExtraccionIADTO() {
    }

    public ExtraccionIADTO(String actividad, LocalDate fecha, LocalTime horaFin, LocalTime horaInicio, List<String> categoriasSugeridas) {
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaFin = horaFin;
        this.horaInicio = horaInicio;
        this.categoriasSugeridas = categoriasSugeridas;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public List<String> getCategoriasSugeridas() {
        return categoriasSugeridas;
    }

    public void setCategoriasSugeridas(List<String> categoriasSugeridas) {
        this.categoriasSugeridas = categoriasSugeridas;
    }
}
