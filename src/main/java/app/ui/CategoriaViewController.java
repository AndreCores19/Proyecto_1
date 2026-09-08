package app.ui;

import app.DTO.CategoriaDTO;
import app.Servicios.ServicioCategoria;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CategoriaViewController {

    @FXML private TextField txtFieldBusqDesc;
    @FXML private Button btnBuscDesc;
    @FXML private Button btnImpriDesc;

    @FXML private TextField txtFIDCateg;
    @FXML private TextField txtFDesCateg;
    @FXML private Button btnGuardarFunc;
    @FXML private Button btnBorrarFunc;
    @FXML private Button btnLimpiarFunc;

    @FXML private TableView<CategoriaDTO> tvCategorias;
    @FXML private TableColumn<CategoriaDTO, String> tcIDCateg;
    @FXML private TableColumn<CategoriaDTO, String> tcDesCateg;

    private ServicioCategoria servicioCategoria = new ServicioCategoria();
    private ObservableList<CategoriaDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tcIDCateg.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcDesCateg.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tvCategorias.setItems(datosTabla);

        tvCategorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtFIDCateg.setText(seleccionado.getId());
                txtFDesCateg.setText(seleccionado.getDescripcion());
            }
        });

        btnGuardarFunc.setOnAction(event -> guardar());
        btnBorrarFunc.setOnAction(event -> borrar());
        btnLimpiarFunc.setOnAction(event -> limpiar());
        btnBuscDesc.setOnAction(event -> buscar());
        btnImpriDesc.setOnAction(event -> imprimir());
    }

    private void guardar() {
        String id = txtFIDCateg.getText();
        String descripcion = txtFDesCateg.getText();

        try {
            if (id == null || id.trim().isEmpty()) {
                servicioCategoria.agregar(new CategoriaDTO(null, descripcion));
            } else {
                CategoriaDTO actualizada = servicioCategoria.buscarPorId(id);
                actualizada.setDescripcion(descripcion);
                servicioCategoria.modificar(actualizada);
            }
            mostrarExito("Categoría guardada correctamente.");
            limpiar();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void borrar() {
        CategoriaDTO seleccionada = tvCategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Debe seleccionar una categoría de la tabla para poder borrarlo borrar.");
            return;
        }
        try {
            servicioCategoria.eliminar(seleccionada.getId());
            mostrarExito("Categoría eliminada.");
            datosTabla.remove(seleccionada);
            limpiar();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiar() {
        txtFIDCateg.clear();
        txtFDesCateg.clear();
        tvCategorias.getSelectionModel().clearSelection();
    }

    private void buscar() {
        String descripcion = txtFieldBusqDesc.getText();
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return;
        }
        List<CategoriaDTO> resultado = servicioCategoria.buscarPorDescripcion(descripcion);
        for (CategoriaDTO c : resultado) {
            agregarSiNoExiste(c);
        }
    }

    private void agregarSiNoExiste(CategoriaDTO nueva) {
        for (CategoriaDTO c : datosTabla) {
            if (c.getId().equals(nueva.getId())) {
                return;
            }
        }
        datosTabla.add(nueva);
    }

    private void imprimir() {
        List<String> encabezados = List.of("ID", "Descripción");
        List<List<String>> filas = new java.util.ArrayList<>();

        for (CategoriaDTO c : datosTabla) {
            filas.add(List.of(c.getId(), c.getDescripcion()));
        }

        String rutaPdf = "Data/reporteCategorias.pdf";

        try {
            app.Logica.GeneradorReportePDFLogica.generar("Listado de Categorías", encabezados, filas, rutaPdf);
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