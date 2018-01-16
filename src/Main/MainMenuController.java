package Main;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.aldebaran.qi.helper.proxies.*;

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

    private String talkingText;
    private String language = "english";
    private RadioMenuItem setLanguageToEnglish;
    private RadioMenuItem setLanguageToGerman;

    // Die Main-Klasse
    private Main mainClass;
    private Session session;

    public void setMainClass(Main main)
    {
        this.mainClass = main;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    //Head
    public void headUp() {
        moveHead("up");
    }
    public void headLeft() {
        moveHead("left");
    }
    public void headDown() {
        moveHead("down");
    }
    public void headRight() {
        moveHead( "right");
    }
    public void moveHead(String direction) {
        System.out.println("move head " + direction);
        // TODO: Verbindung zum NAO
    }

    //Walk
    public void walkForwards() {
        walkTowards("forwards");
    }
    public void walkLeft() {
        walkTowards("left");
    }
    public void walkBackwards() {
        walkTowards("backwards");
    }
    public void walkRight() {
        walkTowards("right");
    }
    public void walkTowards(String walkDirection) {
        System.out.println("walk " + walkDirection);
        // TODO: Verbindung zum NAO
    }

    //Speak
    public void saySomething() throws Exception {
        //language = menuLanguage.getText();
        talkingText = sayText.getText();
        if ((session == null)|| (session.isConnected() == false)){
            System.out.println("No connection");
            return;
        }
        ALTextToSpeech tts = new ALTextToSpeech(session);
        tts.say(talkingText);
        System.out.println(talkingText + " " + "in Sprache");
        // TODO: Verbindung zum NAO
    }

    public void walkSpeed() {
        System.out.println(walkSpeed.getValue());
    }
}
