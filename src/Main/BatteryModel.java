package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALBattery;
import com.aldebaran.qi.helper.proxies.ALMemory;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class BatteryModel {

    private ArrayList<ImageView> imgBatteryList;
    private ArrayList<Label> lblChargeList;
    private int charge = -1;
    private boolean isCharging = false;

    public void setGUIcomponents(ArrayList<ImageView> imgBatteryList, ArrayList<Label> lblChargeList) {
        this.imgBatteryList = imgBatteryList;
        this.lblChargeList = lblChargeList;
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

        if ( charge != -1 ) {
            lblChargeText = (charge + "%");
            chargingState = (isCharging) ? "_charging" : "";

            if ( charge > 80 ) {
                batteryState = "full";
            }
            if ( charge > 60 ) {
                batteryState = "good";
            }
            if ( charge > 30 ) {
                batteryState = "middle";
            }
            else {
                batteryState = "low";
            }
        }

        // Ladezustand in alle Label setzen
        for ( Label lbl : lblChargeList ) {
            lbl.setText(lblChargeText);
        }
        // Zutreffendes Bild in alle Container setzen
        for ( ImageView img : imgBatteryList ) {
            img.setImage(new Image(getClass().getResource("images/battery_icons/battery_" + batteryState + chargingState + ".bmp").toString()));
        }
    }
}
