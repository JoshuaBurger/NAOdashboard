package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import com.aldebaran.qi.helper.proxies.ALSpeechRecognition;

public class MainMenuController {

    // Die nachfolgenden FXML Komponenten sind teilweise nur protected,
    // damit aus den verschiedenen Models darauf zugegriffen werden kann.

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabConnection;
    @FXML
    private Tab tabLeds;
    @FXML
    private Tab tabSpeech;
    @FXML
    private Tab tabPosturesTracking;
    @FXML
    private Tab tabMoves;
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
    private Button btnRGBleftEye;
    @FXML
    private Button btnRGBrightEye;
    @FXML
    private Button btnRGBbothEyes;
    @FXML
    private Button btnPlayAudio;
    @FXML
    protected Button btnConnect;
    @FXML
    protected Button btnDisconnect;
    @FXML
    protected Button btnCameraOn;
    @FXML
    protected Button btnCameraOff;
    @FXML
    protected Button btnPhoto;
    @FXML
    protected Button btnStartTracking;
    @FXML
    protected Button btnStopTracking;
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
    protected Label lblCameraLoading;
    @FXML
    protected Label lblTrackingInfo;
    @FXML
    protected Label lblHeadSensorInfo;
    @FXML
    protected Label lblCameraInfo;
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
    protected ImageView imgCamera;
    @FXML
    protected TextField txtConnectionIP;
    @FXML
    protected TextField txtConnectionPort;
    @FXML
    private ComboBox cbxAudio;
    @FXML
    protected ComboBox cbxConnectionHistory;
    @FXML
    protected GridPane gridPaneColorButtons;
    @FXML
    protected GridPane gridPaneLEDsOff;
    @FXML
    protected Pane paneTrackingConfig;
    @FXML
    protected ToggleGroup toggleGroupTrackTarget;
    @FXML
    protected ToggleGroup toggleGroupTrackMode;
    @FXML
    protected ToggleGroup toggleGroupTrackEffector;
    @FXML
    protected Spinner spinTrackTargetSize;
    @FXML
    protected Button btnSpeechRecognition;


    private int pitchValue = 100;
    private int speedValue = 100;
    private int valueRedInteger = 255;
    private int valueGreenInteger = 0;
    private int valueBlueInteger = 0;
    private float walkSpeedValue = 0.5F;
    private long infoTimerId = 0;
    private Label lblinfoTimerCurrent;
    protected String language = "English";
    private String selectedLedItem;
    private String hexRGBColor = "#FF0000";
    private String tfRedString = "255";
    private String tfGreenString = "0";
    private String tfBlueString = "0";
    private ObservableList sounds;

    private Session session;
    protected ALMemory memory;
    private ConnectionModel connection;
    private MovementModel movement;
    private LedModel ledModel;
    private BatteryModel battery;
    private HeadSensorModel headSensors;
    private TemperatureModel temperature;
    private CameraModel camera;
    private TrackerModel tracker;
    private SpeechModel speech;

    @FXML
    public void initialize() {
        // Methode wird wie Konstruktor aufgerufen (allerdings sind hier die FXML-Komponenten bereits bekannt)
        // Verschiedene Models initializieren
        connection = new ConnectionModel(this);
        movement = new MovementModel(this);
        ledModel = new LedModel(this);
        battery = new BatteryModel(this);
        headSensors = new HeadSensorModel(this);
        temperature = new TemperatureModel(this);
        camera = new CameraModel(this);
        speech = new SpeechModel(this);
        tracker = new TrackerModel(this);
    }

    public void startTracking() {
        tracker.startTracking();
    }

    public void stopTracking() {
        tracker.stopTracking();
    }

    public void enableCamera() {
        camera.enableCamera();
    }

    public void disableCamera() {
        camera.disableCamera();
    }

    public void takePhoto() {
        camera.takePhoto();
    }

    //LEDs
    public void allLEDsOff() {
        ledModel.turnledsOff("FaceLeds");
    }

    public void rightEyeLEDsOff() {
        ledModel.turnledsOff("LeftFaceLeds");
    }

    public void leftEyeLEDsOff() {
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
        setTextfieldText();
        identifySelectedColor("FaceLeds");
    }
    public void changeRightEyeLEDs() {
        setTextfieldText();
        identifySelectedColor("LeftFaceLeds");
    }
    public void changeLeftEyeLEDs() {
        setTextfieldText();
        identifySelectedColor("RightFaceLeds");
    }
    public void setTextfieldText() {
        textfieldRed.setText(tfRedString);
        textfieldGreen.setText(tfGreenString);
        textfieldBlue.setText(tfBlueString);
    }
    public void identifySelectedColor(String ledsName) {             //identify which color is selected
        if (selectedLedItem == "rgbColor") {
        Float red = (Float.parseFloat(tfRedString)/100);
        Float green = (Float.parseFloat(tfGreenString)/100);
        Float blue = (Float.parseFloat(tfBlueString)/100);
        ledModel.setledsRGBcolor(ledsName, red, green, blue);
        } else if (selectedLedItem == "orange") {
            ledModel.setledsRGBcolor(ledsName, 2.55F, 0.4F, 0F);
        } else {
            ledModel.colorFaceLeds(ledsName, selectedLedItem);
        }
    }


    //change style of selected button
    public void buttonFaceLedsRed() {
        unselectAllLEDitems();
        buttonFaceLedsRed.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: red;");
        selectedLedItem = "red";
    }
    public void buttonFaceLedsBlue() {
        unselectAllLEDitems();
        buttonFaceLedsBlue.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: blue;");
        selectedLedItem = "blue";
    }
    public void buttonFaceLedsGreen() {
        unselectAllLEDitems();
        buttonFaceLedGreen.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: green;");
        selectedLedItem = "green";
    }
    public void buttonFaceLedsYellow() {
        unselectAllLEDitems();
        buttonFaceLedsYellow.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: yellow;");
        selectedLedItem = "yellow";
    }
    public void buttonFaceLedsCyan() {
        unselectAllLEDitems();
        buttonFaceLedCyan.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: cyan;");
        selectedLedItem = "cyan";
    }
    public void buttonFaceLedsMagenta() {
        unselectAllLEDitems();
        buttonFaceLedMagenta.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: magenta;");
        selectedLedItem = "magenta";
    }
    public void buttonFaceLedsWhite() {
        unselectAllLEDitems();
        buttonFaceLedWhite.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: #e4dcdc;");
        selectedLedItem = "white";
    }
    public void buttonFaceLedsOrange() {
        unselectAllLEDitems();
        buttonFaceLedOrange.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: #fe7200;");
        selectedLedItem = "orange";
    }

    //disables apply buttons
    public void disableOtherItems(boolean value) {
        textfieldRed.setDisable(value);
        textfieldGreen.setDisable(value);
        textfieldBlue.setDisable(value);
        buttonRGBpreview.setDisable(value);
        btnRGBleftEye.setDisable(value);
        btnRGBrightEye.setDisable(value);
        btnRGBbothEyes.setDisable(value);
        gridPaneColorButtons.setDisable(value);
        gridPaneLEDsOff.setDisable(value);
    }

    public void textfieldKeyReleased(KeyEvent keyEvent) {
        tfRedString = textfieldRed.getText();
        tfGreenString = textfieldGreen.getText();
        tfBlueString = textfieldBlue.getText();

        //remove whitespaces
        if (keyEvent.getCode() == KeyCode.SPACE) {
            removeWhitespaces();
        }

        if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
            //prevent that textfield could be empty
            preventEmptyTextfield();

            //check if textfield values are >255
            if (compareValueTo255(tfRedString) && compareValueTo255(tfGreenString) && compareValueTo255(tfBlueString)) {
                RGBcolorPreview();                  //show preview of rgb color
            } else {
                labelAllowedValue.setVisible(true);
                disableOtherItems(true);         //disable everything else

                if (compareValueTo255(tfRedString) == false) {
                    textfieldRed.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
                    textfieldRed.setDisable(false);
                }
                if (compareValueTo255(tfGreenString) == false) {
                    textfieldGreen.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
                    textfieldGreen.setDisable(false);
                }
                if (compareValueTo255(tfBlueString) == false) {
                    textfieldBlue.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
                    textfieldBlue.setDisable(false);
                }
            }
        }
    }
    public void textfieldKeyTyped() {
        tfRedString = textfieldRed.getText();
        tfGreenString = textfieldGreen.getText();
        tfBlueString = textfieldBlue.getText();

        //remove letters and punctuation
        removeforbiddenCharacters();

        //remove 0 to prevent errors while converting
        removeNull();

        //prevent that textfield could be empty
        preventEmptyTextfield();

        //check if textfield values are >255
        if (compareValueTo255(tfRedString) && compareValueTo255(tfGreenString) && compareValueTo255(tfBlueString)) {
            RGBcolorPreview();                  //show preview of rgb color
        } else {
            labelAllowedValue.setVisible(true);
            disableOtherItems(true);         //disable all other items

            if (compareValueTo255(tfRedString) == false) {
                textfieldRed.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
                textfieldRed.setDisable(false);
            }
            if (compareValueTo255(tfGreenString) == false) {
                textfieldGreen.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
                textfieldGreen.setDisable(false);
            }
            if (compareValueTo255(tfBlueString) == false) {
                textfieldBlue.setStyle("-fx-border-color: red; -fx-border-width: 5; -fx-border-radius: 5;");
                textfieldBlue.setDisable(false);
            }
        }
    }
    public boolean compareValueTo255(String tfValue) {
        int tfValueInt;
        try {
            tfValueInt = Integer.parseInt(tfValue);
            if (tfValueInt > 255) {
                return false;
            } else {
                return true;
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " // something went wrong while converting values of textfields.");
        }
        return true;
    }
    public void removeWhitespaces() {
        tfRedString = tfRedString.replaceAll("\\s", "");
        textfieldRed.setText(tfRedString);
        textfieldRed.positionCaret(tfRedString.length());

        tfGreenString = tfGreenString.replaceAll("\\s", "");
        textfieldGreen.setText(tfGreenString);
        textfieldGreen.positionCaret(tfGreenString.length());

        tfBlueString = tfBlueString.replaceAll("\\s", "");
        textfieldBlue.setText(tfBlueString);
        textfieldBlue.positionCaret(tfBlueString.length());
    }
    public void preventEmptyTextfield() {
        if (tfRedString.isEmpty()) { tfRedString = "0"; }
        if (tfGreenString.isEmpty()) { tfGreenString = "0"; }
        if (tfBlueString.isEmpty()) { tfBlueString = "0"; }
    }
    public void removeforbiddenCharacters() {
        tfRedString = tfRedString.replaceAll("\\W","");
        tfRedString = tfRedString.replaceAll("[a-zA-Z]", "");
        textfieldRed.setText(tfRedString);
        textfieldRed.positionCaret(tfRedString.length());

        tfGreenString = tfGreenString.replaceAll("\\W","");
        tfGreenString = tfGreenString.replaceAll("[a-zA-Z]", "");
        textfieldGreen.setText(tfGreenString);
        textfieldGreen.positionCaret(tfGreenString.length());

        tfBlueString = tfBlueString.replaceAll("\\W","");
        tfBlueString = tfBlueString.replaceAll("[a-zA-Z]", "");
        textfieldBlue.setText(tfBlueString);
        textfieldBlue.positionCaret(tfBlueString.length());
    }
    public void removeNull() {
        if (tfRedString.startsWith("0") && !tfRedString.equals("0")) {
            tfRedString = tfRedString.substring(1);
            textfieldRed.setText(tfRedString);
            textfieldRed.positionCaret(tfRedString.length());
        }
        if (tfGreenString.startsWith("0") && !tfGreenString.equals("0")) {
            tfGreenString = tfGreenString.substring(1);
            textfieldGreen.setText(tfGreenString);
            textfieldGreen.positionCaret(tfGreenString.length());
        }
        if (tfBlueString.startsWith("0") && !tfBlueString.equals("0")) {
            tfBlueString = tfBlueString.substring(1);
            textfieldBlue.setText(tfBlueString);
            textfieldBlue.positionCaret(tfBlueString.length());
        }
    }

    //show preview of RGB color
    public void RGBcolorPreview() {
        selectedLedItem = "rgbColor";
        unselectAllLEDitems();
        labelAllowedValue.setVisible(false);
        disableOtherItems(false);         //enable all other items
        textfieldRed.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5;");
        textfieldGreen.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5;");
        textfieldBlue.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5;");

        try {
            valueRedInteger = Integer.parseInt(tfRedString);
            valueGreenInteger = Integer.parseInt(tfGreenString);
            valueBlueInteger = Integer.parseInt(tfBlueString);
        } catch(Exception e) {
            System.out.println(e.getMessage() + " // something went wrong while converting values of textfields.");
        }

        hexRGBColor = String.format("#%02X%02X%02X", valueRedInteger, valueGreenInteger, valueBlueInteger);
        buttonRGBpreview.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: " + hexRGBColor + ";");
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
                if ( (session == null) || (session.isConnected() == false) ) {
                    handleConnectionClosed(true);
                }
                else {
                    System.out.println(e.getMessage());
                }
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

    public void setUpAudio()  {
        try {
            cbxAudio.setDisable(false);
            btnPlayAudio.setDisable(false);
            ALAudioPlayer audioPlayer = new ALAudioPlayer(session);
            List soundsList = audioPlayer.getSoundSetFileNames("Aldebaran");
            sounds = FXCollections.observableArrayList();
            sounds.setAll(soundsList);
            cbxAudio.setItems(sounds);
            System.out.println(sounds);
        }
        catch (Exception e){
            // Moeglicherweise hat NAO z.b. keine Audio-Dateien
            // Verbindung muss hier nicht geprueft werden, da die Methode
            // direkt beim Verbindung setzen ausgefuehrt wird
            System.out.println(e.toString());
            cbxAudio.setDisable(true);
            btnPlayAudio.setDisable(true);
        }
    }

    public void playAudio() throws Exception {
        try {
            ALAudioPlayer audioPlayer = new ALAudioPlayer(session);
            audioPlayer.playSoundSetFile((cbxAudio.getValue()).toString());
            System.out.println(cbxAudio.getValue());
        } catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                handleConnectionClosed(true);
            }
            else  {
                System.out.println(e.getMessage());
            }
        }
    }

    public void listenToSpeech(){
        if(btnSpeechRecognition.getText().equals("Listen")){
            btnSpeechRecognition.setText("Stop listening");
            speech.registerSpeechEvents(memory);
        }
        else{


            speech.unregisterSpeechEvents(memory);
            btnSpeechRecognition.setText("Listen");

            }
        }

    //Postures
    public void wakeUp() throws Exception{
        movement.goToRest(false);
    }
    public void rest()throws Exception {
        movement.goToRest(true);
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
    public void standInit() {
        movement.goToPosture("StandInit");
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
        //set walking speed
        walkSpeedValue = (float)(currentValue/100);
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
        camera.setSession(session);
        speech.setSession(session);
        tracker.setSession(session);

        // AudioFiles vom NAO holen
        setUpAudio();
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

    public void unregisterEvents() {
        try {
            memory.unsubscribeAllEvents();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleConnectionClosed(boolean lost) {
        // Verbindung geschlossen
        // Zuerst pruefen, ob Verbindungsverlust nicht bereits gehandelt wurde
        if ( tabLeds.isDisabled() == false ) {
            // Tabs deaktivieren und zu Connection Tab springen
            tabLeds.setDisable(true);
            tabMoves.setDisable(true);
            tabSpeech.setDisable(true);
            tabPosturesTracking.setDisable(true);
            tabPane.getSelectionModel().select(tabConnection);
            connection.setDisconnected(lost);
        }
    }

    public void enableTabs() {
        tabLeds.setDisable(false);
        tabMoves.setDisable(false);
        tabSpeech.setDisable(false);
        tabPosturesTracking.setDisable(false);
    }

    public void connect() {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void applyConnectionHistory() {
        connection.applySelectedHistoryConn();
    }


    public void displayTextTemporarily(Label label, String text, long durationMs) {
        // Mit dieser Methode wird ein Text in ein Label gesetzt und nach
        // mitgegebener Zeit wieder geloescht
        Timer t = new Timer();
        label.setText(text);
        // Wir merken uns das Label und eine Id, sodass der Timer bei Aktivierung
        // pruefen kann, ob er der aktuelle Timer ist, um nicht einen neueren Text zu loeschen.
        // Ausser es ist auch nicht das aktuelle Label, dann kann das alte vmtl. doch geloescht werden.
        infoTimerId += 1;
        lblinfoTimerCurrent = label;
        t.schedule(new DeleteTextTask(label, infoTimerId), durationMs);
    }

    class DeleteTextTask extends TimerTask {
        private Label label;
        private long id;

        public DeleteTextTask(Label label, long id) {
            this.label = label;
            this.id    = id;
        }

        @Override
        public void run() {
            Platform.runLater(new Runnable(){
                public void run(){
                    // Label leer setzen, nur wenn es sich um den aktuellsten Timer handelt.
                    // Ansonsten wuerde evtl. ein Text direkt nach Anzeige geloescht.
                    // Ausser auch nicht aktuellstes Label, dann doch loeschen
                    if ( (id == infoTimerId) || (label != lblinfoTimerCurrent) ) {
                        label.setText("");
                    }
                }
            });
        }
    }
}
