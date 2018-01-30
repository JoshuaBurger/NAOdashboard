package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;

class TactilSpeechTask {
    public String text;
    public String language;
    public int speed;
    public int pitch;
}

public class HeadSensorModel {

    private MainMenuController controller;
    private Session session;

    private TactilSpeechTask front;
    private TactilSpeechTask middle;
    private TactilSpeechTask rear;

    public HeadSensorModel(MainMenuController controller) {
        // Leere Tasks anlegen
        this.controller = controller;
        front = new TactilSpeechTask();
        middle = new TactilSpeechTask();
        rear = new TactilSpeechTask();
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void registerTactilEvents(ALMemory memory) {
        // NAO-Events der Kopfsensoren auf Methoden registrieren.
        try {
            memory.subscribeToEvent("FrontTactilTouched",
                    new EventCallback<String>() {
                        @Override
                        public void onEvent(String value) {
                            onTactilTouched("Front");
                        }
                    });
            memory.subscribeToEvent("MiddleTactilTouched",
                    new EventCallback<String>() {
                        @Override
                        public void onEvent(String value) {
                            onTactilTouched("Middle");
                        }
                    });
            memory.subscribeToEvent("RearTactilTouched",
                    new EventCallback<String>() {
                        @Override
                        public void onEvent(String value) {
                            onTactilTouched("Rear");
                        }
                    });
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
    }
}
