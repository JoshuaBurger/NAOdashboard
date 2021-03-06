package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // View laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root = loader.load();
        // View in Stage setzen
        stage.setTitle("NAO Dashboard");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        // Wir muessen explizit terminieren, da sonst Threads offenbleiben (NAO-Verbindung? Timer? Wer weiß...)
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}