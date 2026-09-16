package app.Servicios;

import app.DTO.MatrizDTO;
import app.Logica.CalendarizacionRecursosLogica;

import java.time.LocalDate;
import java.util.List;

public class ServicioCalendarizacionRecursos {
    private final CalendarizacionRecursosLogica logica = new CalendarizacionRecursosLogica();

    public List<MatrizDTO> obtenerMatrizDeRecursos(String idCategoria, LocalDate fecha) throws Exception {
        return logica.construirMatriz(idCategoria, fecha);
    }
}
