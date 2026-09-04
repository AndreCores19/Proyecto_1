package app;

import java.util.List;

import static app.Logica.GeneradorReportePDF.generar;

public class PruebaPDF {
    public static void main(String[] args) {
        List<String> encabezados = List.of("Id", "Descripcion");
        List<List<String>> filas = List.of(
                List.of("CAT-000001", "Sala para 10 personas"),
                List.of("CAT-000002", "Laptop windows")
        );
        generar("Listado de Categorias", encabezados, filas, "prueba.pdf");
    }
}
