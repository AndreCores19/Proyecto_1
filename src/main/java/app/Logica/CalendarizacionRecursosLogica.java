package app.Logica;

import app.DTO.CategoriaDTO;
import app.DTO.MatrizDTO;
import app.DTO.RecursoDTO;
import app.DTO.ReservaDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarizacionRecursosLogica {
    private final LogicaReservas logicaReservas;
    private final CategoriaLogica categoriaLogica;
    private final RecursoLogica recursoLogica;

    public CalendarizacionRecursosLogica() {
        this(new LogicaReservas(), new CategoriaLogica(), new RecursoLogica());
    }

    public CalendarizacionRecursosLogica(LogicaReservas logicaReservas, CategoriaLogica categoriaLogica, RecursoLogica recursoLogica) {
        this.logicaReservas = logicaReservas;
        this.categoriaLogica = categoriaLogica;
        this.recursoLogica = recursoLogica;
    }
    public List<MatrizDTO> construirMatriz (String idCategoria, LocalDate fecha) throws Exception {
        CategoriaDTO categoria = categoriaLogica.buscarPorId(idCategoria);
        List<RecursoDTO> recursos = recursoLogica.filtrarPorCategoria(categoria.getId());
        List<ReservaDTO> reservasDelDia = logicaReservas.listarPorRango(fecha, fecha);
        List<MatrizDTO> matriz = new ArrayList<>();

        for (LocalTime hora = LocalTime.of(7, 0); hora.isBefore(LocalTime.of(21, 0)); hora = hora.plusMinutes(30)) {
            LocalTime finBloque = hora.plusMinutes(30);
            MatrizDTO fila = new MatrizDTO(hora.toString());
            for (RecursoDTO recurso : recursos) {
                boolean ocupado = estaOcupado(recurso.getNumActivo(), hora, finBloque, reservasDelDia);
                fila.getEstadoPorRecurso().put(recurso.getNumActivo(), ocupado ? "Ocupado" : "Libre");
            }
            matriz.add(fila);
        }
        return matriz;
    }

    private boolean estaOcupado(String numActivo, LocalTime inicioBloque, LocalTime finBloque, List<ReservaDTO> reservasDelDia) {
        for(ReservaDTO reserva : reservasDelDia) {
            if (reserva.getEstado().equals("CANCELADA")) continue;
            if (reserva.getIdsRecursosAsignados()==null ||!reserva.getIdsRecursosAsignados().contains(numActivo)) continue;
            if (logicaReservas.hayConflictoHoras(inicioBloque, finBloque, reserva.getHoraInicio(), reserva.getHoraFin())) {
                return true;
            }
        }
        return false;
    }

}