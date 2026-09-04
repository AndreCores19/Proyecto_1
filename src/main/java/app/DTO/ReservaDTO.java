package app.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaDTO {
    private String idReserva;
    private String actividad;
    private LocalDate fecha;
    private LocalTime hora;
    private LocalTime horaFin;
    private List<String> idsCategoria;
    private List<String> idsRecursos;
    private String idFuncionario;
    private String estado;

    public ReservaDTO() {}

    public ReservaDTO(String idReserva,String actividad, LocalDate fecha, LocalTime hora, LocalTime horaFin, List<String> idsCategoria, String idFuncionario, String estado, List<String> idsRecursos) {
        this.idReserva = idReserva;
        this.actividad = actividad;
        this.fecha = fecha;
        this.hora = hora;
        this.horaFin = horaFin;
        this.idsCategoria = idsCategoria;
        this.idFuncionario = idFuncionario;
        this.estado = estado;
        this.idsRecursos = idsRecursos;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getActividad() {
        return actividad;
    }

    public LocalTime getHora() {
        return hora;
    }

    public List<String> getIdsCategoria() {
        return idsCategoria;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public List<String> getIdsRecursos() {
        return idsRecursos;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setIdsCategoria(List<String> idsCategoria) {
        this.idsCategoria = idsCategoria;
    }

    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setIdsRecursos(List<String> idsRecursos) {
        this.idsRecursos = idsRecursos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
