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
    private Label lblConnectionState;
    @FXML
    private Button btnConnect;
    @FXML
    private Button btnDisconnect;

    private Main mainClass;
    private Session session;

    public void setMainClass(Main main)
    {
        // main-Klasse merken, um ueber diese das Hauptmenue zu oeffnen.
        this.mainClass = main;
    }

    public void connect() {
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
        setInfoText("connecting to " + naoUrl + " ...");
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
            lblInfo.setTextFill(Color.GREEN);
            setInfoText("Connection established.");
            lblConnectionState.setTextFill(Color.GREEN);
            lblConnectionState.setText("Connected to " + naoUrl);
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
        lblConnectionState.setTextFill(Color.RED);
        lblConnectionState.setText("Disconnected.");
    }

    private void setInfoText(String text){
        lblInfo.setText(text);
        System.out.println(text);
    }

}
