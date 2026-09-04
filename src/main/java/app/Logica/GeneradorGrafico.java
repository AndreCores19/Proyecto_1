package app.Logica;
import app.DTO.ResultadoEstadisticaDTO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.image.BufferedImage;
import java.util.List;

public class GeneradorGrafico {
    public static Image generar(String titulo, String ejex, String ejey, List<ResultadoEstadisticaDTO> datos){
        if (datos == null || datos.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para graficar");
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ResultadoEstadisticaDTO r : datos) {
            dataset.addValue(r.getCantidad(), "Cantidad", r.getEtiqueta());
        }

        JFreeChart chart = ChartFactory.createBarChart(titulo, ejex, ejey, dataset);
        BufferedImage bufferedImage = chart.createBufferedImage(600, 400);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
