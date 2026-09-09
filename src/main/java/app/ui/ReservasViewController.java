package app.ui;

import app.Controllers.SesionActual;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;


import app.Servicios.ServicioReservas;
import app.DTO.ReservaDTO;
import app.DTO.AdministradorDTO;
import app.DTO.CategoriaDTO;
import app.DTO.RecursoDTO;
import app.DTO.FuncionarioDTO;

public class ReservasViewController {
    @FXML private TextField txtFFrase;
    @FXML private TextField txtFActividad;
    @FXML private Button btnExtraerAI;

    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbxHoraInicio;
    @FXML private ComboBox<String> cbxHoraFin;

    @FXML private Button btnCatReservar;
    @FXML private Button btnCatCancelarSeleccionada;
    @FXML private Button btnLimpiarSeleccion;
    @FXML private ListView <CategoriaDTO> lvCategorias;

    @FXML private Button btnCerrarSesion;

    @FXML private TableView <ReservaDTO> tvMisReservas;
    @FXML private TableColumn <ReservaDTO,String> tcReservasId;
    @FXML private TableColumn <ReservaDTO,String> tcReservasActividad;
    @FXML private TableColumn <ReservaDTO,String> tcReservasFecha;
    @FXML private TableColumn <ReservaDTO,String> tcReservasHorario;
    @FXML private TableColumn <ReservaDTO,String> tcReservasRecursos;
    @FXML private TableColumn <ReservaDTO,String> tcReservasEstado;
    @FXML private Button btnImprimirReservas;

    private ServicioReservas servicioReservas = new ServicioReservas();

    @FXML
    public void initialize() {
        btnExtraerAI.setOnAction(event -> extraerIA());
        btnCatReservar.setOnAction(event -> reservarCategoria());
        btnCatCancelarSeleccionada.setOnAction(event -> cancelarReservaSeleccionada());
        btnLimpiarSeleccion.setOnAction(event -> limpiarSeleccion());
        btnImprimirReservas.setOnAction(event -> imprimirReservas());
        btnCerrarSesion.setOnAction(event -> cerrarSesion(event));

        cargarHorario();
        configurarTablaReservas();
        cargarCategorias();
        cargarMisReservas();
    }

    private void extraerIA() {
        // Lógica para extraer la información de la actividad basada en la frase ingresada
    }

    private void extraerActividad() {
        // Lógica para extraer la actividad basada en la frase ingresada
    }

    private void cargarHorario() {
        // Lógica para cargar el horario en los ComboBox
    }

    private void configurarTablaReservas() {
        // Lógica para configurar la TableView de reservas
    }

    private void imprimirReservas() {
        // Lógica para imprimir las reservas del usuario
    }

    private void cargarCategorias() {
        // Lógica para cargar categorías en la ListView
    }

    private void cargarMisReservas() {
        // Lógica para cargar las reservas del usuario en la TableView
    }

    private void reservarCategoria() {
        // Lógica para reservar la categoría seleccionada
    }

    private void cancelarReservaSeleccionada() {
        // Lógica para cancelar la reserva seleccionada
    }

    private void limpiarSeleccion() {
        // Lógica para limpiar la selección de categorías
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cerrarSesion(javafx.event.ActionEvent event) {
        SesionActual.setUsuarioActual(null);
        try {
            javafx.scene.Parent raiz = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/app/ui/login-view.fxml")
            );
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
