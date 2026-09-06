package app.ui;

import app.DTO.ResultadoEstadisticaDTO;
import app.Logica.GeneradorGrafico;
import app.Servicios.ServicioEstadisticas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.List;

public class EstadisticasViewController {
    @FXML private TitledPane titleFondo;
    @FXML private HBox hbCentro;

    //objetos Recursos
    @FXML private VBox vbRecursos;
    @FXML private AnchorPane ancpFechasR;
    @FXML private GridPane grdFechasR;
    @FXML private Label lblRecursos;
    @FXML private Label lblDesdeR;
    @FXML private Label lblHastaR;
    @FXML private Button btnCargarR;
    @FXML private Button btnImprimirR;
    @FXML private DatePicker dteDesdeR;
    @FXML private DatePicker dteHastaR;
    @FXML private AnchorPane ancpEstadisticaR;
    @FXML private TableView<ResultadoEstadisticaDTO> tblRecursos;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblCategoriaR;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblCantidadR;
    @FXML private AnchorPane ancpGraficoR;
    @FXML private ImageView imgGraficoR;

    // objetos Actividades
    @FXML private VBox vbActividades;
    @FXML private AnchorPane ancpFechasA;
    @FXML private GridPane grdFechasA;
    @FXML private Label lblActividades;
    @FXML private Label lblDesdeA;
    @FXML private Label lblHastaA;
    @FXML private Button btnCargarA;
    @FXML private Button btnImprimirA;
    @FXML private DatePicker dteDesdeA;
    @FXML private DatePicker dteHastaA;
    @FXML private AnchorPane ancpEstadisticaA;
    @FXML private TableView<ResultadoEstadisticaDTO> tblActividades;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblSemanaA;
    @FXML private TableColumn<ResultadoEstadisticaDTO, String> tblCantidadA;
    @FXML private AnchorPane ancpGraficoA;
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
    private void cargarEstadisticaRecursos() {
        LocalDate desde = dteDesdeR.getValue();
        LocalDate hasta = dteHastaR.getValue();

        List<ResultadoEstadisticaDTO> resultados = servicioEstadisticas.obtenerEstadisticaRecursos(desde, hasta);

        tblRecursos.setItems(FXCollections.observableArrayList(resultados));
        imgGraficoR.setImage(GeneradorGrafico.generar("Recursos Usados", "Categoría", "Cantidad", resultados));
    }

    private void cargarEstadisticaActividades() {
        LocalDate desde = dteDesdeA.getValue();
        LocalDate hasta = dteHastaA.getValue();

        List<ResultadoEstadisticaDTO> resultados = servicioEstadisticas.obtenerEstadisticaActividades(desde, hasta);

        tblActividades.setItems(FXCollections.observableArrayList(resultados));
        imgGraficoA.setImage(GeneradorGrafico.generar("Recursos Usados", "Semana", "Cantidad", resultados));
    }
}
