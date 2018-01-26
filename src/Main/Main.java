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
    private ConnectionMenuController connController;


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        // Controller programmatisch erstellen, um Daten zu behalten bei View-wechsel.
        mainMenuController = new MainMenuController(this);
        connController = new ConnectionMenuController(this, mainMenuController);
        startConnectMenu();
        stage.show();
    }

    public void startConnectMenu() throws Exception {
        // ConnectMenu View oeffnen
        setSceneContent("ConnectionMenu.fxml", "NAO Dashboard - Connection menu", connController);
        // Daten aus Controller in die View setzen.
        connController.setDataToView();
    }

    public void startMainMenu() throws Exception {
        // MaiMenu View oeffnen
        setSceneContent("MainMenuNEU.fxml", "NAO Dashboard", mainMenuController);
        // Daten aus Controller in die View setzen
        mainMenuController.setDataToView();
    }

    private void setSceneContent(String sceneName, String sceneTitle, Object controller) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        // Gegebenen Controller fuer die View verwenden.
        loader.setController(controller);
        Parent root = loader.load();
        // Aktive View austauschen.
        stage.setTitle(sceneTitle);
        stage.setScene(new Scene(root));
    }

    public void setSession(Session session) {
        if( mainMenuController != null ) {
            // NAO session an MainMenuController weiterreichen
            mainMenuController.setSession(session);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}