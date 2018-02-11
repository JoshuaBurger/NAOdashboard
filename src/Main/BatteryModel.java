package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALBattery;
import com.aldebaran.qi.helper.proxies.ALMemory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class BatteryModel {

    private BatteryGUIrefresher batteryGUIrefresher;
    private int charge = -1;
    private boolean isCharging = false;

    public BatteryModel(MainMenuController mainController ) {
        // Liste der Batterie Label/Bilder aller Tabs erstellen
        // Um aktuellen Batteriezustand anzuzeigen
        ArrayList<ImageView> imageList = new ArrayList<ImageView>();
        ArrayList<Label> labelList = new ArrayList<Label>();

        imageList.add(mainController.imgBattery1);
        imageList.add(mainController.imgBattery2);
        imageList.add(mainController.imgBattery3);
        imageList.add(mainController.imgBattery4);
        labelList.add(mainController.lblBattery1);
        labelList.add(mainController.lblBattery2);
        labelList.add(mainController.lblBattery3);
        labelList.add(mainController.lblBattery4);

        // batteryGUIrefresher is needed to update the GUI from other thread than JavaFX...
        batteryGUIrefresher = new BatteryGUIrefresher(labelList, imageList);
    }

    public void setSession(Session session) {
        try {
            ALBattery battery = new ALBattery(session);

            // Batterie Ueberwachung aktivieren
            battery.enablePowerMonitoring(true);
            // Momentanen Ladezustand holen
            charge = battery.getBatteryCharge();
        } catch(Exception e) {
            charge = -1;
        }
        // Ob gerade geladen wird, kann nicht direkt ermittelt werden
        // Bei ein-/ausstecken des Kabels wird BatteryChargingFlagChanged gefeuert
        isCharging = false;
        displayCurrentBatteryState();
    }

    public void registerBatteryEvents(ALMemory memory) {
        try {
            memory.subscribeToEvent("BatteryChargingFlagChanged",
                    new EventCallback<Boolean>() {
                        @Override
                        public void onEvent(Boolean state) {
                            isCharging = state;
                            displayCurrentBatteryState();
                        }
                    });
            memory.subscribeToEvent("BatteryChargeChanged",
                    new EventCallback<Integer>() {
                        @Override
                        public void onEvent(Integer state) {
                            charge = state;
                            System.out.println("current battery state: " + charge);
                            displayCurrentBatteryState();
                        }
                    });
        } catch(Exception e) {
            System.out.println("Couldn't register battery events");
        }
    }

    public void displayCurrentBatteryState() {
        String lblChargeText = "";
        String batteryState = "unknown";
        String chargingState = "";
        String iconPath = "";

        if ( charge != -1 ) {
            lblChargeText = (charge + "%");
            chargingState = (isCharging) ? "_charging" : "";

            if ( charge > 80 ) {
                batteryState = "full";
            }
            else if ( charge > 60 ) {
                batteryState = "good";
            }
            else if ( charge > 30 ) {
                batteryState = "middle";
            }
            else {
                batteryState = "low";
            }
        }

        // Pfad des richtigen Icons holen
        iconPath = getClass().getResource("images/battery_icons/battery_" + batteryState + chargingState + ".bmp").toString();

        // Da diese Methode aus dem naoqi-messaging Thread aufgerufen wird, aktualisieren wir die GUI (Akku-Icon und Ladezustand)
        // ueber eine kleine Wrapper-Methode, die die Aktualisierung im JavaFX-Thread vornimmt.
        // Andernfalls gibt es eine Exception.
        batteryGUIrefresher.setValues(lblChargeText, iconPath);
        Platform.runLater(batteryGUIrefresher);
    }

    class BatteryGUIrefresher implements Runnable {
        private ArrayList<Label> chargeLabels;
        private ArrayList<ImageView> batteryIcons;
        private String charge;
        private String iconPath;

        public BatteryGUIrefresher(ArrayList<Label> chargeLabels, ArrayList<ImageView> batteryIcons ) {
            this.chargeLabels = chargeLabels;
            this.batteryIcons = batteryIcons;
        }

        @Override public void run() {
            // Ladezustand in alle Label setzen
            if ( chargeLabels != null ) {
                for (Label lbl : chargeLabels) {
                    lbl.setText(charge);
                }
            }
            // Zutreffendes Bild in alle Container setzen
            if ( batteryIcons != null ) {
                for (ImageView img : batteryIcons) {
                    img.setImage(new Image(iconPath));
                }
            }
        }

        public void setValues(String charge, String iconPath) {
            this.charge = charge;
            this.iconPath = iconPath;
        }
    }
}
