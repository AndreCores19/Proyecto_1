package app.ui;

import app.DTO.CategoriaDTO;
import app.DTO.MatrizDTO;
import app.Servicios.ServicioCalendarizacionRecursos;
import app.Servicios.ServicioCategoria;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.List;

public class CalendarizacionRecursosViewController {
    @FXML private ComboBox<CategoriaDTO>cbxCategoria;
    @FXML private DatePicker dpFechafiltros;
    @FXML private Button btnCargarFiltros;
    @FXML private Button btnImprimirEnFiltros;
    @FXML private Button btnCerrarSesion;
    @FXML private TableView<MatrizDTO> tvCalendarizacion;
    @FXML private TableColumn<MatrizDTO, String> tcHora;

    private final ServicioCalendarizacionRecursos servicioCalendarizacion = new ServicioCalendarizacionRecursos();
    private final ServicioCategoria servicioCategoria = new ServicioCategoria();

    @FXML
    public void initialize() {
    tcHora.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("hora"));

    List<CategoriaDTO> categorias = servicioCategoria.listarTodas();
    cbxCategoria.setItems(FXCollections.observableArrayList(categorias));
    cbxCategoria.setCellFactory(cb-> new javafx.scene.control.ListCell<>() {
        @Override
        protected void updateItem(CategoriaDTO item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getDescripcion());
        }

    });
cbxCategoria.setButtonCell(cbxCategoria.getCellFactory().call(null));
btnCargarFiltros.setOnMouseClicked(e -> cargarMatriz());
    }

    private void cargarMatriz() {
        CategoriaDTO categoria = cbxCategoria.getValue();
        LocalDate fecha = dpFechafiltros.getValue();

        if (categoria == null || fecha == null) {
            mostrarError("Por favor, seleccione una categoría y una fecha.");
            return;
        }
        try{
            List<MatrizDTO> filas = servicioCalendarizacion.obtenerMatrizDeRecursos(categoria.getId(), fecha);
            construirColumnas(categoria);
            tvCalendarizacion.setItems(FXCollections.observableArrayList(filas));
        } catch (Exception e) {
            mostrarError("Error al cargar la matriz: " + e.getMessage());
        }
    }

    private void construirColumnas(CategoriaDTO categoria) {
        tvCalendarizacion.getColumns().setAll(tcHora);

        for (var recurso : categoria.getRecursos()){
            TableColumn<MatrizDTO, String> columnaRecurso = new TableColumn<>(recurso.getDescripcion());
            String numActivo = recurso.getNumActivo();
            columnaRecurso.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstadoPorRecurso().get(numActivo)));

            tvCalendarizacion.getColumns().add(columnaRecurso);
        }
    }


    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}