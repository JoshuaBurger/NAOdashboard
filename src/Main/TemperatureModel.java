package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALBodyTemperature;
import com.aldebaran.qi.helper.proxies.ALMemory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.ArrayList;

public class TemperatureModel {
    private MainMenuController mainController;
    private TemperatureGUIrefresher tempGUIrefresher;

    public TemperatureModel(MainMenuController mainController ) {
        this.mainController = mainController;
        // Alle Label holen...
        ArrayList<Label> headLabels = new ArrayList<>();
        ArrayList<Label> lArmLabels = new ArrayList<>();
        ArrayList<Label> rArmLabels = new ArrayList<>();
        ArrayList<Label> lLegLabels = new ArrayList<>();
        ArrayList<Label> rLegLabels = new ArrayList<>();
        headLabels.add(mainController.lblHead1);
        headLabels.add(mainController.lblHead2);
        headLabels.add(mainController.lblHead3);
        headLabels.add(mainController.lblHead4);
        lArmLabels.add(mainController.lblLArm1);
        lArmLabels.add(mainController.lblLArm2);
        lArmLabels.add(mainController.lblLArm3);
        lArmLabels.add(mainController.lblLArm4);
        rArmLabels.add(mainController.lblRArm1);
        rArmLabels.add(mainController.lblRArm2);
        rArmLabels.add(mainController.lblRArm3);
        rArmLabels.add(mainController.lblRArm4);
        lLegLabels.add(mainController.lblLLeg1);
        lLegLabels.add(mainController.lblLLeg2);
        lLegLabels.add(mainController.lblLLeg3);
        lLegLabels.add(mainController.lblLLeg4);
        rLegLabels.add(mainController.lblRLeg1);
        rLegLabels.add(mainController.lblRLeg2);
        rLegLabels.add(mainController.lblRLeg3);
        rLegLabels.add(mainController.lblRLeg4);
        tempGUIrefresher = new TemperatureGUIrefresher(headLabels, lArmLabels, rArmLabels, lLegLabels, rLegLabels);
    }

    public void setSession(Session session) {
        try{
            ALBodyTemperature temperature = new ALBodyTemperature(session);

            // Temperatur Events aktivieren
            temperature.setEnableNotifications(true);
            // Initial erstmal alle Temperaturstati auf "gut" setzen
            evaluateTemperatureDiagnosis(null,true);
            // Momentanen Temperaturdiagnose abholen
            Object obj = temperature.getTemperatureDiagnosis();
            if ( obj != null ) {
                evaluateTemperatureDiagnosis(obj,true);
            }
        } catch(Exception e) {
            if ( session != null ) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void registerTemperatureEvents(ALMemory memory) {
        try {
            memory.subscribeToEvent("TemperatureDiagnosisErrorChanged",
                    new EventCallback<Object>() {
                        @Override
                        public void onEvent(Object obj) {
                            // Momentane Temperaturdiagnose auswerten
                            evaluateTemperatureDiagnosis(obj,false);
                        }
                    });
            memory.subscribeToEvent("TemperatureStatusChanged",
                    new EventCallback<Object>() {
                        @Override
                        public void onEvent(Object obj) {
                            // Momentane Temperaturdiagnose auswerten
                            evaluateTemperatureDiagnosis(obj,false);
                        }
                    });

        } catch(Exception e) {
            System.out.println("Couldn't register temperature events");
        }
    }

    private void evaluateTemperatureDiagnosis(Object obj, boolean fromMainThread) {
        int state = -1;
        ArrayList<String> chains = null;

        if (obj != null) {
            if (obj instanceof ArrayList) {
                ArrayList<Object> tempDiag = (ArrayList) obj;

                // Diagnose-Stufe holen (level of failure severity: 0(NEGLIGIBLE), 1(SERIOUS) or 2(CRITICAL) )
                state = (int) tempDiag.get(0);
                // Betroffene Chains (Head,LArm,RArm,LLeg,RLeg)
                chains = (ArrayList<String>) tempDiag.get(1);
                System.out.println("Temperatur-Status:" + state);
                System.out.println("Betroffene Chains: ");
                for (String s : chains) {
                    System.out.print(s + " ");
                }
            }
        }
        if (chains == null) {
            // Dann war das TemperaturDiagnose-Objekt null, da die Temperatur ok ist.
            state = 0;
            chains = new ArrayList<String>();
            chains.add("Head");
            chains.add("LArm");
            chains.add("RArm");
            chains.add("LLeg");
            chains.add("RLeg");
        }

        // Da diese Methode meist aus dem naoqi-messaging Thread aufgerufen wird, aktualisieren wir die GUI (Akku-Icon und Ladezustand)
        // ueber eine kleine Wrapper-Methode, die die Aktualisierung im JavaFX-Thread vornimmt.
        // Andernfalls gibt es eine Exception.
        tempGUIrefresher.setValues(state, chains);
        if ( fromMainThread == false ) {
            Platform.runLater(tempGUIrefresher);
        }
        else {
            // In diesem Fall muessen wir nicht warten
            tempGUIrefresher.run();
        }

    }

    class TemperatureGUIrefresher implements Runnable {
        private ArrayList<String> chains;
        private int tempState;
        private ArrayList<Label> headLabels;
        private ArrayList<Label> lArmLabels;
        private ArrayList<Label> rArmLabels;
        private ArrayList<Label> lLegLabels;
        private ArrayList<Label> rLegLabels;

        public TemperatureGUIrefresher(ArrayList<Label> headLabels, ArrayList<Label> lArmLabels, ArrayList<Label> rArmLabels, ArrayList<Label> lLegLabels, ArrayList<Label> rLegLabels) {
            this.headLabels = headLabels;
            this.lArmLabels = lArmLabels;
            this.rArmLabels = rArmLabels;
            this.lLegLabels = lLegLabels;
            this.rLegLabels = rLegLabels;
        }

        public void setValues(int state, ArrayList<String> chains) {
            this.tempState = state;
            this.chains = chains;
        }

        @Override public void run() {
            for ( String chain : chains ) {
                ArrayList<Label> currentLabels = null;

                switch(chain) {
                    case "Head":
                        currentLabels = headLabels;
                        break;
                    case "LArm":
                        currentLabels = lArmLabels;
                        break;
                    case "RArm":
                        currentLabels = rArmLabels;
                        break;
                    case "LLeg":
                        currentLabels = lLegLabels;
                        break;
                    case "RLeg":
                        currentLabels = rLegLabels;
                        break;
                    default:
                        System.out.println("Temperature: " + chain + " is no chain...");
                        break;
                }
                if ( currentLabels != null ) {
                    for ( Label label : currentLabels ) {
                        switch (tempState) {
                            case 0:
                                label.setTextFill(Color.GREEN);
                                label.setText("Good");
                                break;
                            case 1:
                                label.setTextFill(Color.YELLOW);
                                label.setText("Serious");
                                break;
                            case 2:
                                label.setTextFill(Color.RED);
                                label.setText("Critical");
                                break;
                            default:
                                System.out.println("Temperature: State " + tempState + " is not defined");
                                return;
                        }
                    }
                }
            }
        }
    }
}
