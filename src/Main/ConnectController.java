package Main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ConnectController {

    @FXML
    private TextField txtIP;
    @FXML
    private TextField txtPort;

    // Das Applikations-Objekt
    private Main app;

    public void setApplication(Main app)
    {
        this.app = app;
    }

    public void executeConnect() {
        System.out.println("try connecting to " + txtIP.getText() + ":" + txtPort.getText() + " ...");
        // TODO: Verbindung aufbauen
        // Wenn erfolgreich verbunden
        if ( app != null ) {
            // Hauptmenue starten
            try {
                app.startMainMenu();
            }
            catch(Exception e){}
        }
    }

}
