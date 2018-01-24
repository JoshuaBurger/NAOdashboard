package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainMenuController {

    @FXML
    private TextArea sayText;
    @FXML
    private TextField textfieldRed;
    @FXML
    private TextField textfieldGreen;
    @FXML
    private TextField textfieldBlue;
    @FXML
    private String talkingText;
    @FXML
    private Button RGBcolorPane;
    @FXML
    private Label VolumeLabel;
    @FXML
    private Label PitchLabel;
    @FXML
    private Label speedLabel;
    @FXML
    private Slider sliderVolume;
    @FXML
    private Slider sliderPitch;
    @FXML
    private Slider sliderSpeed;

    int pitchValue = 100;
    int speedValue = 100;
    private float walkSpeed;
    private String language = "English";

    private Main mainClass;
    private Session session;
    private MovementModel movement;
    private LedModel ledModel;

    public MainMenuController(Main main) {
        // Main-Klasse merken, um ueber diese andere Views zu oeffnen
        this.mainClass = main;
        movement = new MovementModel(main);
        ledModel = new LedModel(main);
    }

    public void setSession(Session session) {
        this.session = session;
        this.movement.setSession(session);
        this.ledModel.setSession(session);
    }


    //Postures
    public void standUp() {
        movement.goToPosture("StandUp");
    }
    public void sitRelax() {
        movement.goToPosture("SitRelax");
    }
    public void crouch() {
        movement.goToPosture("Crouch");
    }
    public void lyingBack() {
        movement.goToPosture("LyingBack");
    }
    public void lyingBelly() {
        movement.goToPosture("LyingBelly");
    }


    //Speak
    public void radioButtonEnglish() {
        language = "English";
    }
    public void radioButtonDeutsch() {
        language = "German";
    }

    public void sayText() {
        if (sayText.getText() == null) {
            System.out.println("No text to say.");
        } else {
            saySomething(sayText.getText());
        }
    }
    public void saySomething(String text) {
        try {
            ALTextToSpeech tts = new ALTextToSpeech(session);
            tts.say("\\vct=" + pitchValue+ "\\" + "\\rspd=" + speedValue+ "\\" + text, language);
            System.out.println(language);
        } catch(Exception e) {
            System.out.println("No connection.");
        }
    }

    public void changeVolume() {
        try {
        ALTextToSpeech volume = new ALTextToSpeech(session);
        float v = ((float) sliderVolume.getValue() / 100);
        volume.setVolume(v);
        VolumeLabel.setText((int)(v * 100) + "%");
        System.out.println(v * 100 + "%");
        } catch(Exception e) {
            System.out.println("No connection.");
        }
    }
    public void changePitch() {
        try {
        ALTextToSpeech pitch = new ALTextToSpeech(session);
        pitchValue = ((int)(sliderPitch.getValue() * 1.5f +50f));
        PitchLabel.setText(pitchValue +"%");
        System.out.println(pitchValue +"%");
        } catch(Exception e) {
            System.out.println("No connection.");
        }
    }
    public void changeTalkingSpeed() {
        try {
            ALTextToSpeech speed = new ALTextToSpeech(session);
            speedValue = ((int)(sliderSpeed.getValue() * 3.5f +50f));
            speedLabel.setText(speedValue +"%");
            System.out.println(speedValue +"%");
        } catch(Exception e) {
            System.out.println("No connection.");
        }
    }


    //LEDs
    public void ledsOff() {
        ledModel.ledsOff();
    }
    public void ledsOn() {
        ledModel.colorFaceLeds("FaceLeds", "white", 1F);
    }
    public void buttonFaceLedsRed() {
        ledModel.colorFaceLeds("FaceLeds", "red", 1F);
    }
    public void buttonFaceLedsBlue() {
        ledModel.colorFaceLeds("FaceLeds", "blue", 1F);
    }
    public void buttonFaceLedsGreen() {
        ledModel.colorFaceLeds("FaceLeds", "green", 1F);
    }
    public void buttonFaceLedsYellow() {
        ledModel.colorFaceLeds("FaceLeds", "yellow", 1F);
    }
    public void buttonFaceLedsCyan() {
        ledModel.colorFaceLeds("FaceLeds", "cyan", 1F);
    }
    public void buttonFaceLedsMagenta() {
        ledModel.colorFaceLeds("FaceLeds", "magenta", 1F);
    }
    public void buttonFaceLedsWhite() {
        ledModel.colorFaceLeds("FaceLeds", "white", 1F);
    }

    public void RGBcolorPreview() {
        try {
            int red = Integer.parseInt(textfieldRed.getText());
            int green = Integer.parseInt(textfieldGreen.getText());
            int blue = Integer.parseInt(textfieldBlue.getText());
            String hex = String.format("#%02X%02X%02X", red, green, blue);
            RGBcolorPane.setStyle("-fx-background-color: "+ hex + ";");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void setledsRGBcolor() {
        Float red = (Float.parseFloat(textfieldRed.getText())/100);
        Float green = (Float.parseFloat(textfieldGreen.getText())/100);
        Float blue = (Float.parseFloat(textfieldBlue.getText())/100);
        ledModel.setledsRGBcolor(red, green, blue);
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
    public void walkForwards() {
        movement.walkTowards("forwards");
    }
    public void walkLeft() {
        movement.walkTowards("left");
    }
    public void walkBackwards() {
        movement.walkTowards("backwards");
    }
    public void walkRight() {
        movement.walkTowards("right");
    }
    public void stopWalking() {
        movement.stopWalking();
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