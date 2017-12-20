package Main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private TextField txtIP;

    public void executeConnect() {
        System.out.println("connecting to " + txtIP.getText() + " ...");
    }
}
