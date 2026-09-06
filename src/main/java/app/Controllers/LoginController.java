package app.Controllers;

import app.DTO.UsuarioDTO;
import app.Servicios.ServicioUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class LoginController {
    @FXML private TextField txtIdLogin;
    @FXML private PasswordField txtPwdLogin;
    @FXML private Button btnLogin;
    @FXML private Button btnCancelar;
    @FXML private Hyperlink hyperPwd;

    private ServicioUsuario servicioUsuario = new ServicioUsuario();
    @FXML
    private void initialize() {
        btnLogin.setOnAction(event -> intentarLogin());
        hyperPwd.setOnAction(event -> irACambiarClave(event));
    }

    private void intentarLogin() {
        String id = txtIdLogin.getText();
        String clave = txtPwdLogin.getText();
        try {
            UsuarioDTO usuario = servicioUsuario.iniciarSesion(id, clave);
            SesionActual.setUsuarioActual(usuario);
            System.out.println("Login exitoso: " + usuario.getId() + "  (rol: " + usuario.getRol() + ")");
            // acá después vamos a navegar al menú principal
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void irACambiarClave(javafx.event.ActionEvent event) {
        String id = txtIdLogin.getText();
        String clave = txtPwdLogin.getText();
        try {
            UsuarioDTO usuario = servicioUsuario.iniciarSesion(id, clave);
            SesionActual.setUsuarioActual(usuario);

            javafx.scene.Parent raiz = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/app/ui/cambiarClave-view.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz);

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de inicio de sesión. ");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}