package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALLeds;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainMenuController {

    @FXML
    private TextArea sayText;

    @FXML
    private String talkingText;

    private Main mainClass;
    private Session session;

    private MovementModel movement;
    private float walkSpeed;
    private String language = "English";

    public MainMenuController(Main main) {
        // Main-Klasse merken, um ueber diese andere Views zu oeffnen
        this.mainClass = main;
        movement = new MovementModel(main);
    }

    public void setSession(Session session) {
        this.session = session;
        this.movement.setSession(session);
    }


    //Speak
    public void radioButtonEnglish() {
        language = "English";
    }
    public void radioButtonDeutsch() {
        language = "German";
    }
    public void saySomething() throws Exception {
        talkingText = sayText.getText();
        if ((session == null) || (session.isConnected() == false)) {
            System.out.println("No connection");
            return;
        }
        ALTextToSpeech tts = new ALTextToSpeech(session);
        tts.say(talkingText, language);
        System.out.println("Nao says: " + "<" + talkingText + "> in " + language);
    }

    public void changeVoice(){

    }


    //LEDs
    public void ledsOff() throws Exception {
        if (session == null || session.isConnected() == false) {
            System.out.println("No connection.");
            return;
        }
        ALLeds leds = new ALLeds(session);
        String name = "FaceLeds";
        leds.off(name);
    }
    public void ledsOn() throws Exception {
        buttonFaceLedsWhite();
    }

    public void buttonFaceLedsRed() throws Exception {
        colorFaceLeds("FaceLeds", "red", 1F);
    }
    public void buttonFaceLedsBlue() throws Exception {
        colorFaceLeds("FaceLeds", "blue", 1F);
    }
    public void buttonFaceLedsGreen() throws Exception {
        colorFaceLeds("FaceLeds", "green", 1F);
    }
    public void buttonFaceLedsYellow() throws Exception {
        colorFaceLeds("FaceLeds", "yellow", 1F);
    }
    public void buttonFaceLedsCyan() throws Exception {
        colorFaceLeds("FaceLeds", "cyan", 1F);
    }
    public void buttonFaceLedsMagenta() throws Exception {
        colorFaceLeds("FaceLeds", "magenta", 1F);
    }
    public void buttonFaceLedsWhite() throws Exception {
        colorFaceLeds("FaceLeds", "white", 1F);
    }
    public void colorFaceLeds(String ledsName, String color, float duration) throws Exception {
        if (session == null || session.isConnected() == false) {
            System.out.println("keine Verbindung mehr.");
            return;
        }
        ALLeds leds = new ALLeds(session);
        leds.fadeRGB(ledsName, color, duration);
    }



    //Head
    public void headUp() {
        movement.moveHead("up");
    }

    public void headLeft() {
        movement.moveHead("left");
    }

    public void headDown() {
        movement.moveHead("down");
    }

    public void headRight() {
        movement.moveHead("right");
    }

    //Walk
    public void walkForwards() throws Exception {
        walkTowards("forwards");
    }

    public void walkLeft() throws Exception {
        walkTowards("left");
    }

    public void walkBackwards() throws Exception {
        walkTowards("backwards");
    }

    public void walkRight() throws Exception {
        walkTowards("right");
    }

    public void walkTowards(String walkDirection) throws Exception {
        ALMotion walk = new ALMotion(session);
        walk.moveTo(0F, 1F);
        // TODO: Verbindung zum NAO prüfen
    }
    public void stopWalking() {
        // TODO: Verbindung zum NAO
    }

    public void startConnectionView() {
        try {
            mainClass.startConnectMenu();
        }
        catch(Exception e) {
            System.out.println("Could not open ConnectView.");
        }
    }
}