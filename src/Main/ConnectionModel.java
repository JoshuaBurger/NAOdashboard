package Main;

import com.aldebaran.qi.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ConnectionModel {

    private Label lblConnectionInfo;
    private Label lblConnectionState;
    private Button btnConnect;
    private Button btnDisconnect;
    private TextField txtIP;
    private TextField txtPort;
    private ComboBox cbxConnHistory;

    private MainMenuController mainMenuController;
    private Session session;
    private ObservableList<String> connHistory;
    private ConnectionCheck connCheck;

    public ConnectionModel(MainMenuController controller)
    {
        this.mainMenuController = controller;
        // JavaFX-Komponenten von MainMenuController holen
        lblConnectionInfo   = mainMenuController.lblConnectionInfo;
        lblConnectionState  = mainMenuController.lblConnectionState;
        btnConnect          = mainMenuController.btnConnect;
        btnDisconnect       = mainMenuController.btnDisconnect;
        txtIP               = mainMenuController.txtConnectionIP;
        txtPort             = mainMenuController.txtConnectionPort;
        cbxConnHistory      = mainMenuController.cbxConnectionHistory;
        // Gespeicherte Verbindungen (History) laden
        connHistory = FXCollections.observableArrayList();
        loadFavoritesFromFile();
        // Verbindung alle 10 Sekunden ueberpruefen
        Timer timer = new Timer();
        connCheck = new ConnectionCheck(controller);
        timer.schedule( connCheck, 0, 10000 );

        // Session erzeugen, damit die naoqi-Bibliothek schonmal geladen wird (Performance)
        session = new Session();
        session = null;
    }

    public void connect() {
        String naoUrl = txtIP.getText() + ":" + txtPort.getText();

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
            session.connect("tcp://" + naoUrl).get(5, TimeUnit.SECONDS);
        }
        catch(Exception e){
            // Fehler wird unten mitbehandelt
        }

        if( session.isConnected() ){
            // Erfolgreich verbunden
            setInfoText("Connection established.", Color.GREEN);
            setConnectionState(naoUrl);
            // Session an alle Nutzer propagieren
            mainMenuController.setSession(session);
            connCheck.setSession(session);
            mainMenuController.saySomething("Connected.");
            mainMenuController.enableTabs();
            // Verbindung in Datei merken
            addConnectionToHistory(naoUrl);
        }
        else{
            setInfoText("Couldn't connect to NAO.", Color.RED);
            setConnectionState(null);
            // session zurücksetzen
            session.close();
            session = null;
            // Disconnect an alle Nutzer propagieren
            mainMenuController.setSession(session);
            connCheck.setSession(session);
        }
    }

    public void disconnect(){
        if ( (session != null) && (session.isConnected()) ){
            mainMenuController.saySomething("Disconnected.");
            mainMenuController.unregisterEvents();
            session.close();
        }
        // Main Controller sperrt alle Tabs und ruft setDisconnected() auf
        // So ist das aktive Disconnecten stringent zum passiven Verbindungsverlust
        mainMenuController.handleConnectionClosed(false);
    }

    public void setDisconnected(boolean lost) {
        session = null;
        mainMenuController.setSession(session);
        connCheck.setSession(session);
        if ( lost ) {
            System.out.println("Connection lost.");
            setInfoText("Connection lost.", Color.RED);
        }
        else {
            System.out.println("Connection closed.");
            setInfoText("Connection closed.", Color.BLACK);
        }
        setConnectionState(null);
    }

    private void setInfoText(String text, Color color){
        lblConnectionInfo.setTextFill(color);
        mainMenuController.displayTextTemporarily(lblConnectionInfo, text, 4000);
        System.out.println(text);
    }

    private void setConnectionState(String url){
        if( url == null ){
            lblConnectionState.setTextFill(Color.RED);
            lblConnectionState.setText("Disconnected.");
            btnConnect.setDisable(false);
            btnDisconnect.setDisable(true);
        }
        else{
            lblConnectionState.setTextFill(Color.GREEN);
            lblConnectionState.setText("Connected to " + url);
            btnConnect.setDisable(true);
            btnDisconnect.setDisable(false);
        }
    }

    private void addConnectionToHistory(String url) {
        // Nur wenn aktuelle Verbindung nicht schon an erster Stelle in History
        if ( (connHistory.isEmpty()) || (connHistory.get(0).equals(url) == false) ) {
            File file = new File("nao_dashboard/connHistory.dat");
            try {
                // Pfad + Datei erstellen falls nicht schon vorhanden
                file.getParentFile().mkdirs();
                file.createNewFile();
                // Datei zum Schreiben oeffnen (wirft Exception bei Fehler)
                PrintWriter p = new PrintWriter(file);

                if ( connHistory.contains(url) ) {
                    // Wenn Verbindung schon in History(nicht an 1. Stelle), erstmal loeschen
                    for (int i = 0; i < connHistory.size(); i++) {
                        if ( connHistory.get(i).equals(url) ) {
                            connHistory.remove(i);
                            break;
                        }
                    }
                }
                // Verbindung zu Liste hinzufuegen (bzw. an erste Stelle setzen) und auswaehlen
                connHistory.add(0,url);
                cbxConnHistory.getSelectionModel().selectFirst();
                // Nach 5 Verbindungen abschneiden
                for (int i = connHistory.size(); i > 5; i--) {
                    connHistory.remove(i-1);
                }
                // Favoriten in Datei merken
                for ( String conn : connHistory ) {
                    p.println(conn);
                }
                p.flush();
                p.close();
            } catch(Exception e) {
                System.out.println("Error saving connection history: " + e.toString());
            }
        }
    }

    private void loadFavoritesFromFile() {
        // Gespeicherte Verbindungen laden (1x pro Laufzeit)
        File file = new File("nao_dashboard/connHistory.dat");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Datei Zeile fuer Zeile einlesen (Schema: "ip:port")
            while ( reader.ready() ) {
                String line = reader.readLine();
                if ( (line != null) && (line != "") ) {
                    connHistory.add(line);
                }
            }
            // Erste Verbindung (zuletzt aktive) in Auswahl setzen
            String firstUrl = connHistory.get(0);
            if ( firstUrl != null ) {
                cbxConnHistory.getSelectionModel().selectFirst();
                applyHistoryConnection(firstUrl);
            }

        } catch(Exception e) {
            System.out.println("Loading connection history failed: " + e.toString());
        }
        // Liste mit Combobox verknuepfen (Auch wenn sie noch leer sein sollte)
        cbxConnHistory.setItems(connHistory);
    }

    public void applySelectedHistoryConn() {
        // Ausgewaehlte URL aus Combobox holen
        String url = cbxConnHistory.getSelectionModel().getSelectedItem().toString();
        applyHistoryConnection(url);
    }

    public void applyHistoryConnection(String url) {
        if ( (url != null) && (url.length() > 0) ) {
            // URL in IP und Port splitten und in Textfelder setzen
            int index = url.indexOf(':');
            String ip = url.substring(0,index);
            String port = url.substring(index + 1);

            txtIP.setText(ip);
            txtPort.setText(port);
        }
    }

    class ConnectionCheck extends TimerTask
    {
        private MainMenuController mainController;
        private Session session;
        private boolean disconnHandled;

        public ConnectionCheck(MainMenuController main) {
            this.mainController = main;
            session = null;
            disconnHandled = false;
        }

        public void setSession(Session s) {
            session = s;
            disconnHandled = false;
        }

        @Override public void run()
        {
            // Erstmal pruefen, ob nicht bereits gehandelt
            if ( disconnHandled == false) {
                if ( (session != null) && (session.isConnected() == false) ) {
                    // Bei Verbindungsverlust MainMenuController informieren
                    // Via runLater, da GUI nicht im Timer-Thread verändert werden darf
                    Platform.runLater( new Runnable() {
                        @Override
                        public void run() {
                            mainController.handleConnectionClosed(true);
                            disconnHandled = true;
                        }
                    }
                    );
                }
            }
        }
    }

}
