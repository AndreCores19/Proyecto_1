package app.Servicios;

import app.DTO.ResultadoEstadisticaDTO;
import app.Logica.GeneradorGraficoLogica;
import javafx.scene.image.Image;

import java.util.List;

public class ServicioGeneradorGrafico {
    public Image generar(String titulo, String ejex, String ejey, List<ResultadoEstadisticaDTO> resultados) {
        return GeneradorGraficoLogica.generar(titulo, ejex, ejey, resultados);
    }
}
