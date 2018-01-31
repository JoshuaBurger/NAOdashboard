package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;

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
    private Button buttonRGBpreview;
    @FXML
    private Label lblSpeechSpeedValue;
    @FXML
    private Label lblPitchValue;
    @FXML
    private Label lblVolumeValue;
    @FXML
    protected Label lblBattery1;
    @FXML
    protected Label lblBattery2;
    @FXML
    protected Label lblBattery3;
    @FXML
    protected Label lblBattery4;
    @FXML
    private Slider sliderVolume;
    @FXML
    private Slider sliderSpeechPitch;
    @FXML
    private Slider sliderSpeechSpeed;
    @FXML
    protected ImageView imgBattery1;
    @FXML
    protected ImageView imgBattery2;
    @FXML
    protected ImageView imgBattery3;
    @FXML
    protected ImageView imgBattery4;
    @FXML
    private Label labelAllowedValue;
    @FXML
    protected TextField txtConnectionIP;
    @FXML
    protected TextField txtConnectionPort;
    @FXML
    protected Label lblConnectionInfo;
    @FXML
    protected Label lblConnectionState;
    @FXML
    protected Button btnConnect;
    @FXML
    protected Button btnDisconnect;

    int pitchValue = 100;
    int speedValue = 100;
    private float walkSpeed;
    private String language = "English";

    private Session session;
    private ALMemory memory;

    private ConnectionModel connection;
    private MovementModel movement;
    private LedModel ledModel;
    private BatteryModel battery;
    private HeadSensorModel headSensors;

    public MainMenuController() {
        connection = new ConnectionModel(this);
        movement = new MovementModel();
        ledModel = new LedModel();
        battery = new BatteryModel(this);
        headSensors = new HeadSensorModel(this);
    }

    public void setSession(Session session) {
        this.session = session;
        movement.setSession(session);
        ledModel.setSession(session);
        battery.setSession(session);
        headSensors.setSession(session);

        // Methoden auf verschiedene Events registrieren
        registerEvents();
    }

    private void registerEvents() {
        try {
            // Allgemeinen Eventhandler auf die Session erstellen.
            memory = new ALMemory(session);

            // Batteriespezifische Events registrieren.
            battery.registerBatteryEvents(memory);
            // Kopfsensor (Sprache) Events registrieren.
            headSensors.registerTactilEvents(memory);
        } catch(Exception e) {
            System.out.println("Events not available");
            memory = null;
        }
    }

    public void connect() {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
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
            labelAllowedValue.setVisible(false);
            textfieldRed.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
            textfieldGreen.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
            textfieldBlue.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
            int valueRed = Integer.parseInt(textfieldRed.getText());
            int valueGreen = Integer.parseInt(textfieldGreen.getText());
            int valueBlue = Integer.parseInt(textfieldBlue.getText());

            if (setVisibleAllowedValueLabel(valueRed) == true) {
                valueRed = 200;
                textfieldRed.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
            }
            if (setVisibleAllowedValueLabel(valueGreen) == true){
                valueGreen = 200;
                textfieldGreen.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
            }
            if (setVisibleAllowedValueLabel(valueBlue) == true){
                valueBlue = 200;
                textfieldBlue.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
            }

            String hexRGBColor = String.format("#%02X%02X%02X", valueRed, valueGreen, valueBlue);
            buttonRGBpreview.setStyle("-fx-background-color: " + hexRGBColor + ";");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Connection lost.");
        }
    }
    public boolean setVisibleAllowedValueLabel(int value) {
        if (value > 200) {
            labelAllowedValue.setVisible(true);
            value = 200;
            return true;
        } else {
            return false;
        }
    }
    public void setledsRGBcolor() {
        Float red = (Float.parseFloat(textfieldRed.getText())/100);
        Float green = (Float.parseFloat(textfieldGreen.getText())/100);
        Float blue = (Float.parseFloat(textfieldBlue.getText())/100);
        ledModel.setledsRGBcolor(red, green, blue);
    }

    //Speak
    public void radioButtonEnglish() {
        language = "English";
    }
    public void radioButtonDeutsch() {
        language = "German";
    }

    public void sayText() {
        saySomething(sayText.getText());
    }

    public void saySomething(String text) {
        saySomething(text, speedValue, pitchValue, language);
    }

    public void saySomething(String text, int speed, int pitch, String lang) {
        if (text == null) {
            System.out.println("No text to say.");
        }
        else {
            try {
                ALTextToSpeech tts = new ALTextToSpeech(session);
                tts.say("\\vct=" + pitch+ "\\" + "\\rspd=" + speed+ "\\" + text, lang);
                System.out.println(lang);
            } catch(Exception e) {
                System.out.println("No connection.");
            }
        }
    }

    public void changeVolume() {
        try {
            ALTextToSpeech volume = new ALTextToSpeech(session);
            float v = ((float) sliderVolume.getValue() / 100);
            volume.setVolume(v);
            lblVolumeValue.setText((int)(v * 100) + "%");
            System.out.println(v * 100 + "%");
        } catch(Exception e) {
            System.out.println("No connection.");
        }
    }
    public void changePitch() {
        pitchValue = ((int)(sliderSpeechPitch.getValue()));
        lblPitchValue.setText(pitchValue +"%");
        System.out.println(pitchValue +"%");
    }


    public void changeTalkingSpeed() {
        speedValue = ((int)(sliderSpeechSpeed.getValue()));
        lblSpeechSpeedValue.setText(speedValue +"%");
        System.out.println(speedValue +"%");
    }

    public void setHeadFrontSensor() {
        headSensors.setHeadSensorSpeechTask("Front", sayText.getText(), (int)(sliderSpeechSpeed.getValue()), (int)sliderSpeechPitch.getValue(), language);
    }
    public void setHeadMiddleSensor() {
        headSensors.setHeadSensorSpeechTask("Middle", sayText.getText(), (int)(sliderSpeechSpeed.getValue()), (int)sliderSpeechPitch.getValue(), language);
    }
    public void setHeadRearSensor() {
        headSensors.setHeadSensorSpeechTask("Rear", sayText.getText(), (int)(sliderSpeechSpeed.getValue()), (int)sliderSpeechPitch.getValue(), language);
    }

    public void playAudio() throws Exception {
        ALAudioPlayer audioPlayer = new ALAudioPlayer(session);
        System.out.println(audioPlayer.getSoundSetFileNames("Aldebaran"));
    }


    //Postures
    public void wakeUp() throws Exception{
        try {
            ALMotion wakeUp = new ALMotion(session);
            wakeUp.wakeUp();
        }
        catch(Exception e) {
            System.out.println(e.getMessage() + "Connection lost.");
        }
    }
    public void rest()throws Exception {
        ALMotion rest = new ALMotion(session);
        rest.rest();
    }
    public void standUp() {
        movement.goToPosture("Stand");
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
    public void standZero() {
        movement.goToPosture("StandZero");
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
        movement.move(0.3F, 0F, 0F);
    }
    public void walkLeft() {
        movement.move(0F, 0.4F, 0F);
    }
    public void walkBackwards() {
        movement.move(-0.4F, 0F, 0F);
    }
    public void walkRight() {
        movement.move(0F, -0.4F, 0F);
    }
    public void stopWalking() {
        movement.stopWalking();
    }

    public void walkKey(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.W) {
            walkForwards();
        } else if (keyEvent.getCode() == KeyCode.A) {
            walkLeft();
        } else if (keyEvent.getCode() == KeyCode.S) {
            walkBackwards();
        } else if (keyEvent.getCode() == KeyCode.D) {
            walkRight();
        }
    }


    //TurnAround
    public void turnLeft() {
        movement.move(0F, 0F, 0.4F);
    }
    public void turnRight() {
        movement.move(0F, 0F, -0.4F);
    }
}