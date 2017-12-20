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
        System.out.println("connecting to " + txtIP.getText() + ":" + txtPort.getText() + " ...");
    }

}
