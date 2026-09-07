package app.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaDTO {
    private String idReserva;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> idsCategoriaSolicitada;
    private List<String> idsRecursosAsignados;
    private String idFuncionario;
    private String estado;

    public ReservaDTO() {}

    public ReservaDTO(String idReserva,String actividad, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, List<String> idsCategoria, String idFuncionario, String estado, List<String> idsRecursos) {
        this.idReserva = idReserva;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idsCategoriaSolicitada = idsCategoria;
        this.idFuncionario = idFuncionario;
        this.estado = estado;
        this.idsRecursosAsignados = idsRecursos;
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

    public LocalTime getHoraInicio() {
        return horaInicio;
    }


    public List<String> getIdsCategoriaSolicitada() {
        return idsCategoriaSolicitada;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public List<String> getIdsRecursosAsignados() {
        return idsRecursosAsignados;
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

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setIdsCategoriaSolicitada(List<String> idsCategoria) {
        this.idsCategoriaSolicitada = idsCategoria;
    }

    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setIdsRecursosAsignados(List<String> idsRecursos) {
        this.idsRecursosAsignados = idsRecursos;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
