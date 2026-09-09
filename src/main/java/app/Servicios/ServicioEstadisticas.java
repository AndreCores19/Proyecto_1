package app.Servicios;
import app.DTO.CategoriaDTO;
import app.DTO.ReservaDTO;
import app.DTO.ResultadoEstadisticaDTO;
import app.Logica.CategoriaLogica;
import app.Logica.LogicaReservas;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ServicioEstadisticas {
    private LogicaReservas logicaReservas = new LogicaReservas();
    private CategoriaLogica categoriaLogica = new CategoriaLogica();

    public List<ResultadoEstadisticaDTO> obtenerEstadisticaRecursos(LocalDate desde, LocalDate hasta){
        List<ReservaDTO> reservas = logicaReservas.listarPorRango(desde, hasta);
        Map<String, Integer> conteoRecursos = new TreeMap<>();

        for (ReservaDTO reserva : reservas) {
            for (String idCategoria : reserva.getIdsCategoriaSolicitada()) {
                conteoRecursos.merge(idCategoria, 1, Integer::sum); // si la llave ya existe, sume 1; si no existe, se pone en 1

            }
        }
        List<ResultadoEstadisticaDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : conteoRecursos.entrySet()) {
            String nombreCategoria;
            try {
                CategoriaDTO categoria = categoriaLogica.buscarPorId(entrada.getKey());
                nombreCategoria = categoria.getDescripcion();
            } catch (Exception e) {
                nombreCategoria = "Desconocida";
            }
            resultado.add(new ResultadoEstadisticaDTO(nombreCategoria, entrada.getValue()));
        }
        return resultado;
    }

    public List<ResultadoEstadisticaDTO> obtenerEstadisticaActividades(LocalDate desde, LocalDate hasta){
        List<ReservaDTO> reservas = logicaReservas.listarPorRango(desde, hasta);
        Map<String, Integer> contarActividades = new TreeMap<>();

        for (ReservaDTO reserva : reservas) {
            LocalDate lunesDeEsaSemana = reserva.getFecha().with(DayOfWeek.MONDAY);
            contarActividades.merge(lunesDeEsaSemana.toString(), 1, Integer::sum);
        }
        List<ResultadoEstadisticaDTO> resultado = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : contarActividades.entrySet()) {
            resultado.add(new ResultadoEstadisticaDTO(entrada.getKey(), entrada.getValue()));
        }
        return resultado;
    }
}
