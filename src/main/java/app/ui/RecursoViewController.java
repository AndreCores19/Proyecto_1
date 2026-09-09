package app.ui;

import app.DTO.CategoriaDTO;
import app.DTO.RecursoDTO;
import app.Servicios.ServicioCategoria;
import app.Servicios.ServicioRecurso;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class RecursoViewController {

    @FXML private ComboBox<CategoriaDTO> CombBRecur;
    @FXML private TextField txtFieldDescRecur;
    @FXML private Button btnBuscRecur;
    @FXML private Button btnImpriRecur;

    @FXML private TextField txtFIDRecur;
    @FXML private ComboBox<CategoriaDTO> CombRecur;
    @FXML private TextField txtFRecur;
    @FXML private Button btnGuardarFunc;
    @FXML private Button btnBorrarFunc;
    @FXML private Button btnLimpiarFunc;

    @FXML private TableView<RecursoDTO> tvRecursos;
    @FXML private TableColumn<RecursoDTO, String> tcIDRecur;
    @FXML private TableColumn<RecursoDTO, String> tccategRecur;
    @FXML private TableColumn<RecursoDTO, String> tcDesRecur;

    private ServicioRecurso servicioRecurso = new ServicioRecurso();
    private ServicioCategoria servicioCategoria = new ServicioCategoria();
    private ObservableList<RecursoDTO> datosTabla = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        tcIDRecur.setCellValueFactory(new PropertyValueFactory<>("numActivo"));
        tccategRecur.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoria().getDescripcion())
        );
        tcDesRecur.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tvRecursos.setItems(datosTabla);

        configurarComboBox(CombBRecur);
        configurarComboBox(CombRecur);
        cargarCategoriasEnCombos();

        tvRecursos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtFIDRecur.setText(seleccionado.getNumActivo());
                txtFRecur.setText(seleccionado.getDescripcion());
                CombRecur.setValue(seleccionado.getCategoria());
            }
        });

        btnGuardarFunc.setOnAction(event -> guardar());
        btnBorrarFunc.setOnAction(event -> borrar());
        btnLimpiarFunc.setOnAction(event -> limpiar());
        btnBuscRecur.setOnAction(event -> buscar());
        btnImpriRecur.setOnAction(event -> imprimir());
    }

    private void configurarComboBox(ComboBox<CategoriaDTO> combo) {
        combo.setConverter(new StringConverter<CategoriaDTO>() {
            @Override
            public String toString(CategoriaDTO categoria) {
                return categoria == null ? "" : categoria.getDescripcion();
            }
            @Override
            public CategoriaDTO fromString(String string) {
                return null; // no hace falta, el usuario no escribe texto libre acá
            }
        });
    }

    private void cargarCategoriasEnCombos() {
        List<CategoriaDTO> categorias = servicioCategoria.listarTodas();
        ObservableList<CategoriaDTO> observable = FXCollections.observableArrayList(categorias);
        CombBRecur.setItems(observable);
        CombRecur.setItems(observable);
    }

    private void guardar() {
        String id = txtFIDRecur.getText();
        String descripcion = txtFRecur.getText();
        CategoriaDTO categoriaElegida = CombRecur.getValue();

        try {
            RecursoDTO existente = null;
            try {
                existente = servicioRecurso.buscarPorNumActivo(id);
            } catch (Exception ignorado) {
            }

            if (existente == null) {
                servicioRecurso.agregar(new RecursoDTO(id, categoriaElegida, descripcion));
            } else {
                existente.setDescripcion(descripcion);
                existente.setCategoria(categoriaElegida);
                servicioRecurso.modificar(existente);
            }

            mostrarExito("Recurso guardado correctamente.");
            limpiar();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void borrar() {
        RecursoDTO seleccionado = tvRecursos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar un recurso de la tabla para borrar.");
            return;
        }
        try {
            servicioRecurso.eliminar(seleccionado.getNumActivo());
            mostrarExito("Recurso eliminado.");
            datosTabla.remove(seleccionado);
            limpiar();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiar() {
        txtFIDRecur.clear();
        txtFRecur.clear();
        CombRecur.setValue(null);
        tvRecursos.getSelectionModel().clearSelection();
    }

    private void buscar() {
        CategoriaDTO categoriaFiltro = CombBRecur.getValue();
        String descripcion = txtFieldDescRecur.getText();

        List<RecursoDTO> resultado;
        if (categoriaFiltro != null) {
            resultado = servicioRecurso.filtrarPorCategoria(categoriaFiltro.getId());
        } else {
            resultado = servicioRecurso.listarTodos();
        }

        for (RecursoDTO r : resultado) {
            if (descripcion == null || descripcion.trim().isEmpty()
                    || r.getDescripcion().toLowerCase().contains(descripcion.toLowerCase())) {
                agregarSiNoExiste(r);
            }
        }
    }

    private void agregarSiNoExiste(RecursoDTO nuevo) {
        for (RecursoDTO r : datosTabla) {
            if (r.getNumActivo().equals(nuevo.getNumActivo())) {
                return;
            }
        }
        datosTabla.add(nuevo);
    }

    private void imprimir() {
        List<String> encabezados = List.of("ID", "Categoría", "Descripción");
        List<List<String>> filas = new java.util.ArrayList<>();

        for (RecursoDTO r : datosTabla) {
            filas.add(List.of(r.getNumActivo(), r.getCategoria().getDescripcion(), r.getDescripcion()));
        }

        String rutaPdf = "Data/reporteRecursos.pdf";

        try {
            app.Logica.GeneradorReportePDFLogica.generar("Listado de Recursos", encabezados, filas, rutaPdf);
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