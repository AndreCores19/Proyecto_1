package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("/app/ui/login-view.fxml"));
        escenarioPrincipal.setTitle("Nombre app");
        escenarioPrincipal.setScene(new Scene(raiz, 348, 363));
        escenarioPrincipal.show();
    }
    public static void main(String[] args){ launch(args); }
}
