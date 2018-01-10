package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private Stage stage;
    private String sceneName;
    private String sceneTitle;


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        startLogin();
        stage.show();
    }

    public void startLogin() throws Exception {
        sceneName = "ConnectView.fxml";
        sceneTitle = ("NAO login");

        FXMLLoader loader = setSceneContent(sceneName);
        ConnectController connController = (ConnectController)loader.getController();
        connController.setMainClass(this);
    }

    public void startMainMenu() throws Exception {
        sceneName = "MainMenu.fxml";
        sceneTitle = ("NAO dashboard");

        FXMLLoader loader = setSceneContent(sceneName);
        MainMenuController mainMenuController = loader.getController();
        mainMenuController.setApplication(this);
    }

    private FXMLLoader setSceneContent(String sceneName) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        Parent root = loader.load();
        stage.setTitle(sceneTitle);
        stage.setScene(new Scene(root, 900, 700));
        return loader;
    }

    public static void main(String[] args) {
        launch(args);
    }
}