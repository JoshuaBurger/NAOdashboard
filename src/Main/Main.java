package Main;

import com.aldebaran.qi.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage stage;
    private MainMenuController mainMenuController;
    private ConnectController connController;


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        startConnectMenu();
        stage.show();
    }

    public void startConnectMenu() throws Exception {
        // JavaFX Windows aus Datei laden
        FXMLLoader loader = setSceneContent("ConnectView.fxml", "NAO connection management");

        if ( connController == null ) {
            // Beim ersten Mal Controller aus FXML laden
            connController = loader.getController();
            connController.setMainClass(this);
        }
        else {
            // Wenn schonmal geoeffnet, alten Controller behalten, um Daten zu behalten.
            loader.setController(connController);
            connController.setDataToView();
        }
    }

    public void startMainMenu() throws Exception {
        // JavaFX Windows aus Datei laden
        FXMLLoader loader = setSceneContent("MainMenu.fxml", "NAO dashboard");

        if ( mainMenuController == null ) {
            // Beim ersten Mal Controller aus FXML laden
            mainMenuController = loader.getController();
            mainMenuController.setMainClass(this);
        }
        else {
            // Wenn schonmal geoeffnet, alten Controller behalten, um Daten zu behalten.
            loader.setController(mainMenuController);
        }

        String sceneName = "MainMenu.fxml";
        String sceneTitle = ("NAO dashboard");
    }

    private FXMLLoader setSceneContent(String sceneName, String sceneTitle) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        Parent root = loader.load();

        stage.setTitle(sceneTitle);
        stage.setScene(new Scene(root, 900, 700));
        return loader;
    }

    public void setSession(Session session) {
        if( mainMenuController != null ) {
            mainMenuController.setSession(session);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}