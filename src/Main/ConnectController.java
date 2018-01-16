package Main;

import com.aldebaran.qi.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML
    private Button btnConnect;

    private Main mainClass;
    private Session session;

    public void setMainClass(Main main)
    {
        // main-Klasse merken, um ueber diese das Hauptmenue zu oeffnen.
        this.mainClass = main;
    }

    public void executeConnect() {
        boolean bConnected = false;
        String naoUrl = "tcp://" + txtIP.getText() + ":" + txtPort.getText();

        lblInfo.setTextFill(Color.BLACK);
        lblInfo.setText("");

        if( session != null ){
            // Aktuelle Verbindung schließen
            session.close();
            session = null;
        }
        // Button disablen, um mehrfachen Connect zu unterbinden
        btnConnect.setDisable(true);

        // Verbindung aufbauen
        session = new Session();
        lblInfo.setText("Verbinde zu " + naoUrl + " ...");
        System.out.println("Verbinde zu " + naoUrl + " ...");
        try{
            session.connect(naoUrl);
            if ( session.isConnected() ) {
                bConnected = true;
            }
        }
        catch(Exception e){
        }

        if( bConnected == true){
            // erfolgreich verbunden
            System.out.println("Verbunden.");
            try {
                mainClass.startMainMenu();
            }
            catch(Exception e) {
                lblInfo.setTextFill(Color.RED);
                lblInfo.setText("Hauptmenü kann nicht geoeffnet werden");
                System.out.println("Hauptmenue kann nicht geoeffnet werden");
            }
        }
        else{
            lblInfo.setTextFill(Color.RED);
            lblInfo.setText("Verbindung konnte nicht hergestellt werden.");
            System.out.println("Verbindung konnte nicht hergestellt werden.");

            // Connect-Button wieder aktivieren und session zurücksetzen
            btnConnect.setDisable(false);
            session.close();
            session = null;
        }

        mainClass.setSession(session);
    }

}
