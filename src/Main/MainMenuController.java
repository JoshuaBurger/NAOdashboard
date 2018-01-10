package Main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainMenuController {

    @FXML
    private Button headUp;
    @FXML
    private Button headLeft;
    @FXML
    private Button headDown;
    @FXML
    private Button headRight;

    @FXML
    private Button walkForwards;
    @FXML
    private Button walkLeft;
    @FXML
    private Button walkBackwards;
    @FXML
    private Button walkRight;

    @FXML
    private TextArea sayText;
    @FXML
    private MenuButton menuLanguage;
    @FXML
    private Button buttonSay;

    @FXML
    private Slider walkSpeed;


    private String howToMoveHead;
    private String walkDirection;
    private String talkingText;
    private String language = "english";
    private RadioMenuItem setLanguageToEnglish;
    private RadioMenuItem setLanguageToGerman;

    // Das Applikations-Objekt
    private Main app;

    public void setApplication(Main app)
    {
        this.app = app;
    }

    //Head
    public void headUp() {
        moveHead(howToMoveHead = "up");
    }
    public void headLeft() {
        moveHead(howToMoveHead = "left");
    }
    public void headDown() {
        moveHead(howToMoveHead = "down");
    }
    public void headRight() {
        moveHead(howToMoveHead = "right");
    }
    public void moveHead(String howToMoveHead) {
        System.out.println("move head " + howToMoveHead);
        // TODO: Verbindung zum NAO
    }

    //Walk
    public void walkForwards() {
        walkTowards(walkDirection = "forwards");
    }
    public void walkLeft() {
        walkTowards(walkDirection = "left");
    }
    public void walkBackwards() {
        walkTowards(walkDirection = "backwards");
    }
    public void walkRight() {
        walkTowards(walkDirection = "right");
    }
    public void walkTowards(String walkDirection) {
        System.out.println("walk " + walkDirection);
        // TODO: Verbindung zum NAO
    }

    //Speak
    public void setLanguageToEnglish() {
        language = "english";
    }
    public void setLanguageToGerman() {
        language = "german";
    }
    public void saySomething() {
        talkingText = sayText.getText();
        System.out.println(talkingText + " " + "in "+language);
        // TODO: Verbindung zum NAO
    }

    public void walkSpeed() {
        System.out.println(walkSpeed.getValue());
    }
}
