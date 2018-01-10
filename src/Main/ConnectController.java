package Main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.aldebaran.qi.Application;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ConnectController {

    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;
    @FXML
    private Label lblInfo;

    private Main mainClass;
    private Application app;
    private String naoUrl;

    public void setMainClass(Main main)
    {
        // main-Klasse merken, um ueber diese das Hauptmenue zu oeffnen.
        this.mainClass = main;
    }

    public void executeConnect() {
        boolean bConnected = false;

        lblInfo.setTextFill(Color.BLACK);
        lblInfo.setText("");

        if( this.app == null ){
            naoUrl = "tcp://" + txtIP.getText() + ":" + txtPort.getText();
            // Application erstellen
            app = new Application(new String[]{}, naoUrl);
        }

        // Verbindung aufbauen
        try{
            lblInfo.setText("Verbinde zu " + naoUrl + " ...");
            System.out.println("Verbinde zu " + naoUrl + " ...");
            app.start();
            bConnected = true;
        }
        catch(Exception e){
            System.out.println("Verbindung konnte nicht hergestellt werden.");
            lblInfo.setTextFill(Color.RED);
            lblInfo.setText("Verbindung konnte nicht hergestellt werden.");
            app.stop();
        }

        if( bConnected == true){
            // erfolgreich verbunden
            System.out.println("Verbunden.");
            try {
                mainClass.startMainMenu();
            }
            catch(Exception e) {
                System.out.println("Hauptmenue kann nicht geoeffnet werden");
            }
        }
    }

}
