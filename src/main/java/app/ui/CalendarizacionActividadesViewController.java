package app.ui;
import app.Controllers.SesionActual;
import app.DTO.CeldaActividadDTO;
import app.Logica.GeneradorReportePDFLogica;
import app.Servicios.ServicioProgramacion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarizacionActividadesViewController {
    private ServicioProgramacion servicioProgramacion = new ServicioProgramacion();

    @FXML private TableView<FilaCalendario> tblCalendario;
    @FXML private TableColumn tcHora;
    @FXML private TableColumn tcFechaLun;
    @FXML private TableColumn tcFechaMart;
    @FXML private TableColumn tcFechaMier;
    @FXML private TableColumn tcFechaJuev;
    @FXML private TableColumn tcFechaVier;
    @FXML private TableColumn tcFechaSab;
    @FXML private TableColumn tcFechaDom;
    @FXML private DatePicker dteFechaReferencia;

    @FXML private Button btnCerrarSesion;

    @FXML
    public void initialize() {
        tcHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        tcFechaLun.setCellValueFactory(new PropertyValueFactory<>("lunes"));
        tcFechaMart.setCellValueFactory(new PropertyValueFactory<>("martes"));
        tcFechaMier.setCellValueFactory(new PropertyValueFactory<>("miercoles"));
        tcFechaJuev.setCellValueFactory(new PropertyValueFactory<>("jueves"));
        tcFechaVier.setCellValueFactory(new PropertyValueFactory<>("viernes"));
        tcFechaSab.setCellValueFactory(new PropertyValueFactory<>("sabado"));
        tcFechaDom.setCellValueFactory(new PropertyValueFactory<>("domingo"));

        btnCerrarSesion.setOnAction(event -> cerrarSesion(event));

    }

    @FXML
    private void cargarCalendario() {
        LocalDate fechaReferencia = dteFechaReferencia.getValue();
        CeldaActividadDTO[][] matriz = servicioProgramacion.matrizReservas(fechaReferencia);

        List<FilaCalendario> filas = new ArrayList<>();

        for (int hora = 0; hora < 24; hora++) {
            FilaCalendario fila = new FilaCalendario();
            fila.setHora(hora + ":00");

            fila.setLunes(convertirCelda(matriz[hora][0]));
            fila.setMartes(convertirCelda(matriz[hora][1]));
            fila.setMiercoles(convertirCelda(matriz[hora][2]));
            fila.setJueves(convertirCelda(matriz[hora][3]));
            fila.setViernes(convertirCelda(matriz[hora][4]));
            fila.setSabado(convertirCelda(matriz[hora][5]));
            fila.setDomingo(convertirCelda(matriz[hora][6]));

            filas.add(fila);
        }

        tblCalendario.setItems(FXCollections.observableArrayList(filas));
    }
    private String convertirCelda(CeldaActividadDTO celda) {
        if (celda == null) {
            return "";
        }
        return celda.getNombreActividad() + " - " + celda.getNombreFuncionario();
    }

    @FXML
    private void imprimirCalendario() {
        if (tblCalendario.getItems().isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING, "No hay datos cargados. Presione 'Cargar' primero.");
            alerta.showAndWait();
            return;
        }
        List<String> encabezados = List.of(
                tcHora.getText(), tcFechaLun.getText(), tcFechaMart.getText(), tcFechaMier.getText(),
                tcFechaJuev.getText(), tcFechaVier.getText(), tcFechaSab.getText(), tcFechaDom.getText()
        );
        List<List<String>> filas = new ArrayList<>();
        for (FilaCalendario f : tblCalendario.getItems()) {
            filas.add(List.of(
                    f.getHora(), f.getLunes(), f.getMartes(), f.getMiercoles(),
                    f.getJueves(), f.getViernes(), f.getSabado(), f.getDomingo()
            ));
        }
        GeneradorReportePDFLogica.generar("Programación de Actividades", encabezados, filas, "Data/programacion_actividades.pdf");
        try {
            Desktop.getDesktop().open(new File("Data/programacion_actividades.pdf"));
        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "El PDF se generó, pero no se pudo abrir automáticamente.");
            alerta.showAndWait();
        }
    }

    @FXML
    private void limpiarCalendario() {
        tblCalendario.getItems().clear();
        dteFechaReferencia.setValue(null);
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
