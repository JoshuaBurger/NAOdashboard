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
        startLogin();
        stage.show();
    }

    public void startLogin() throws Exception {
        String sceneName = "ConnectView.fxml";
        String sceneTitle = ("NAO login");

        FXMLLoader loader = setSceneContent(sceneName, sceneTitle);
        connController = (ConnectController)loader.getController();
        connController.setMainClass(this);
    }

    public void startMainMenu() throws Exception {
        String sceneName = "MainMenu.fxml";
        String sceneTitle = ("NAO dashboard");

        FXMLLoader loader = setSceneContent(sceneName, sceneTitle);
        mainMenuController = loader.getController();
        mainMenuController.setMainClass(this);
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