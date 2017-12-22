package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConnectView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("NAO Verbindung einrichten"); // zeigt "NAO Verbindung einrichten" an!
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.show();

        // Controller aus fxml laden
        ConnectController connController = (ConnectController)loader.getController();
        // Main Objekt an Controller geben, damit dieser Zugriff hat
        connController.setApplication(this);

        stage = primaryStage;
    }

    public void startMainMenu() throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("MainMenu.fxml"));
        stage.setTitle("NAO Dashboard");
        stage.setScene(new Scene(root, 900, 700));
        stage.show();

        //MainMenuController mainMenuController = loader.getController();
        //mainMenuController.setApplication(this);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
