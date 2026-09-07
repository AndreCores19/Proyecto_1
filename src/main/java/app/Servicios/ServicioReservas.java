package app.Servicios;

import app.DTO.ReservaDTO;
import app.Logica.LogicaReservas;

public class ServicioReservas {
    private final LogicaReservas logicaReservas = new LogicaReservas();

    public ReservaDTO reservarEspacio(ReservaDTO reservaDTO) throws Exception {
        return logicaReservas.agregarReserva(reservaDTO);
    }
}
