package app.ui;

import app.DTO.FuncionarioDTO;
import app.Logica.GeneradorReportePDFLogica;
import app.Servicios.ServicioFuncionario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import java.util.List;

public class FuncionarioViewController {
    @FXML private TextField txtFieldBusqID;
    @FXML private TextField txtFieldBusqNombre;
    @FXML private Button btnBusc;
    @FXML private Button btnImpri;

    @FXML private TextField txtFIDFunc;
    @FXML private TextField txtFNombFunc;
    @FXML private TextField txtFTelfFunc;
    @FXML private Button btnGuardarFunc;
    @FXML private Button btnBorrarFunc;
    @FXML private Button btnLimpiarFunc;

    @FXML private TableView<FuncionarioDTO> tvFuncionarios;
    @FXML private TableColumn<FuncionarioDTO, String> tcIDFunc;
    @FXML private TableColumn<FuncionarioDTO, String> tcNombFunc;
    @FXML private TableColumn<FuncionarioDTO, String> tcTelFunc;

    private ServicioFuncionario servicioFuncionario = new ServicioFuncionario();
    private ObservableList<FuncionarioDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tcIDFunc.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcNombFunc.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcTelFunc.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tvFuncionarios.setItems(datosTabla);

        tvFuncionarios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtFIDFunc.setText(seleccionado.getId());
                txtFNombFunc.setText(seleccionado.getNombre());
                txtFTelfFunc.setText(seleccionado.getTelefono());
            }
        });
        btnGuardarFunc.setOnAction(event -> guardar());
        btnBorrarFunc.setOnAction(event -> borrar());
        btnLimpiarFunc.setOnAction(event -> limpiar());
        btnBusc.setOnAction(event -> buscar());
        btnImpri.setOnAction(event -> imprimir());
    }

    private void cargarTabla() {
        List<FuncionarioDTO> lista = servicioFuncionario.listarTodos();
        datosTabla.setAll(lista);
        tvFuncionarios.setItems(datosTabla);
    }

    private void guardar() {
        String id = txtFIDFunc.getText();
        String nombre = txtFNombFunc.getText();
        String telefono = txtFTelfFunc.getText();

        try {
            // Intentamos ver si ya existe, para decidir si es alta o modificación
            FuncionarioDTO existente = null;
            try {
                existente = servicioFuncionario.buscarPorId(id);
            } catch (Exception ignorado) {
                // No existe, entonces es un alta nueva
            }

            if (existente == null) {
                servicioFuncionario.agregar(new FuncionarioDTO(id, null, null, nombre, telefono));
            } else {
                existente.setNombre(nombre);
                existente.setTelefono(telefono);
                servicioFuncionario.modificar(existente);
            }

            mostrarExito("Funcionario guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void borrar() {
        FuncionarioDTO seleccionado = tvFuncionarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar un funcionario de la tabla para borrar.");
            return;
        }
        try {
            servicioFuncionario.eliminar(seleccionado.getId());
            mostrarExito("Funcionario eliminado.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiar() {
        txtFIDFunc.clear();
        txtFNombFunc.clear();
        txtFTelfFunc.clear();
        tvFuncionarios.getSelectionModel().clearSelection();
    }

    private void buscar() {
        String id = txtFieldBusqID.getText();
        String nombre = txtFieldBusqNombre.getText();

        if (!id.isEmpty()) {
            try {
                FuncionarioDTO encontrado = servicioFuncionario.buscarPorId(id);
                agregarSiNoExiste(encontrado);
            } catch (Exception e) {
                mostrarError(e.getMessage());
            }
        } else if (!nombre.isEmpty()) {
            List<FuncionarioDTO> resultado = servicioFuncionario.buscarPorNombre(nombre);
            for (FuncionarioDTO f : resultado) {
                agregarSiNoExiste(f);
            }
        } else {
            cargarTabla();
        }
    }

    private void agregarSiNoExiste(FuncionarioDTO nuevo) {
        for (FuncionarioDTO f : datosTabla) {
            if (f.getId().equals(nuevo.getId())) {
                return;
            }
        }
        datosTabla.add(nuevo);
    }

    private void imprimir() {
        List<String> encabezados = List.of("ID", "Nombre", "Teléfono");
        List<List<String>> filas = new java.util.ArrayList<>();

        for (FuncionarioDTO f : datosTabla) {
            filas.add(List.of(f.getId(), f.getNombre(), f.getTelefono()));
        }

        String rutaPdf = "Data/reporteFuncionarios.pdf";

        try {
            GeneradorReportePDFLogica.generar("Listado de Funcionarios", encabezados, filas, "Data/reporteFuncionarios.pdf");
            java.io.File archivoPdf = new java.io.File(rutaPdf);
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(archivoPdf);
            }
        } catch (Exception e) {
            mostrarError("Error al generar el reporte: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
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