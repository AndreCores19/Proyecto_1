package app.ui;

import app.DTO.ResultadoEstadisticaDTO;
import app.Logica.GeneradorGraficoLogica;
import app.Logica.GeneradorReportePDFLogica;
import app.Servicios.ServicioEstadisticas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasViewController {
    //objetos Recursos
    @FXML private DatePicker dteDesdeR;
    @FXML private DatePicker dteHastaR;
    @FXML private TableView<ResultadoEstadisticaDTO> tblRecursos;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblCategoriaR;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblCantidadR;
    @FXML private ImageView imgGraficoR;

    // objetos Actividades
    @FXML private DatePicker dteDesdeA;
    @FXML private DatePicker dteHastaA;
    @FXML private TableView<ResultadoEstadisticaDTO> tblActividades;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblSemanaA;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblCantidadA;
    @FXML private ImageView imgGraficoA;

    private ServicioEstadisticas servicioEstadisticas = new ServicioEstadisticas();
    @FXML
    public void initialize() {
        tblCategoriaR.setCellValueFactory(new PropertyValueFactory<>("etiqueta"));
        tblCantidadR.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tblSemanaA.setCellValueFactory(new PropertyValueFactory<>("etiqueta"));
        tblCantidadA.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
    }
    @FXML
    private void cargarRecursos() {
        LocalDate desde = dteDesdeR.getValue();
        LocalDate hasta = dteHastaR.getValue();

        List<ResultadoEstadisticaDTO> resultados = servicioEstadisticas.obtenerEstadisticaRecursos(desde, hasta);

        tblRecursos.setItems(FXCollections.observableArrayList(resultados));
        imgGraficoR.setImage(GeneradorGraficoLogica.generar("Recursos Usados", "Categoría", "Cantidad", resultados));
    }

    @FXML
    private void cargarActividades() {
        LocalDate desde = dteDesdeA.getValue();
        LocalDate hasta = dteHastaA.getValue();
        if (tblRecursos.getItems().isEmpty()) {
            // avisar al usuario que no hay datos cargados
            Alert alerta = new Alert(Alert.AlertType.WARNING, "No hay datos cargados. Presione 'Cargar' primero.");
            alerta.showAndWait();
            return;
        }
        List<ResultadoEstadisticaDTO> resultados = servicioEstadisticas.obtenerEstadisticaActividades(desde, hasta);

        tblActividades.setItems(FXCollections.observableArrayList(resultados));
        imgGraficoA.setImage(GeneradorGraficoLogica.generar("Recursos Usados", "Semana", "Cantidad", resultados));
    }

    @FXML
    private void imprimirRecursos() {
        if (tblRecursos.getItems().isEmpty()) {
            // avisar al usuario que no hay datos cargados
            Alert alerta = new Alert(Alert.AlertType.WARNING, "No hay datos cargados. Presione 'Cargar' primero.");
            alerta.showAndWait();
            return;
        }
        List<List<String>> filas = new ArrayList<>();
        for (ResultadoEstadisticaDTO r : tblRecursos.getItems()) {
            filas.add(List.of(r.getEtiqueta(), String.valueOf(r.getCantidad())));
        }
        List<String> encabezados = List.of("Categoría", "Cantidad");
        GeneradorReportePDFLogica.generar("Estadísticas de Recursos", encabezados, filas, "/Data/estadisticas_recursos.pdf");
    }

    @FXML
    private void imprimirActividades() {
        if (tblActividades.getItems().isEmpty()) {
            // avisar al usuario que no hay datos cargados
            Alert alerta = new Alert(Alert.AlertType.WARNING, "No hay datos cargados. Presione 'Cargar' primero.");
            alerta.showAndWait();
            return;
        }
        List<List<String>> filas = new ArrayList<>();
        for (ResultadoEstadisticaDTO a : tblActividades.getItems()) {
            filas.add(List.of(a.getEtiqueta(), String.valueOf(a.getCantidad())));
        }
        List<String> encabezados = List.of("Semana", "Cantidad");
        GeneradorReportePDFLogica.generar("Estadísticas de Actividades", encabezados, filas, "/Data/estadisticas_actividades.pdf");
    }
}
