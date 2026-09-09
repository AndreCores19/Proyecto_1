package app.Servicios;

import app.DTO.ReservaDTO;
import app.Logica.LogicaReservas;
import java.util.List;

public class ServicioReservas {
    private final LogicaReservas logicaReservas = new LogicaReservas();

    public ReservaDTO reservarEspacio(ReservaDTO reservaDTO) throws Exception {
        return logicaReservas.agregarReserva(reservaDTO);
    }
    public List<ReservaDTO> listarReservas(ReservaDTO reservaDTO) throws Exception { return logicaReservas.listarTodas(); }

    public void cancelarReserva(String idReserva) throws Exception {
        logicaReservas.cancelarReserva(idReserva);
    }
}
