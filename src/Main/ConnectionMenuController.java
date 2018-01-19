package Main;

import com.aldebaran.qi.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.aldebaran.qi.Application;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.*;

public class ConnectionMenuController {

    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;
    @FXML
    private Label lblInfo;
    @FXML
    private Label lblConnectionState;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;

    private Main mainClass;
    private Session session;

    private String naoIP;
    private String naoPort;

    public ConnectionMenuController(Main main)
    {
        // main-Klasse merken, um ueber diese das Hauptmenue zu oeffnen.
        this.mainClass = main;
    }

    public void connect() {
        boolean bConnected = false;
        naoIP = txtIP.getText();
        naoPort = txtPort.getText();
        String naoUrl = "tcp://" + naoIP + ":" + naoPort;

        if( session != null ){
            // Aktuelle Verbindung schließen
            session.close();
            session = null;
        }
        // Button disablen, um mehrfachen Connect zu unterbinden
        btnConnect.setDisable(true);

        session = new Session();
        setInfoText("Connecting to " + naoUrl + " ...", Color.BLACK);
        try{
            // Verbindungsversuch (blockierend mit 5 Sek Timeout, da das default Timeout sehr lang ist.)
            session.connect(naoUrl).get(5, TimeUnit.SECONDS);
            if ( session.isConnected() ) {
                bConnected = true;
            }
        }
        catch(Exception e){
            // Fehler wird unten mitbehandelt
        }

        if( bConnected == true){
            // Erfolgreich verbunden
            setInfoText("Connection established.", Color.GREEN);
            setConnectionState();
            startMainMenu();
        }
        else{
            setInfoText("Couldn't connect to NAO.", Color.RED);
            setConnectionState();
            // session zurücksetzen
            session.close();
            session = null;
        }

        mainClass.setSession(session);
    }

    public void disconnect(){
        if ( session != null ){
            session.close();
            session = null;
        }
        setInfoText("Connection closed.", Color.BLACK);
        setConnectionState();
        mainClass.setSession(session);
    }

    public void startMainMenu() {
        try {
            mainClass.startMainMenu();
        }
        catch(Exception e) {
            setInfoText("Main menu cannot be opened.", Color.RED);
        }
    }

    public void setDataToView(){
        // View wurde neu geladen, daher muessen alle Daten gesetzt werden
        if ( (naoIP != null) && (naoPort != null) ){
            txtIP.setText(naoIP);
            txtPort.setText(naoPort);
        }
        setConnectionState();
    }


    private void setInfoText(String text, Color color){
        lblInfo.setTextFill(color);
        lblInfo.setText(text);
        System.out.println(text);
    }

    private void setConnectionState(){
        if( (session == null) || (session.isConnected() == false) ){
            lblConnectionState.setTextFill(Color.RED);
            lblConnectionState.setText("Disconnected.");
            btnConnect.setDisable(false);
            btnDisconnect.setDisable(true);
        }
        else{
            lblConnectionState.setTextFill(Color.GREEN);
            lblConnectionState.setText("Connected to " + naoIP + ":" + naoPort);
            btnConnect.setDisable(true);
            btnDisconnect.setDisable(false);
        }
    }

}
