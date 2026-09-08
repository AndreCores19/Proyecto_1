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
        Scene escena = new Scene(raiz);
        Stage stage = new Stage();
        stage.setScene(escena);
        stage.setTitle("Prueba de Interfaz");
        stage.show();
    }
    public static void main(String[] args){ launch(args); }
}

//Hay que decidir un nombre pa esta vaina?

// di, yo diría que si jajaja