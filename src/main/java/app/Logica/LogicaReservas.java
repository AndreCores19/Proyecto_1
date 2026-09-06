package app.Logica;

import app.DTO.ReservaDTO;
import app.Datos.RecursoDatos;
import app.Datos.ReservaDatos;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;



public class LogicaReservas {
    private RecursoDatos recursoDatos;
    private ReservaDatos reservaDatos;

    public List<ReservaDTO> obtenerTodas() {
        reservaDatos.deserializar();
        return reservaDatos.getReservas();
    }

    public List<ReservaDTO> obtenerPorRango(LocalDate desde, LocalDate hasta) {
        List<ReservaDTO> todas = obtenerTodas();
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
}
