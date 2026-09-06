package app.Controllers;

import app.DTO.UsuarioDTO;
import app.Servicios.ServicioUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;

public class CambiarClaveController {

    @FXML private PasswordField pwdActual;
    @FXML private PasswordField pwdNueva;
    @FXML private PasswordField pwdConf;
    @FXML private Button btnAceptar;
    @FXML private Button btnCancel;

    private ServicioUsuario servicioUsuario = new ServicioUsuario();

    @FXML
    private void initialize() {
        btnAceptar.setOnAction(event -> intentarCambiar());
    }

    private void intentarCambiar() {
        UsuarioDTO usuario = SesionActual.getUsuarioActual();
        String claveActual = pwdActual.getText();
        String claveNueva = pwdNueva.getText();
        String confirmacion = pwdConf.getText();

        if (!claveNueva.equals(confirmacion)) {
            mostrarError("La nueva clave y la confirmación no coinciden.");
            return;
        }

        try {
            servicioUsuario.cambiarClave(usuario, claveActual, claveNueva);
            mostrarExito("Clave actualizada correctamente.");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
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
        alerta.setTitle("Éxito");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}