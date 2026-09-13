package app.Servicios;

import app.DTO.ReservaDTO;
import app.DTO.ResultadoDeAsignacionDTO;
import app.Logica.LogicaReservas;
import java.util.List;

public class ServicioReservas {
    private final LogicaReservas logicaReservas = new LogicaReservas();

    public ResultadoDeAsignacionDTO reservarEspacio(ReservaDTO reservaDTO) throws Exception {
        return logicaReservas.agregarReserva(reservaDTO);
    }
    public List<ReservaDTO> listarReservas(ReservaDTO reservaDTO) throws Exception {
        return logicaReservas.listarTodas();
    }

    public List<ReservaDTO> listarReservasPorFuncionario(String idFuncionario) throws Exception {
        return logicaReservas.listarReservasPorFuncionario(idFuncionario);
    }

    public void cancelarReserva(String idReserva) throws Exception {
        logicaReservas.cancelarReserva(idReserva);
    }

}
