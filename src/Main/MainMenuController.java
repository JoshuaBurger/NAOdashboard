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

import javafx.scene.layout.AnchorPane;
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
    protected Button btnRed;
    @FXML
    protected Button btnGreen;
    @FXML
    protected Button btnBlue;
    @FXML
    protected Button btnMagenta;
    @FXML
    protected Button btnYellow;
    @FXML
    protected Button btnCyan;
    @FXML
    protected Button btnOrange;
    @FXML
    protected Button btnWhite;
    @FXML
    protected Button buttonRGBpreview;
    @FXML
    protected Button btnLeftEye;
    @FXML
    protected Button btnRightEye;
    @FXML
    protected Button btnBothEyes;
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
    protected TextField textfieldRed;
    @FXML
    protected TextField textfieldGreen;
    @FXML
    protected TextField textfieldBlue;
    @FXML
    private Label lblSpeechSpeedValue;
    @FXML
    private Label lblPitchValue;
    @FXML
    private Label lblVolumeValue;
    @FXML
    protected Label labelAllowedValue;
    @FXML
    protected Label lblHeatHead1;
    @FXML
    protected Label lblHeatHead2;
    @FXML
    protected Label lblHeatHead3;
    @FXML
    protected Label lblHeatHead4;
    @FXML
    protected Label lblHeatLArm1;
    @FXML
    protected Label lblHeatLArm2;
    @FXML
    protected Label lblHeatLArm3;
    @FXML
    protected Label lblHeatLArm4;
    @FXML
    protected Label lblHeatRArm1;
    @FXML
    protected Label lblHeatRArm2;
    @FXML
    protected Label lblHeatRArm3;
    @FXML
    protected Label lblHeatRArm4;
    @FXML
    protected Label lblHeatLLeg1;
    @FXML
    protected Label lblHeatLLeg2;
    @FXML
    protected Label lblHeatLLeg3;
    @FXML
    protected Label lblHeatLLeg4;
    @FXML
    protected Label lblHeatRLeg1;
    @FXML
    protected Label lblHeatRLeg2;
    @FXML
    protected Label lblHeatRLeg3;
    @FXML
    protected Label lblHeatRLeg4;
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
    protected GridPane gridPaneLedsOff;
    @FXML
    protected GridPane gridPaneLedsOn;
    @FXML
    protected GridPane gridPaneApplyColor;
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
    private float walkSpeedValue = 0.5F;
    private float stepHeight = 0.020F;
    private long infoTimerId = 0;
    private Label lblinfoTimerCurrent;
    protected String language = "English";
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

    //LEDs on oder off
    public void brainLEDsOn() { ledModel.turnledsOn("BrainLeds"); }
    public void brainLEDsOff() { ledModel.turnledsOff("BrainLeds"); }
    public void eyesLEDon() { ledModel.turnledsOn("FaceLeds"); }
    public void eyesLEDsOff() { ledModel.turnledsOff("FaceLeds"); }
    public void rightEyeLEDsOff() { ledModel.turnledsOff("RightFaceLeds"); }
    public void leftEyeLEDsOff() { ledModel.turnledsOff("LeftFaceLeds"); }
    public void earLEDsOn() { ledModel.turnledsOn("EarLeds"); }
    public void earLEDsOff() { ledModel.turnledsOff("EarLeds"); }
    public void leftEarLEDsOff() { ledModel.turnledsOff("LeftEarLeds"); }
    public void rightEarLEDsOff() { ledModel.turnledsOff("RightEarLeds"); }
    public void feetLEDsOn() { ledModel.turnledsOn("FeetLeds"); }
    public void feetLEDsOff() { ledModel.turnledsOff("FeetLeds"); }
    public void leftFootLEDsOff() { ledModel.turnledsOff("LeftFootLeds"); }
    public void rightFootLEDsOff() { ledModel.turnledsOff("RightFootLeds"); }

    //ändere Farbe
    public void changeFaceLEDs() {
        ledModel.setTextfieldText();
        ledModel.fadeLEDs("FaceLeds");
    }
    public void changeRightEyeLEDs() {
        ledModel.setTextfieldText();
        ledModel.fadeLEDs("RightFaceLeds");
    }
    public void changeLeftEyeLEDs() {
        ledModel.setTextfieldText();
        ledModel.fadeLEDs("LeftFaceLeds");
    }
    public void changeFeetLEDs() {
        ledModel.setTextfieldText();
        ledModel.fadeLEDs("FeetLeds");
    }


    //ausgewählte Farbe bekommt schwarzen Rahmen
    public void buttonFaceLedsRed() {
        ledModel.buttonFaceLedsRed();
    }
    public void buttonFaceLedsBlue() {
        ledModel.buttonFaceLedsBlue();
    }
    public void buttonFaceLedsGreen() {
        ledModel.buttonFaceLedsGreen();
    }
    public void buttonFaceLedsYellow() {
        ledModel.buttonFaceLedsYellow();
    }
    public void buttonFaceLedsCyan() {
        ledModel.buttonFaceLedsCyan();
    }
    public void buttonFaceLedsMagenta() {
        ledModel.buttonFaceLedsMagenta();
    }
    public void buttonFaceLedsWhite() {
        ledModel.buttonFaceLedsWhite();
    }
    public void buttonFaceLedsOrange() {
        ledModel.buttonFaceLedsOrange();
    }

    //Benutzereingabe prüfen
    public void textfieldKeyReleased(KeyEvent keyEvent) {
        ledModel.textfieldKeyReleased(keyEvent);
    }
    public void textfieldKeyTyped() {
        ledModel.textfieldKeyTyped();
    }
    public void textfieldMouseClicked() {
        ledModel.textfieldMouseClicked();
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
            speech.understand =false;
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
        movement.goToRest(false); }
    public void rest()throws Exception {
        movement.goToRest(true); }
    public void standUp() {
        movement.goToPosture("Stand"); }
    public void sitRelax() {
        movement.goToPosture("SitRelax"); }
    public void crouch() {
        movement.goToPosture("Crouch"); }
    public void lyingBack() {
        movement.goToPosture("LyingBack"); }
    public void lyingBelly() {
        movement.goToPosture("LyingBelly"); }
    public void standZero() {
        movement.goToPosture("StandZero"); }
    public void standInit() {
        movement.goToPosture("StandInit");
    }

    //Kopf
    public void headUp() {
        movement.moveHead("up"); }
    public void headLeft() {
        movement.moveHead("left"); }
    public void headDown() {
        movement.moveHead("down"); }
    public void headRight() {
        movement.moveHead("right"); }

    //Walk
    //Laufgeschwindigkeit speichern
    public void getWalkSpeedSliderValue() {
        walkSpeedValue = (float)(walkSpeedSlider.getValue()); }

    //loslaufen
    public void walkForwards() {
        movement.move(walkSpeedValue, 0F, 0F); }
    public void walkLeft() {
        movement.move(0F, walkSpeedValue, 0F); }
    public void walkBackwards() {
        movement.move(-walkSpeedValue, 0F, 0F); }
    public void walkRight() {
        movement.move(0F, -walkSpeedValue, 0F); }

    //laufen stoppen
    public void stopWalking() {
        movement.stopWalking();
    }

    //loslaufen durch W/A/S/D Tasten
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.W) {
            movement.move(walkSpeedValue, 0F, 0F);
        } else if (keyEvent.getCode() == KeyCode.A) {
            movement.move(0F, walkSpeedValue, 0F);
        } else if (keyEvent.getCode() == KeyCode.S) {
            movement.move(-walkSpeedValue, 0F, 0F);
        } else if (keyEvent.getCode() == KeyCode.D) {
            movement.move(0F, -walkSpeedValue, 0F);
        }
    }
    //laufen stoppen durch W/A/S/D Taste loslassen
    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.W || keyEvent.getCode() == KeyCode.A || keyEvent.getCode() == KeyCode.S ||keyEvent.getCode() == KeyCode.D) {
            movement.stopWalking();
        }
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
