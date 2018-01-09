package Main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import com.aldebaran.qi.Application;

public class ConnectController {

    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;

    private Main mainClass;
    private Application app;

    public void setMainClass(Main main)
    {
        // main-Klasse merken, um ueber diese das Hauptmenue zu oeffnen.
        this.mainClass = main;
    }

    public void executeConnect() {
        String sUrl = "tcp://" + txtIP.getText() + ":" + txtPort.getText();

        // Verbindung aufbauen
        System.out.println("try connecting to " + sUrl + " ...");
        this.app = new Application(new String[]{""}, sUrl);
        try{
            this.app.start();
            // erfolgreich verbunden
            mainClass.startMainMenu();
        }
        catch(Exception e){
            System.out.println("Verbindung konnte nicht hergestellt werden.");
            this.app.stop();
            this.app= null;
        }
    }

}
