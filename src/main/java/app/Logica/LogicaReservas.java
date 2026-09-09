package app.Logica;

import app.DTO.CategoriaDTO;
import app.DTO.RecursoDTO;
import app.Logica.CategoriaLogica;
import app.DTO.ReservaDTO;
import app.Datos.RecursoDatos;
import app.Datos.ReservaDatos;
import app.DTO.ResultadoDeAsignacionDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class LogicaReservas {
    private RecursoDatos recursoDatos;
    private ReservaDatos reservaDatos;
    private CategoriaLogica categoriaLogica;

    public LogicaReservas() {
        this.recursoDatos = new RecursoDatos();
        this.recursoDatos.setRutaArchivo("Data/recursos.json");
        this.reservaDatos = new ReservaDatos();
        this.reservaDatos.setRutaArchivo("Data/reservas.json");
        this.categoriaLogica = new CategoriaLogica();
    }

    public List<ReservaDTO> listarTodas() {
        reservaDatos.deserializar();
        return reservaDatos.getReservas();
    }

    public List<ReservaDTO> listarPorRango(LocalDate desde, LocalDate hasta) {
        List<ReservaDTO> todas = listarTodas();
        List<ReservaDTO> resultado = new ArrayList<>();

        for (ReservaDTO reserva : todas) {
            boolean dentroDelRango = !reserva.getFecha().isBefore(desde) && !reserva.getFecha().isAfter(hasta);
            boolean estaActiva = !reserva.getEstado().equals("CANCELADA");

            if (dentroDelRango && estaActiva) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public boolean tieneReservasActivas(String idFuncionario) {
        List<ReservaDTO> todas = listarTodas();
        for(ReservaDTO reserva : todas) {
            if (reserva.getIdFuncionario().equals(idFuncionario) && !reserva.getEstado().equals("CANCELADA")) {
                return true;
            }
        }
        return false;
    }

    public List<ReservaDTO> listarReservasPorFuncionario(String idFuncionario) {
        List<ReservaDTO> todas = listarTodas();
        List<ReservaDTO> resultado = new ArrayList<>();
        for(ReservaDTO reserva : todas) {
            if (reserva.getIdFuncionario().equals(idFuncionario)) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }

    public boolean tieneReservasActivasPorRecurso(String numActivo) throws Exception {
        List<ReservaDTO> todas = listarTodas();
        for(ReservaDTO reserva : todas) {
            if (reserva.getIdsRecursosAsignados().contains(numActivo) && !reserva.getEstado().equals("CANCELADA")) {
                return true;
            }
        }
        return false;
    }

    public ReservaDTO agregarReserva(ReservaDTO nueva) throws Exception {
        ResultadoDeAsignacionDTO resultado = estaDisponible(nueva);
        nueva.setIdsRecursosAsignados(resultado.getIdsRecursosAsignados());

        reservaDatos.deserializar();
        String nuevoId = generarSiguienteId(reservaDatos.getReservas());
        nueva.setIdReserva(nuevoId);
        reservaDatos.getReservas().add(nueva);
        reservaDatos.serializar();
        return nueva;
    }

    private String generarSiguienteId(List<ReservaDTO> reservas) {
        int maxId = 0;
        for (ReservaDTO r : reservas) {
            try {
                int idNum = Integer.parseInt(r.getIdReserva());
                if (idNum > maxId) {
                    maxId = idNum;
                }
            } catch (NumberFormatException e) { }
        }
        return String.valueOf(maxId + 1);
    }

    private ResultadoDeAsignacionDTO estaDisponible(ReservaDTO reserva) throws Exception {
        List<ReservaDTO> todasLasReservas = listarTodas();
        List<CategoriaDTO> todasLasCategorias = categoriaLogica.listarTodas();
        List<String> idsCategoriasNoDisponibles = new ArrayList<>();
        List<String> idsRecursosAsignados = new ArrayList<>();
        if(!esFechaFutura(reserva.getFecha(), reserva.getHoraInicio())){
            throw new Exception("La fecha y hora de la reserva deben ser futuras.");
        }
        for(String idCategoriaSolicitada : reserva.getIdsCategoriaSolicitada()){
            for(CategoriaDTO categoria : todasLasCategorias){
                if(categoria.getId().equals(idCategoriaSolicitada)){
                    boolean recursoEncontrado = false;
                    for(RecursoDTO recurso : categoria.getRecursos()){
                        boolean recursoOcupado = false;
                        for(ReservaDTO reservaExistente : todasLasReservas){
                            if(reservaExistente.getEstado().equals("CANCELADA")) continue;
                            if (reservaExistente.getIdsRecursosAsignados() != null
                                    && reservaExistente.getIdsRecursosAsignados().contains(recurso.getNumActivo())
                                    && comparacionFechas(reserva.getFecha(), reservaExistente.getFecha())
                                    && hayConflictoHoras(reserva.getHoraInicio(), reserva.getHoraFin(),
                                    reservaExistente.getHoraInicio(), reservaExistente.getHoraFin())) {
                                recursoOcupado = true;
                                break;
                            }
                        }
                        if(!recursoOcupado){
                            idsRecursosAsignados.add(recurso.getNumActivo());
                            recursoEncontrado = true;
                            break;
                        }
                    }
                    if(!recursoEncontrado){
                        idsCategoriasNoDisponibles.add(idCategoriaSolicitada);
                    }
                }
                break;
            }
        }
        return new ResultadoDeAsignacionDTO(idsCategoriasNoDisponibles.isEmpty(), idsCategoriasNoDisponibles, idsRecursosAsignados);
    }

    public void cancelarReserva(String idReserva) throws Exception {
        reservaDatos.deserializar();
        List<ReservaDTO> reservas = reservaDatos.getReservas();
        boolean encontrada = false;
        for (ReservaDTO reserva : reservas) {
            if (reserva.getIdReserva().equals(idReserva)) {
                if (reserva.getEstado().equals("CANCELADA")) {
                    throw new Exception("La reserva ya está cancelada.");
                }
                if(!esFechaFutura(reserva.getFecha(), reserva.getHoraInicio())) {
                    throw new Exception("No se puede cancelar una reserva pasada.");
                }
                reserva.setEstado("CANCELADA");
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new Exception("No se encontró la reserva con el ID proporcionado.");
        }
        reservaDatos.serializar();
    }

    private boolean hayConflictoHoras(LocalTime horaInicioA, LocalTime horaFinA, LocalTime horaInicioB, LocalTime horaFinB) {
        return horaInicioA.isBefore(horaFinB) && horaInicioB.isBefore(horaFinA);
    }
    private boolean comparacionFechas(LocalDate fechaA, LocalDate fechaB) {
        return fechaA.equals(fechaB);
    }
    private boolean esFechaFutura(LocalDate fechaA, LocalTime horaInicioA) {
        return fechaA.isAfter(LocalDate.now()) || fechaA.equals(LocalDate.now()) && horaInicioA.isAfter(LocalTime.now());
    }
}