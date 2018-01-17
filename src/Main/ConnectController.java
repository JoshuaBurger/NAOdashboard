package Main;

import com.aldebaran.qi.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import com.aldebaran.qi.Application;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static java.lang.Thread.*;

public class ConnectController {

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

    public void setMainClass(Main main)
    {
        // main-Klasse merken, um ueber diese das Hauptmenue zu oeffnen.
        this.mainClass = main;
    }

    public void connect() {
        boolean bConnected = false;
        naoIP = txtIP.getText();
        naoPort = txtPort.getText();
        String naoUrl = "tcp://" + naoIP + ":" + naoPort;

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
        setInfoText("connecting to " + naoUrl + " ...");
        try{
            session.connect(naoUrl);
            Thread.sleep(500);
            if ( session.isConnected() ) {
                bConnected = true;
            }
        }
        catch(Exception e){
        }

        if( bConnected == true){
            // erfolgreich verbunden
            lblInfo.setTextFill(Color.GREEN);
            setInfoText("Connection established.");
            setConnectionState();
            btnDisconnect.setDisable(false);
            try {
                mainClass.startMainMenu();
            }
            catch(Exception e) {
                lblInfo.setTextFill(Color.RED);
                setInfoText("Main menu cannot be opened.");
            }
        }
        else{
            lblInfo.setTextFill(Color.RED);
            setInfoText("Couldn't connect to NAO.");

            // Connect-Button wieder aktivieren und session zurücksetzen
            btnConnect.setDisable(false);
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
        btnDisconnect.setDisable(true);
        btnConnect.setDisable(false);
        lblInfo.setTextFill(Color.BLACK);
        setInfoText("Connection closed.");
        setConnectionState();
    }

    public void setDataToView(){
        // View wurde neu geladen, daher muessen alle Daten gesetzt werden
        if ( (naoIP != null) && (naoPort != null) ){
            txtIP.setText(naoIP);
            txtPort.setText(naoPort);
        }
        setConnectionState();
    }

    private void setInfoText(String text){
        lblInfo.setText(text);
        System.out.println(text);
    }

    private void setConnectionState(){
        if( (session == null) || (session.isConnected() == false) ){
            lblConnectionState.setTextFill(Color.RED);
            lblConnectionState.setText("Disconnected.");
        }
        else{
            lblConnectionState.setTextFill(Color.GREEN);
            lblConnectionState.setText("Connected to " + naoIP + ":" + naoPort);
        }
    }

}
