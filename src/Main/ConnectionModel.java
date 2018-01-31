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

public class ConnectionModel {
    @FXML
    private Label lblConnectionInfo;
    @FXML
    private Label lblConnectionState;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;
    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;

    private MainMenuController mainMenuController;
    private Session session;
    private String naoIP;
    private String naoPort;
    private boolean initialized;

    public ConnectionModel(MainMenuController controller)
    {
        this.mainMenuController = controller;
        initialized = false;
    }

    private void init() {
        initialized = true;
        lblConnectionInfo   = mainMenuController.lblConnectionInfo;
        lblConnectionState  = mainMenuController.lblConnectionState;
        btnConnect          = mainMenuController.btnConnect;
        btnDisconnect       = mainMenuController.btnDisconnect;
        txtIP               = mainMenuController.txtConnectionIP;
        txtPort             = mainMenuController.txtConnectionPort;
    }

    public void connect() {
        boolean bConnected = false;

        if ( initialized == false ) {
            init();
        }

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
            mainMenuController.setSession(session);
            mainMenuController.saySomething("Connected.");
        }
        else{
            setInfoText("Couldn't connect to NAO.", Color.RED);
            setConnectionState();
            // session zurücksetzen
            session.close();
            session = null;
            mainMenuController.setSession(null);
        }
    }

    public void disconnect(){
        if ( session != null ){
            mainMenuController.saySomething("Disconnected.");
            session.close();
            session = null;
        }
        setInfoText("Connection closed.", Color.BLACK);
        setConnectionState();
        mainMenuController.setSession(session);
    }

    private void setInfoText(String text, Color color){
        lblConnectionInfo.setTextFill(color);
        lblConnectionInfo.setText(text);
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
