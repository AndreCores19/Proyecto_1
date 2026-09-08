package app.ui;

import app.DTO.CategoriaDTO;
import app.DTO.RecursoDTO;
import app.Logica.GeneradorReportePDFLogica;
import app.Servicios.ServicioCategoria;
import app.Servicios.ServicioRecurso;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
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
        tccategRecur.setCellValueFactory(cellData -> {
            CategoriaDTO cat = cellData.getValue().getCategoria();
            return new SimpleStringProperty(cat != null ? cat.getDescripcion() : "");
        });
        tcDesRecur.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tvRecursos.setItems(datosTabla);

        StringConverter<CategoriaDTO> converter = new StringConverter<>() {
            @Override
            public String toString(CategoriaDTO cat) {
                return cat != null ? cat.getDescripcion() : "";
            }

            @Override
            public CategoriaDTO fromString(String string) {
                return null;
            }
        };

        CombBRecur.setConverter(converter);
        CombRecur.setConverter(converter);

        cargarCategorias();
        cargarTabla();

        tvRecursos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtFIDRecur.setText(seleccionado.getNumActivo());
                txtFRecur.setText(seleccionado.getDescripcion());

                // Seleccionar la categoría correspondiente en el ComboBox
                if (seleccionado.getCategoria() != null) {
                    for (CategoriaDTO cat : CombRecur.getItems()) {
                        if (cat.getId() != null && cat.getId().equals(seleccionado.getCategoria().getId())) {
                            CombRecur.getSelectionModel().select(cat);
                            break;
                        }
                    }
                } else {
                    CombRecur.getSelectionModel().clearSelection();
                }
            }
        });
        btnGuardarFunc.setOnAction(event -> guardar());
        btnBorrarFunc.setOnAction(event -> borrar());
        btnLimpiarFunc.setOnAction(event -> limpiar());
        btnBuscRecur.setOnAction(event -> buscar());
        btnImpriRecur.setOnAction(event -> imprimir());
    }

    private void cargarCategorias() {
        try {
            List<CategoriaDTO> categorias = servicioCategoria.listarTodos();
            CombBRecur.setItems(FXCollections.observableArrayList(categorias));
            CombRecur.setItems(FXCollections.observableArrayList(categorias));
        } catch (Exception e) {
            mostrarError("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarTabla() {
        try {
            List<RecursoDTO> lista = servicioRecurso.listarTodos();
            datosTabla.setAll(lista);
        } catch (Exception e) {
            mostrarError("Error al cargar listado de recursos: " + e.getMessage());
        }
    }

    private void guardar() {
        String numActivo = txtFIDRecur.getText();
        CategoriaDTO categoria = CombRecur.getValue();
        String descripcion = txtFRecur.getText();

        if (numActivo == null || numActivo.trim().isEmpty()) {
            mostrarError("El ID del recurso es requerido.");
            return;
        }

        try {
            RecursoDTO existente = null;
            try {
                existente = servicioRecurso.buscarPorNumActivo(numActivo);
            } catch (Exception ignorado) {
                // No existe, se procesará como nuevo
            }

            if (existente == null) {
                servicioRecurso.agregar(new RecursoDTO(numActivo, categoria, descripcion));
            } else {
                existente.setCategoria(categoria);
                existente.setDescripcion(descripcion);
                servicioRecurso.modificar(existente);
            }

            mostrarExito("Recurso guardado correctamente.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void borrar() {
        RecursoDTO seleccionado = tvRecursos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar un recurso de la tabla para poder borrarlo borrar.");
            return;
        }
        try {
            servicioRecurso.eliminar(seleccionado.getNumActivo());
            mostrarExito("Recurso eliminado.");
            limpiar();
            cargarTabla();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiar() {
        txtFIDRecur.clear();
        txtFRecur.clear();
        CombRecur.getSelectionModel().clearSelection();
        tvRecursos.getSelectionModel().clearSelection();
    }

    private void buscar() {
        CategoriaDTO catSeleccionada = CombBRecur.getValue();
        String descBusqueda = txtFieldDescRecur.getText();

        try {
            List<RecursoDTO> resultados;
            if (catSeleccionada != null && catSeleccionada.getId() != null) {
                resultados = servicioRecurso.filtrarPorCategoria(catSeleccionada.getId());
            } else {
                resultados = servicioRecurso.listarTodos();
            }

            if (descBusqueda != null && !descBusqueda.trim().isEmpty()) {
                datosTabla.setAll(
                        resultados.stream()
                                .filter(r -> r.getDescripcion() != null &&
                                        r.getDescripcion().toLowerCase().contains(descBusqueda.toLowerCase()))
                                .toList()
                );
            } else {
                datosTabla.setAll(resultados);
            }
        } catch (Exception e) {
            mostrarError("Error al realizar la búsqueda: " + e.getMessage());
        }
    }

    private void imprimir() {
        List<String> encabezados = List.of("ID", "Categoría", "Descripción");
        List<List<String>> filas = new ArrayList<>();

        for (RecursoDTO r : datosTabla) {
            String nomCategoria = (r.getCategoria() != null) ? r.getCategoria().getDescripcion() : "";
            filas.add(List.of(
                    r.getNumActivo() != null ? r.getNumActivo() : "",
                    nomCategoria,
                    r.getDescripcion() != null ? r.getDescripcion() : ""
            ));
        }

        String rutaPdf = "Data/reporteRecursos.pdf";

        try {
            GeneradorReportePDFLogica.generar("Listado de Recursos", encabezados, filas, rutaPdf);
            File archivoPdf = new File(rutaPdf);
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