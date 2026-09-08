package app;
import app.DTO.CeldaActividadDTO;
import app.DTO.ResultadoEstadisticaDTO;
import app.Datos.ReservaDatos;
import app.Servicios.ServicioEstadisticas;
import app.Servicios.ServicioProgramacion;

import java.time.LocalDate;
import java.util.List;

public class PruebaPDF {
    public static void main(String[] args) {
        ServicioProgramacion servicioProgramacion = new ServicioProgramacion();
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(LocalDate.of(2026, 8, 3));

        for (int h = 0; h < 24; h++) {
            for (int d = 0; d < 7; d++) {
                System.out.print((matriz[h][d] != null ? matriz[h][d].getNombreActividad() : "-") + "\t");
            }
            System.out.println();
        }

        ServicioEstadisticas servicioEstadisticas = new ServicioEstadisticas();

        System.out.println("\n--- Estadísticas de Recursos ---");
        List<ResultadoEstadisticaDTO> estadisticaRecursos = servicioEstadisticas.obtenerEstadisticaRecursos(
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 9));
        for (ResultadoEstadisticaDTO r : estadisticaRecursos) {
            System.out.println(r.getEtiqueta() + ": " + r.getCantidad());
        }

        System.out.println("\n--- Estadísticas de Actividades ---");
        List<ResultadoEstadisticaDTO> estadisticaActividades = servicioEstadisticas.obtenerEstadisticaActividades(
                LocalDate.of(2026, 8, 3), LocalDate.of(2026, 8, 9));
        for (ResultadoEstadisticaDTO a : estadisticaActividades) {
            System.out.println(a.getEtiqueta() + ": " + a.getCantidad());
        }
    }
}
