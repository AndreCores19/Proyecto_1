package app.ui;

import app.Controllers.SesionActual;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import app.Servicios.ServicioReservas;
import app.Servicios.ServicioCategoria;
import app.DTO.ReservaDTO;
import app.DTO.AdministradorDTO;
import app.DTO.CategoriaDTO;
import app.DTO.ResultadoDeAsignacionDTO;
import app.DTO.RecursoDTO;
import app.DTO.FuncionarioDTO;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @FXML private TableView <ReservaDTO> tvMisReservas;
    @FXML private TableColumn <ReservaDTO,String> tcReservasId;
    @FXML private TableColumn <ReservaDTO,String> tcReservasActividad;
    @FXML private TableColumn <ReservaDTO,String> tcReservasFecha;
    @FXML private TableColumn <ReservaDTO,String> tcReservasHorario;
    @FXML private TableColumn <ReservaDTO,String> tcReservasRecursos;
    @FXML private TableColumn <ReservaDTO,String> tcReservasEstado;
    @FXML private Button btnImprimirReservas;

    private ServicioReservas servicioReservas = new ServicioReservas();
    private ServicioCategoria servicioCategoria = new ServicioCategoria();
    private String idFuncionarioActual;


    @FXML
    public void initialize() {
        idFuncionarioActual = SesionActual.getUsuarioActual().getId();
        btnExtraerAI.setOnAction(event -> extraerIA());
        btnCatReservar.setOnAction(event -> reservarCategoria());
        btnCatCancelarSeleccionada.setOnAction(event -> cancelarReservaSeleccionada());
        btnLimpiarSeleccion.setOnAction(event -> limpiarSeleccion());
        btnImprimirReservas.setOnAction(event -> imprimirReservas());
        tvMisReservas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtFActividad.setText(seleccionado.getActividad());
                dpFecha.setValue(seleccionado.getFecha());
                cbxHoraInicio.setValue(seleccionado.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
                cbxHoraFin.setValue(seleccionado.getHoraFin().format(DateTimeFormatter.ofPattern("HH:mm")));
               // lvCategorias.getSelectionModel().clearSelection();
                  //  lvCategorias.getSelectionModel().select(categoria);
               // }
                //lvCategorias.setItems(misCategorias););
            }
        });

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
        ObservableList<String> horas = FXCollections.observableArrayList();
        for (int i = 7; i < 21; i++) {
            horas.add(String.format("%02d:00", i));
            horas.add(String.format("%02d:30", i));
        }
        cbxHoraInicio.setItems(horas);
        cbxHoraFin.setItems(horas);
}

    private void configurarTablaReservas() {
        tcReservasId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        tcReservasActividad.setCellValueFactory(new PropertyValueFactory<>("actividad"));
        tcReservasFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFecha().toString()));
        tcReservasHorario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"))));
        tcReservasRecursos.setCellValueFactory(data -> new SimpleStringProperty(String.join(", ", data.getValue().getIdsRecursosAsignados())));
        tcReservasEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void imprimirReservas() {
        // Lógica para imprimir las reservas del usuario
    }

    private void cargarCategorias() {
        List<CategoriaDTO> categorias = servicioCategoria.listarTodas();
        ObservableList<CategoriaDTO> datosCategorias = FXCollections.observableArrayList(categorias);
        lvCategorias.setItems(datosCategorias);
        lvCategorias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        lvCategorias.setCellFactory(lv -> new ListCell<CategoriaDTO>() {
            @Override
            protected void updateItem(CategoriaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getId() + " - " + item.getDescripcion());
                }
            }
        });
    }

    private void cargarMisReservas() {
        if(idFuncionarioActual == null) return;
        try {
            List<ReservaDTO> reservas = servicioReservas.listarReservasPorFuncionario(idFuncionarioActual);
            ObservableList<ReservaDTO> misReservas = FXCollections.observableArrayList(reservas);
            tvMisReservas.setItems(misReservas);
        } catch (Exception e) {
            mostrarError("No se pudieron cargar las reservas: " + e.getMessage());
        }
    }

    private void reservarCategoria() {
        try {
        List<String> idsCategorias = lvCategorias.getSelectionModel().getSelectedItems().stream().map(CategoriaDTO::getId).collect(Collectors.toList());
         if (txtFActividad.getText().isEmpty()) {
            mostrarError("La actividad no puede estar vacía.");
            return;
         }
         if (dpFecha.getValue() == null) {
            mostrarError("Debe seleccionar una fecha.");
            return;
         }
         if (cbxHoraInicio.getValue() == null) {
            mostrarError("Debe seleccionar una hora de inicio.");
            return;
         }
         if (cbxHoraFin.getValue() == null) {
            mostrarError("Debe seleccionar una hora de fin.");
            return;
         }
         if (idsCategorias.isEmpty()) {
            mostrarError("Debe seleccionar al menos una categoría.");
            return;
         }
         if (LocalTime.parse(cbxHoraFin.getValue()).isBefore(LocalTime.parse(cbxHoraInicio.getValue()))) {
            mostrarError("La hora de fin no puede ser anterior a la hora de inicio.");
            return;
         }
         if (LocalTime.parse(cbxHoraFin.getValue()).equals(LocalTime.parse(cbxHoraInicio.getValue()))) {
            mostrarError("La hora de fin no puede ser igual a la hora de inicio.");
            return;
         }
         if (dpFecha.getValue().isBefore(java.time.LocalDate.now())) {
            mostrarError("La fecha de la reserva no puede ser anterior a la fecha actual.");
            return;
         }
         if (dpFecha.getValue().equals(java.time.LocalDate.now()) && LocalTime.parse(cbxHoraInicio.getValue()).isBefore(LocalTime.now())) {
            mostrarError("La hora de inicio/fin no puede ser anterior a la hora actual.");
            return;
         }
         if (dpFecha.getValue().equals(java.time.LocalDate.now()) && LocalTime.parse(cbxHoraFin.getValue()).isBefore(LocalTime.now())) {
            mostrarError("La hora de fin no puede ser anterior a la hora actual.");
            return;
         }
        ReservaDTO nuevaReserva =
                new ReservaDTO(
                        null,
                        txtFActividad.getText(),
                        dpFecha.getValue(),
                        LocalTime.parse(cbxHoraInicio.getValue()),
                        LocalTime.parse(cbxHoraFin.getValue()),
                        idsCategorias,
                        idFuncionarioActual,
                        "PENDIENTE",
                        new ArrayList<>()
                );
        ResultadoDeAsignacionDTO resultado = servicioReservas.reservarEspacio(nuevaReserva);
        if(resultado.isExitosa()) {
            mostrarExito("Reserva realizada con éxito");
        } else {
            resultado.getIdsCategoriasNoDisponibles().forEach(idCategoria -> {
                try {
                    CategoriaDTO categoria = servicioCategoria.buscarPorId(idCategoria);
                    mostrarError("No hay recursos disponibles para la categoría: " + categoria.getId() + " - " + categoria.getDescripcion());
                } catch (Exception e) {
                    mostrarError("No se pudo encontrar la categoría con id: " + idCategoria);
                }
            });
        }
        limpiarSeleccion();
        cargarMisReservas();
        } catch (Exception e) {
            mostrarError("No se pudo realizar la reserva: " + e.getMessage());
        }
    }

    private void cancelarReservaSeleccionada() {
        ReservaDTO seleccionada = tvMisReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccione una reserva de la tabla para cancelar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea cancelar la reserva seleccionada?");
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    servicioReservas.cancelarReserva(seleccionada.getIdReserva());
                    mostrarExito("Reserva cancelada con éxito.");
                    cargarMisReservas();
                } catch (Exception e) {
                    mostrarError("No se pudo cancelar la reserva: " + e.getMessage());
                }
            }
        });
    }

    private void limpiarSeleccion() {
        txtFActividad.clear();
        txtFFrase.clear();
        dpFecha.setValue(null);
        cbxHoraInicio.setValue(null);
        cbxHoraFin.setValue(null);
        lvCategorias.getSelectionModel().clearSelection();

    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void mostrarExito(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
