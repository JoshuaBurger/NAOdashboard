package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

class TactilSpeechTask {
    public String text;
    public String language;
    public int speed;
    public int pitch;
}

public class HeadSensorModel {

    private Label lblInfo;

    private MainMenuController controller;
    private Session session;
    private boolean sensorsRegistered;

    private TactilSpeechTask front;
    private TactilSpeechTask middle;
    private TactilSpeechTask rear;

    public HeadSensorModel(MainMenuController controller) {
        // Leere Tasks anlegen
        this.controller = controller;
        front = new TactilSpeechTask();
        middle = new TactilSpeechTask();
        rear = new TactilSpeechTask();

        lblInfo = controller.lblHeadSensorInfo;
    }

    public void setSession(Session session) {
        this.session = session;
        sensorsRegistered = false;
    }

    public void registerTactilEvents(ALMemory memory) {
        // NAO-Events der Kopfsensoren auf Methoden registrieren.
        try {
            memory.subscribeToEvent("FrontTactilTouched",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float state) {
                            if ( state == 1 ) {
                                onTactilTouched("Front");
                            }
                        }
                    });
            memory.subscribeToEvent("MiddleTactilTouched",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float state) {
                            if ( state == 1 ) {
                                onTactilTouched("Middle");
                            }
                        }
                    });
            memory.subscribeToEvent("RearTactilTouched",
                    new EventCallback<Float>() {
                        @Override
                        public void onEvent(Float state) {
                            if ( state == 1 ) {
                                onTactilTouched("Rear");
                            }
                        }
                    });
            sensorsRegistered = true;

        } catch(Exception e) {
            System.out.println("Couldn't register battery events");
        }
    }

    private void onTactilTouched(String type) {
        TactilSpeechTask task = null;

        // Gespeicherte Daten des Sensors raussuchen
        switch(type) {
            case "Front":
                task = front;
                break;
            case "Middle":
                task = middle;
                break;
            case "Rear":
                task = rear;
                break;
            default:
                break;
        }
        // GUI kann nicht aus dem naoqi-messaging thread veraendert werden, daher runLater
        Platform.runLater(new Runnable() {
            public void run() {
                lblInfo.setTextFill(Color.BLACK);
                controller.displayTextTemporarily(lblInfo, type + " tactil touched.", 2000);
            }
        });

        if ( (task != null) && (task.text != null) && (task.language != null) ) {
            // NAO den gespeicherten Text sagen lassen
            controller.saySomething(task.text, task.speed, task.pitch, task.language);
        }
    }

    public void setHeadSensorSpeechTask (String type, String text, int speed, int pitch, String language) {
        TactilSpeechTask task = null;

        // Gewuenschte Sprach-Verknuepfung zum richtigen Sensor merken
        switch(type) {
            case "Front":
                task = front;
                break;
            case "Middle":
                task = middle;
                break;
            case "Rear":
                task = rear;
                break;
            default:
                System.out.println("Unknown Sensor");
                break;
        }

        if ( task != null ) {
            task.text = text;
            task.speed = speed;
            task.pitch = pitch;
            task.language = language;
        }

        if ( sensorsRegistered ) {
            lblInfo.setTextFill(Color.GREEN);
            controller.displayTextTemporarily(lblInfo, "Speech config applied to sensor.", 2000);
        }
        else {
            lblInfo.setTextFill(Color.RED);
            controller.displayTextTemporarily(lblInfo, "Sensor is not available...", 2000);
        }
    }
}
