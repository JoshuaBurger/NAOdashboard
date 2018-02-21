package Main;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALFaceDetection;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRedBallDetection;
import com.aldebaran.qi.helper.proxies.ALTracker;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TrackerModel {

    private Pane configPane;
    private ToggleGroup toggleGroupTarget;
    private ToggleGroup toggleGroupMode;
    private ToggleGroup toggleGroupEffector;
    private Spinner spinnerTargetSize;
    private Button btnStartTracking;
    private Button btnStopTracking;
    private Label lblInfo;

    private MainMenuController mainController;
    private Session session;
    private long eventId;
    private boolean targetDetected;

    private String target; // RedBall, Face, LandMark
    private String mode; // Head, WholeBody, Move
    private String effector; // None LArm, RArm, Arms
    private float targetSize; // in meters

    public TrackerModel(MainMenuController main) {
        this.mainController = main;
        toggleGroupTarget   = main.toggleGroupTrackTarget;
        toggleGroupMode     = main.toggleGroupTrackMode;
        toggleGroupEffector = main.toggleGroupTrackEffector;
        spinnerTargetSize   = main.spinTrackTargetSize;
        btnStartTracking    = main.btnStartTracking;
        btnStopTracking     = main.btnStopTracking;
        configPane          = main.paneTrackingConfig;
        lblInfo             = main.lblTrackingInfo;
    }

    public void setSession(Session session) {
        this.session = session;
        targetDetected = false;
        eventId = -1;
        target = "";
        mode = "";
        effector = "";

        // Buttons etc. in Ausgangszustand versetzen
        lblInfo.setText("");
        configPane.setDisable(false);
        btnStopTracking.setDisable(true);
        btnStartTracking.setDisable(false);
    }

    public void startTracking() {
        // Hier warten wir zunaechst auf Benachrichtigung, das Target gefunden wurde
        try {
            // Zunaechst mal Konfiguration laden
            loadConfiguration();

            // Event callback anlegen
            EventCallback eventCallback = new EventCallback<Object>() {
                @Override
                public void onEvent(Object obj) {
                    // Target wurde gefunden
                    if (targetDetected == false) {
                        // Das Tracken darf nur einmal gestartet werden, darum Mehrfachausfuerung verhindern
                        targetDetected = true;
                        try {
                            mainController.memory.unsubscribeToEvent(eventId);
                        } catch (Exception e) {
                        }
                        // Nun wird das eigentliche Tracking gestartet, da ein Target vorhanden ist
                        triggerRealTracking();
                    }

                }
            };

            if ( target.equals("RedBall") ) {
                // RedBall Event-Triggering auf NAO-Seite starten
                ALRedBallDetection detect = new ALRedBallDetection(session);
                detect.subscribe("redBallDetected");
                // Event subscriben
                eventId = mainController.memory.subscribeToEvent("redBallDetected", eventCallback);
            }
            else {
                // Face Event-Triggering auf NAO-Seite starten
                ALFaceDetection detect = new ALFaceDetection(session);
                detect.subscribe("FaceDetected");
                // Event subscriben
                eventId = mainController.memory.subscribeToEvent("FaceDetected", eventCallback);
            }

            lblInfo.setText("Searching for target...");
            // Komponenten umschalten, um mehrfaches Starten oder Konfigurieren zu vermeiden
            configPane.setDisable(true);
            btnStopTracking.setDisable(false);
            btnStartTracking.setDisable(true);

        } catch(Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println("Error starting Tracking" + e.getMessage());
                mainController.displayTextTemporarily(lblInfo, "Couldn't start tracking...", 3000);
            }
        }
    }

    public void triggerRealTracking() {
        // Hier wird das eigentliche Tracking gestartet

        try {
            ALTracker tracker = new ALTracker(session);
            ALMotion motion = new ALMotion(session);

            // Initiale Position annehmen und Stiffness setzen
            mainController.wakeUp();
            mainController.standInit();
            motion.setStiffnesses("Head", 1.0);
            if ( mode.equals("Head") == false ) {
                // Fuer Move oder WholeBody body-stiffness setzen
                motion.setStiffnesses("Body", 1.0);
            }
            if ( effector.equals("None") == false ) {
                if (effector.equals("Arms)")) {
                    motion.setStiffnesses("LArm", 1.0);
                    motion.setStiffnesses("RArm", 1.0);
                } else {
                    motion.setStiffnesses(effector, 1.0);
                }
            }

            // Evtl. alte Tracker-Konfiguration zuruecksetzen
            tracker.removeAllTargets();
            // Neue Konfiguration setzen
            tracker.registerTarget(target, targetSize);
            tracker.setMaximumDistanceDetection(3.0f);
            tracker.setMode(mode);
            tracker.setEffector(effector);
            // Tracking starten
            tracker.track(target);

            // Label kann nicht aus naoqi messaging thread gesetzt werden
            Platform.runLater(new Runnable(){
                public void run() {
                    lblInfo.setText("Tracking target now...");
                }
            });

        } catch(Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println("Problem starting tracker, stopping now: " + e.getMessage());
            }
            stopTracking();
        }
    }

    public void stopTracking() {
        try {
            targetDetected = false;
            // Event unsubscriben
            mainController.memory.unsubscribeToEvent(eventId);
            // Suchen stoppen
            if ( target.equals("RedBall") ) {
                ALRedBallDetection detect = new ALRedBallDetection(session);
                detect.unsubscribe("redBallDetected");
            }
            else {
                ALFaceDetection detect = new ALFaceDetection(session);
                detect.unsubscribe("FaceDetected");
            }
            // Tracking stoppen und Ausgangsposition annehmen
            ALTracker tracker = new ALTracker(session);
            tracker.stopTracker();

            // Wieder in Grundzustand versetzen
            mainController.standUp();
            ALMotion motion= new ALMotion(session);
            motion.setStiffnesses("Head", 0.0);
            if ( effector.equals("None") == false ) {
                if (effector.equals("Arms)")) {
                    motion.setStiffnesses("LArm", 0.0);
                    motion.setStiffnesses("RArm", 0.0);
                } else {
                    motion.setStiffnesses(effector, 0.0);
                }
            }

        } catch(Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println("Problem stopping tracker: " + e.getMessage());
            }
        }

        mainController.displayTextTemporarily(lblInfo,"Tracking stopped.",2000);
        configPane.setDisable(false);
        btnStartTracking.setDisable(false);
        btnStopTracking.setDisable(true);
    }

    private void loadConfiguration() {
        // Target: RedBall, Face
        target = ((RadioButton)toggleGroupTarget.getSelectedToggle()).getText();
        // Target Groesse von cm in m konvertieren
        targetSize = (((float)((int)spinnerTargetSize.getValue())) / 100);
        // Tracking Mode: Head, WholeBody, Move
        mode = ((RadioButton)toggleGroupMode.getSelectedToggle()).getText();
        // Tracking effector: None LArm, RArm, Arms
        effector = ((RadioButton)toggleGroupEffector.getSelectedToggle()).getText();
        // Effector Text zu korrekter Bezeichnung transformieren
        switch(effector) {
            case "Left Arm":
                effector = "LArm";
                break;
            case "Right Arm":
                effector = "RArm";
                break;
            case "Both Arms":
                effector = "Arms";
                break;
            default:
                break;
        }
    }
}
