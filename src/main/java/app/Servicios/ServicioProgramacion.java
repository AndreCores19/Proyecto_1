package app.Servicios;
import app.DTO.CeldaActividadDTO;
import app.DTO.FuncionarioDTO;
import app.DTO.ReservaDTO;
import app.Logica.FuncionarioLogica;
import app.Logica.LogicaReservas;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class ServicioProgramacion {
    private LogicaReservas logicaReservas = new LogicaReservas();
    private FuncionarioLogica funcionarioLogica = new FuncionarioLogica();

    public CeldaActividadDTO[][] matrizReservas(LocalDate fechaReferencia){
        LocalDate lunes = fechaReferencia.with(DayOfWeek.MONDAY);
        LocalDate domingo = lunes.plusDays(6);
        List<ReservaDTO> reservas = logicaReservas.obtenerPorRango(lunes, domingo);
        CeldaActividadDTO[][] matriz = new CeldaActividadDTO[24][7];
        for (ReservaDTO reserva : reservas) {
            int columna = reserva.getFecha().getDayOfWeek().getValue() - 1;
            String nombreFuncionario;
            try {
                FuncionarioDTO funcionario = funcionarioLogica.buscarPorId(reserva.getIdFuncionario());
                nombreFuncionario = funcionario.getNombre();
            } catch (Exception e) {
                nombreFuncionario = "Desconocido";
            }
            int horaInicio = reserva.getHoraInicio().getHour();
            int horaFin = reserva.getHoraFin().getHour();

            for (int fila = horaInicio; fila < horaFin; fila++) {
                matriz[fila][columna] = new CeldaActividadDTO(reserva.getActividad(), nombreFuncionario);
            }
        }
        return matriz;

    }


}

