package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MainMenuController {
    @FXML
    private TabPane tabPane;
    @FXML
    private Button buttonFaceLedsRed;
    @FXML
    private Button buttonFaceLedsYellow;
    @FXML
    private Button buttonFaceLedsBlue;
    @FXML
    private Button buttonFaceLedMagenta;
    @FXML
    private Button buttonFaceLedGreen;
    @FXML
    private Button buttonFaceLedCyan;
    @FXML
    private Button buttonFaceLedOrange;
    @FXML
    private Button buttonFaceLedWhite;
    @FXML
    private Button buttonRGBpreview;
    @FXML
    protected Button btnConnect;
    @FXML
    protected Button btnDisconnect;
    @FXML
    private TextArea sayText;
    @FXML
    private TextField textfieldRed;
    @FXML
    private TextField textfieldGreen;
    @FXML
    private TextField textfieldBlue;
    @FXML
    private Label lblSpeechSpeedValue;
    @FXML
    private Label lblPitchValue;
    @FXML
    private Label lblVolumeValue;
    @FXML
    private Label labelAllowedValue;
    @FXML
    protected Label lblHead1;
    @FXML
    protected Label lblHead2;
    @FXML
    protected Label lblHead3;
    @FXML
    protected Label lblHead4;
    @FXML
    protected Label lblLArm1;
    @FXML
    protected Label lblLArm2;
    @FXML
    protected Label lblLArm3;
    @FXML
    protected Label lblLArm4;
    @FXML
    protected Label lblRArm1;
    @FXML
    protected Label lblRArm2;
    @FXML
    protected Label lblRArm3;
    @FXML
    protected Label lblRArm4;
    @FXML
    protected Label lblLLeg1;
    @FXML
    protected Label lblLLeg2;
    @FXML
    protected Label lblLLeg3;
    @FXML
    protected Label lblLLeg4;
    @FXML
    protected Label lblRLeg1;
    @FXML
    protected Label lblRLeg2;
    @FXML
    protected Label lblRLeg3;
    @FXML
    protected Label lblRLeg4;
    @FXML
    protected Label lblBattery1;
    @FXML
    protected Label lblBattery2;
    @FXML
    protected Label lblBattery3;
    @FXML
    protected Label lblBattery4;
    @FXML
    protected Label lblConnectionInfo;
    @FXML
    protected Label lblConnectionState;
    @FXML
    private Slider sliderVolume;
    @FXML
    private Slider sliderSpeechPitch;
    @FXML
    private Slider sliderSpeechSpeed;
    @FXML
    private Slider walkSpeedSlider;
    @FXML
    protected ImageView imgBattery1;
    @FXML
    protected ImageView imgBattery2;
    @FXML
    protected ImageView imgBattery3;
    @FXML
    protected ImageView imgBattery4;
    @FXML
    protected TextField txtConnectionIP;
    @FXML
    protected TextField txtConnectionPort;

    private int pitchValue = 100;
    private int speedValue = 100;
    private int valueRGBred = 255;
    private int valueRGBgreen = 0;
    private int valueRGBblue = 0;
    private float walkSpeedValue = 0.5F;
    private String language = "English";
    private String selectedLedItem;
    private String hexRGBColor = "#FF0000";
    private Session session;
    private ALMemory memory;
    private ConnectionModel connection;
    private MovementModel movement;
    private LedModel ledModel;
    private BatteryModel battery;
    private HeadSensorModel headSensors;
    private TemperatureModel temperature;

    public MainMenuController() {
        connection = new ConnectionModel(this);
        movement = new MovementModel();
        ledModel = new LedModel();
        battery = new BatteryModel(this);
        headSensors = new HeadSensorModel(this);
        temperature = new TemperatureModel(this);
    }


    //change mousePointer
    public void changeMouseCursorHand(MouseEvent mouseEvent) {
        mouseEvent.getSource();
        tabPane.setStyle("-fx-cursor: hand;");
    }
    public void changeMouseCursorText(MouseEvent mouseEvent) {
        mouseEvent.getSource();
        tabPane.setStyle("-fx-cursor: text;");
    }
    public void setDefaultMouseCursor(MouseEvent mouseEvent) {
        mouseEvent.getSource();
        tabPane.setStyle("-fx-cursor: default;");
    }



    //LEDs
    public void allLEDsOff() {
        ledModel.turnledsOff("FaceLeds");
    }public void rightEyeLEDsOff() {
        ledModel.turnledsOff("LeftFaceLeds");
    }public void leftEyeLEDsOff() {
        ledModel.turnledsOff("RightFaceLeds");
    }
    public void unselectAllLEDitems() {
        buttonFaceLedsRed.setStyle("-fx-border-width: 0; -fx-background-color: red;");
        buttonFaceLedsBlue.setStyle("-fx-border-width: 0; -fx-background-color: blue;");
        buttonFaceLedGreen.setStyle("-fx-border-width: 0; -fx-background-color: green;");
        buttonFaceLedsYellow.setStyle("-fx-border-width: 0; -fx-background-color: yellow;");
        buttonFaceLedCyan.setStyle("-fx-border-width: 0; -fx-background-color: cyan;");
        buttonFaceLedMagenta.setStyle("-fx-border-width: 0; -fx-background-color: magenta;");
        buttonFaceLedWhite.setStyle("-fx-border-width: 0; -fx-background-color: #e4dcdc;");
        buttonFaceLedOrange.setStyle("-fx-border-width: 0; -fx-background-color: #fe7200;");
        textfieldRed.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
        textfieldGreen.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
        textfieldBlue.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
        buttonRGBpreview.setStyle("-fx-border-width: 0; -fx-background-color: " + hexRGBColor + ";");
    }

    public void changeFaceLEDs() {
        getLEDsColor("FaceLeds");
    }
    public void changeRightEyeLEDs() {
        getLEDsColor("LeftFaceLeds");
    }
    public void changeLeftEyeLEDs() {
        getLEDsColor("RightFaceLeds");
    }

    public void getLEDsColor(String ledsName) {
        if (selectedLedItem == "rgbColor") {
        Float red = (Float.parseFloat(textfieldRed.getText())/100);
        Float green = (Float.parseFloat(textfieldGreen.getText())/100);
        Float blue = (Float.parseFloat(textfieldBlue.getText())/100);
        ledModel.setledsRGBcolor(ledsName, red, green, blue);
        } else if (selectedLedItem == "orange") {
            System.out.println("detected: orange was clicked, ledModel wird aufgerufen");
            ledModel.setledsRGBcolor(ledsName, 2.55F, 0.4F, 0F);
        } else {
            ledModel.colorFaceLeds(ledsName, selectedLedItem);
        }
    }


    //change style of selected button
    public void buttonFaceLedsRed() {
        unselectAllLEDitems();
        buttonFaceLedsRed.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: red;");
        selectedLedItem = "red";
    }
    public void buttonFaceLedsBlue() {
        unselectAllLEDitems();
        buttonFaceLedsBlue.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: blue;");
        selectedLedItem = "blue";
    }
    public void buttonFaceLedsGreen() {
        unselectAllLEDitems();
        buttonFaceLedGreen.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: green;");
        selectedLedItem = "green";
    }
    public void buttonFaceLedsYellow() {
        unselectAllLEDitems();
        buttonFaceLedsYellow.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: yellow;");
        selectedLedItem = "yellow";
    }
    public void buttonFaceLedsCyan() {
        unselectAllLEDitems();
        buttonFaceLedCyan.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: cyan;");
        selectedLedItem = "cyan";
    }
    public void buttonFaceLedsMagenta() {
        unselectAllLEDitems();
        buttonFaceLedMagenta.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: magenta;");
        selectedLedItem = "magenta";
    }
    public void buttonFaceLedsWhite() {
        unselectAllLEDitems();
        buttonFaceLedWhite.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: #e4dcdc;");
        selectedLedItem = "white";
    }
    public void buttonFaceLedsOrange() {
        unselectAllLEDitems();
        buttonFaceLedOrange.setStyle("-fx-border-color: darkgrey; -fx-border-width: 3; -fx-background-color: #fe7200;");
        selectedLedItem = "orange";
    }
    public void rgbColorPreviewSelected() {
        unselectAllLEDitems();
        buttonRGBpreview.setStyle("-fx-border-color: darkgrey; -fx-border-width: 5; -fx-background-color: " + hexRGBColor + ";");
        selectedLedItem = "rgbColor";
    }

    //show preview of RGB color
    public void RGBcolorPreview(KeyEvent keyEvent) {
        try {
            labelAllowedValue.setVisible(false);
            unselectAllLEDitems();
            selectedLedItem = "rgbColor";

            //check if textfield is null or value is higher than 255
            if (textfieldRed.getText() == "") {
                textfieldRed.setText("0");
                valueRGBred = 0;
            } else if (setVisibleAllowedValueLabel(valueRGBred) == true) {
                valueRGBred = 255;
                textfieldRed.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
            }
            else {
                valueRGBred = Integer.parseInt(textfieldRed.getText());
            }

            if (textfieldGreen.getText() == null) {
                textfieldGreen.setText("0");
                valueRGBgreen = 0;
            } else if (setVisibleAllowedValueLabel(valueRGBgreen) == true){
                valueRGBgreen = 255;
                textfieldGreen.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
            }
            else {
                valueRGBgreen = Integer.parseInt(textfieldGreen.getText());
            }

            if (textfieldBlue.getText() == "") {
                textfieldBlue.setText("0");
                valueRGBblue = 0;
            } else if (setVisibleAllowedValueLabel(valueRGBblue) == true){
                valueRGBblue = 255;
                textfieldBlue.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
            }
            else {
                valueRGBblue = Integer.parseInt(textfieldBlue.getText());
            }

            //calculate rgb color code
            hexRGBColor = String.format("#%02X%02X%02X", valueRGBred, valueRGBgreen, valueRGBblue);
            buttonRGBpreview.setStyle("-fx-border-color: darkgrey; -fx-border-width: 5; -fx-background-color: " + hexRGBColor + ";");
        } catch (Exception e) {
            System.out.println(e.getMessage() + " // An Error occurred while converting RGB colors. Maybe the value of a textfield is null or the input contains illegal Arguments.");
        }
    }

    //check if a textfields value is higher than 255
    public boolean setVisibleAllowedValueLabel(int value) {
        if (value > 255) {
            labelAllowedValue.setVisible(true);
            return true;
        } else {
            return false;
        }
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
        movement.move(walkSpeedValue, 0F, 0F);
    }
    public void walkLeft() {
        movement.move(0F, walkSpeedValue, 0F);
    }
    public void walkBackwards() {
        movement.move(-walkSpeedValue, 0F, 0F);
    }
    public void walkRight() {
        movement.move(0F, -walkSpeedValue, 0F);
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

    public void getWalkSpeedSliderValue() {
        double currentValue = walkSpeedSlider.getValue();
        walkSpeedValue = (float)(currentValue/100);         //set walking speed
        System.out.println(walkSpeedValue);
    }

    //TurnAround
    public void turnLeft() {
        movement.move(0F, 0F, 0.4F);
    }
    public void turnRight() {
        movement.move(0F, 0F, -0.4F);
    }

    public void setSession(Session session) {
        this.session = session;
        movement.setSession(session);
        ledModel.setSession(session);
        battery.setSession(session);
        headSensors.setSession(session);
        temperature.setSession(session);

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
            // Temperatur Events registrieren
            temperature.registerTemperatureEvents(memory);
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
}