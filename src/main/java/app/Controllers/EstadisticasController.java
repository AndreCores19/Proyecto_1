package app.Controllers;

import app.DTO.ResultadoEstadisticaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class EstadisticasController {
    // Objetos de Recursos
    @FXML private AnchorPane anpRecursos;
    @FXML private TitledPane titlRecursos;
    @FXML private Button btnCargarfechaR;
    @FXML private DatePicker dteDesdeR;
    @FXML private DatePicker dteHastaR;
    @FXML private Label lblFechaDesdeR;
    @FXML private Label lblFechaHastaR;
    @FXML private TableView<ResultadoEstadisticaDTO> tblCategoria;
    @FXML private ImageView imgRecursos;

    // Objetos de Actividades
    @FXML private AnchorPane anpActividades;
    @FXML private TitledPane titlActividades;
    @FXML private Button btnCargarfechaA;
    @FXML private DatePicker dteDesdeA;
    @FXML private DatePicker dteHastaA;
    @FXML private Label lblFechaDesdeA;
    @FXML private Label lblFechaHastaA;
    @FXML private TableView<ResultadoEstadisticaDTO> tblCategoriaActividades;
    @FXML private ImageView imgActividades;


}
