package app.Service;

import app.DTO.ReservaDTO;
import app.Logic.LogicaReservas;

public class ServicioReservas {
    private final LogicaReservas logicaReservas = new LogicaReservas();

    public int crearReserva(ReservaDTO reservaDTO){ return logicaReservas.reservarEspacio(reservaDTO); }
}
