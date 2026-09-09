package app.ui;
import app.Controllers.SesionActual;
import javafx.scene.control.Button;
import javafx.fxml.FXML;



public class CalendarizacionRecursosViewController {
    @FXML private Button btnCerrarSesion;

    @FXML
    public void initialize() {
        btnCerrarSesion.setOnAction(event -> cerrarSesion(event));
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
