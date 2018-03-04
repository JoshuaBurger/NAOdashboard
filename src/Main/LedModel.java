package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALLeds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class LedModel {
    private Session session;
    private MainMenuController mainController;
    private Button btnRed;
    private Button btnBlue;
    private Button btnGreen;
    private Button btnYellow;
    private Button btnCyan;
    private Button btnMagenta;
    private Button btnWhite;
    private Button btnOrange;
    private Button buttonRGBpreview;
    private Button btnLeftEye;
    private Button btnRightEye;
    private Button btnBothEyes;
    private GridPane gridPaneColorButtons;
    private GridPane gridPaneLedsOff;
    private GridPane gridPaneLedsOn;
    private GridPane gridPaneApplyColor;
    private TextField textfieldRed;
    private TextField textfieldGreen;
    private TextField textfieldBlue;
    private Label labelAllowedValue;

    private int valueRedInteger = 255;
    private int valueGreenInteger = 0;
    private int valueBlueInteger = 0;
    private int caretPosition;
    private String selectedLedItem;
    private String hexRGBColor = "#FF0000";
    private String tfRedString = "255";
    private String tfGreenString = "0";
    private String tfBlueString = "0";

    public LedModel(MainMenuController main) {
        this.mainController = main;
        // JavaFX Komponenten holen
        this.btnRed = main.btnRed;
        this.btnBlue = main.btnBlue;
        this.btnGreen = main.btnGreen;
        this.btnYellow = main.btnYellow;
        this.btnCyan = main.btnCyan;
        this.btnMagenta = main.btnMagenta;
        this.btnWhite = main.btnWhite;
        this.btnOrange = main.btnOrange;
        this.textfieldRed = main.textfieldRed;
        this.textfieldGreen = main.textfieldGreen;
        this.textfieldBlue = main.textfieldBlue;
        this.buttonRGBpreview = main.buttonRGBpreview;
        this.btnLeftEye = main.btnLeftEye;
        this.btnRightEye = main.btnRightEye;
        this.btnBothEyes = main.btnBothEyes;
        this.gridPaneColorButtons = main.gridPaneColorButtons;
        this.gridPaneLedsOff = main.gridPaneLedsOff;
        this.gridPaneLedsOn = main.gridPaneLedsOn;
        this.gridPaneApplyColor = main.gridPaneApplyColor;
        this.labelAllowedValue = main.labelAllowedValue;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void turnledsOff(String name) {
        try {
            ALLeds leds = new ALLeds(session);
            leds.off(name);
        } catch (Exception e) {
            if ((session == null) || (session.isConnected() == false)) {
                mainController.handleConnectionClosed(true);
            }
            System.out.println(e.getMessage());
        }
    }
    public void turnledsOn(String name) {
        try {
            ALLeds leds = new ALLeds(session);
            leds.on(name);
        } catch (Exception e) {
            if ((session == null) || (session.isConnected() == false)) {
                mainController.handleConnectionClosed(true);
            }
            System.out.println(e.getMessage());
        }
    }

    //in leeres Textfeld 0 schreiben
    public void setTextfieldText() {
        textfieldRed.setText(tfRedString);
        textfieldGreen.setText(tfGreenString);
        textfieldBlue.setText(tfBlueString);
    }

    public void fadeLEDs(String ledsName) {
        try {
            ALLeds leds = new ALLeds(session);
            if (selectedLedItem == "rgbColor") {
                Float red = (Float.parseFloat(tfRedString) / 100);
                Float green = (Float.parseFloat(tfGreenString) / 100);
                Float blue = (Float.parseFloat(tfBlueString) / 100);
                leds.fadeRGB(ledsName, red, green, blue, 1F);
            } else if (selectedLedItem == "orange") {
                leds.fadeRGB(ledsName, 2.55F, 0.4F, 0F, 1F);
            } else {
                leds.fadeRGB(ledsName, selectedLedItem, 1F);
            }
        } catch(Exception e) {
            if ((session == null) || (session.isConnected() == false)) {
                mainController.handleConnectionClosed(true);
            }
            System.out.println(e.getMessage());
        }
    }


    //reagiert auf Eingabe von Buchstaben, Zahlen, Sonderzeichen und Leerzeichen
    public void textfieldKeyTyped() {
        tfRedString = textfieldRed.getText();
        tfGreenString = textfieldGreen.getText();
        tfBlueString = textfieldBlue.getText();

        //Buchstaben und Sonderzeichen entfernen
        if (!tfRedString.matches("[0-9 ]*") || !tfGreenString.matches("[0-9 ]*") || !tfBlueString.matches("[0-9 ]*")) {
            removeforbiddenCharacters();
        }
        //Leerzeichen entfernen
        if (tfRedString.contains(" ") || tfGreenString.contains(" ") || tfBlueString.contains(" ")) {
            removeWhitespaces();
        }
        //0 an erster Stelle entfernen
        removeNull();

        //leeres Textfeld mit Wert 0 belegen
        preventEmptyTextfield();

        //prüfen ob eingegebene Zahl >255
        checkValueHigher255();
    }

    //reagiert auf MausClicked
    public void textfieldMouseClicked() {
        checkValueHigher255();
    }

    private void checkValueHigher255() {
        if (compareValueTo255(tfRedString) && compareValueTo255(tfGreenString) && compareValueTo255(tfBlueString)) {
            RGBcolorPreview();                  //Vorschau der rgb-Farbe anzeigen
        } else {
            labelAllowedValue.setVisible(true);
            disableOtherItems(true);         //disable everything else

            if (!compareValueTo255(tfRedString)) {
                textfieldRed.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
                textfieldRed.setDisable(false);
            }
            if (!compareValueTo255(tfGreenString)) {
                textfieldGreen.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
                textfieldGreen.setDisable(false);
            }
            if (!compareValueTo255(tfBlueString)) {
                textfieldBlue.setStyle("-fx-border-color: red; -fx-border-width: 3; -fx-border-radius: 5;");
                textfieldBlue.setDisable(false);
            }
        }
    }

    private boolean compareValueTo255(String tfValue) {
        int tfValueInt;
        try {
            tfValueInt = Integer.parseInt(tfValue);
            if (tfValueInt > 255) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " // something went wrong while converting values of textfields.");
        }
        return true;
    }
    private void removeWhitespaces() {
        caretPosition = textfieldRed.getCaretPosition();
        tfRedString = tfRedString.replaceAll("\\s", "");
        textfieldRed.setText(tfRedString);
        textfieldRed.positionCaret(caretPosition-1);

        caretPosition = textfieldGreen.getCaretPosition();
        tfGreenString = tfGreenString.replaceAll("\\s", "");
        textfieldGreen.setText(tfGreenString);
        textfieldGreen.positionCaret(caretPosition-1);

        caretPosition = textfieldBlue.getCaretPosition();
        tfBlueString = tfBlueString.replaceAll("\\s", "");
        textfieldBlue.setText(tfBlueString);
        textfieldBlue.positionCaret(caretPosition-1);
    }
    private void removeforbiddenCharacters() {
        caretPosition = textfieldRed.getCaretPosition();
        tfRedString = tfRedString.replaceAll("[^0-9]","");
        textfieldRed.setText(tfRedString);
        textfieldRed.positionCaret(caretPosition-1);

        caretPosition = textfieldGreen.getCaretPosition();
        tfGreenString = tfGreenString.replaceAll("[^0-9]","");
        textfieldGreen.setText(tfGreenString);
        textfieldGreen.positionCaret(caretPosition-1);

        caretPosition = textfieldBlue.getCaretPosition();
        tfBlueString = tfBlueString.replaceAll("[^0-9]","");
        textfieldBlue.setText(tfBlueString);
        textfieldBlue.positionCaret(caretPosition-1);
    }
    private void removeNull() {
        if (tfRedString.startsWith("0") && !tfRedString.equals("0")) {
            tfRedString = tfRedString.substring(1);
        }
        if (tfGreenString.startsWith("0") && !tfGreenString.equals("0")) {
            tfGreenString = tfGreenString.substring(1);
        }
        if (tfBlueString.startsWith("0") && !tfBlueString.equals("0")) {
            tfBlueString = tfBlueString.substring(1);
        }
    }
    private void preventEmptyTextfield() {
        if (tfRedString.isEmpty()) { tfRedString = "0"; }
        if (tfGreenString.isEmpty()) { tfGreenString = "0"; }
        if (tfBlueString.isEmpty()) { tfBlueString = "0"; }
    }


    //keine Komponente ist mehr ausgewählt (kein schwarzer Rahmen mehr)
    private void unselectAllLEDitems() {
        btnRed.setStyle("-fx-border-width: 0; -fx-background-color: red;");
        btnBlue.setStyle("-fx-border-width: 0; -fx-background-color: blue;");
        btnGreen.setStyle("-fx-border-width: 0; -fx-background-color: green;");
        btnYellow.setStyle("-fx-border-width: 0; -fx-background-color: yellow;");
        btnCyan.setStyle("-fx-border-width: 0; -fx-background-color: cyan;");
        btnMagenta.setStyle("-fx-border-width: 0; -fx-background-color: magenta;");
        btnWhite.setStyle("-fx-border-width: 0; -fx-background-color: #e4dcdc;");
        btnOrange.setStyle("-fx-border-width: 0; -fx-background-color: #fe7200;");
        textfieldRed.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
        textfieldGreen.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
        textfieldBlue.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0; -fx-border-radius: 0;");
        buttonRGBpreview.setStyle("-fx-border-width: 0; -fx-background-color: " + hexRGBColor + ";");
    }

    //schwarzer Rahmen um ausgewählte Komponente
    public void buttonFaceLedsRed() {
        unselectAllLEDitems();
        btnRed.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: red;");
        selectedLedItem = "red";
    }

    public void buttonFaceLedsBlue() {
        unselectAllLEDitems();
        btnBlue.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: blue;");
        selectedLedItem = "blue";
    }

    public void buttonFaceLedsGreen() {
        unselectAllLEDitems();
        btnGreen.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: green;");
        selectedLedItem = "green";
    }

    public void buttonFaceLedsYellow() {
        unselectAllLEDitems();
        btnYellow.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: yellow;");
        selectedLedItem = "yellow";
    }

    public void buttonFaceLedsCyan() {
        unselectAllLEDitems();
        btnCyan.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: cyan;");
        selectedLedItem = "cyan";
    }

    public void buttonFaceLedsMagenta() {
        unselectAllLEDitems();
        btnMagenta.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: magenta;");
        selectedLedItem = "magenta";
    }

    public void buttonFaceLedsWhite() {
        unselectAllLEDitems();
        btnWhite.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: #e4dcdc;");
        selectedLedItem = "white";
    }

    public void buttonFaceLedsOrange() {
        unselectAllLEDitems();
        btnOrange.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: #fe7200;");
        selectedLedItem = "orange";
    }

    //alle Komponenten werden deaktivieren
    private void disableOtherItems(boolean value) {
        textfieldRed.setDisable(value);
        textfieldGreen.setDisable(value);
        textfieldBlue.setDisable(value);
        buttonRGBpreview.setDisable(value);
        btnLeftEye.setDisable(value);
        btnRightEye.setDisable(value);
        btnBothEyes.setDisable(value);
        gridPaneColorButtons.setDisable(value);
        gridPaneLedsOff.setDisable(value);
        gridPaneLedsOn.setDisable(value);
        gridPaneApplyColor.setDisable(value);
    }

    //reagiert auf Backspace
    public void textfieldKeyReleased(KeyEvent keyEvent) {
        tfRedString = textfieldRed.getText();
        tfGreenString = textfieldGreen.getText();
        tfBlueString = textfieldBlue.getText();

        if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
            //leeres Textfeld mit Wert 0 belegen
            preventEmptyTextfield();

            //prüfen ob eingegebene Zahl >255
            checkValueHigher255();
        }
    }

    //aktuelle RGB anzeigen
    private void RGBcolorPreview() {
        selectedLedItem = "rgbColor";
        unselectAllLEDitems();
        labelAllowedValue.setVisible(false);
        disableOtherItems(false);         //enable all other items
        textfieldRed.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5;");
        textfieldGreen.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5;");
        textfieldBlue.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5;");

        try {
            valueRedInteger = Integer.parseInt(tfRedString);
            valueGreenInteger = Integer.parseInt(tfGreenString);
            valueBlueInteger = Integer.parseInt(tfBlueString);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " // something went wrong while converting values of textfields.");
        }

        hexRGBColor = String.format("#%02X%02X%02X", valueRedInteger, valueGreenInteger, valueBlueInteger);
        buttonRGBpreview.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: " + hexRGBColor + ";");
    }
}