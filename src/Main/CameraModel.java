package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALVideoDevice;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CameraModel {

    private ImageView imgTarget;
    private Button btnCameraOn;
    private Button btnCameraOff;
    private Button btnPhoto;
    private Label lblCameraLoading;

    private MainMenuController mainController;
    private Session session;
    private Image disabledImg;
    private Image currentImg;

    private ALVideoDevice camera;
    private String viewerId;
    private static final int resWidth = 320;
    private static final int resHeight = 240;

    private Timer timer;
    private CameraTriggerTask cameraTask;
    private CameraHandler cameraHandler;
    private boolean cameraTaskAborted;
    private boolean cameraError;
    private boolean cameraErrorFixed;


    public CameraModel(MainMenuController main) {
        this.mainController = main;
        // JavaFX Komponenten holen
        this.imgTarget = main.imgCamera;
        this.btnCameraOff = main.btnCameraOff;
        this.btnCameraOn = main.btnCameraOn;
        this.btnPhoto = main.btnPhoto;
        this.lblCameraLoading = main.lblCameraLoading;
        // Timer initialisieren
        timer = new Timer();
        cameraHandler = new CameraHandler();
        cameraTaskAborted = false;
        // Bild für die deaktivierte Kamera vorladen
        try {
            disabledImg = new Image(getClass().getResource("images/camera_disabled.png").toString());
        } catch(Exception e) {
            System.out.println("Error loading disabled camera image:" + e.getMessage());
        }
    }

    public void setSession(Session session) {
        this.session = session;
        // Buttons in Ausgangszustand versetzen
        btnCameraOff.setDisable(true);
        btnCameraOn.setDisable(false);
        btnPhoto.setDisable(true);
    }

    public void enableCamera() {
        try {
            // Kamera-Proxy erstellen
            camera = new ALVideoDevice(session);
            // Als Viewer registrieren, mit folgenden Parametern:
            // Beliebige id, Aufloesung(kQVGA=320x240), Kameraindex(obere), Farbraum(RGB), minimale FPS
            viewerId = camera.subscribeCamera("camera",0,1, 11, 15);
            // Alle 33ms Bild von Kamera laden (Optimalfall ca. 30FPS, aber eher weniger...)
            cameraTaskAborted = false;
            cameraTask = new CameraTriggerTask();
            timer.schedule(cameraTask, 0, 33);
            // Buttons umschalten
            btnCameraOn.setDisable(true);
            btnCameraOff.setDisable(false);
            btnPhoto.setDisable(false);

        } catch(Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println("Camera Problem:" + e.getMessage());
            }
        }
    }

    public void disableCamera() {
        try {
            // Als Viewer abmelden
            camera.unsubscribe(viewerId);
        } catch(Exception e) {
            // Wird unten mitbehandelt
        }
        // Timer-Task abbrechen und evtl. anstehende Ausführung durch Flag verhindern
        if ( cameraTask != null ) {
            cameraTaskAborted = true;
            cameraTask.cancel();
        }
        // Imageview, Buttons und Fehler wieder auf Ausgangszustand schalten
        imgTarget.setImage(disabledImg);
        btnCameraOff.setDisable(true);
        btnCameraOn.setDisable(false);
        btnPhoto.setDisable(true);
        lblCameraLoading.setVisible(false);
        cameraError = false;
        cameraErrorFixed = false;

        if ( (session == null) || (session.isConnected() == false) ) {
            mainController.handleConnectionClosed(true);
        }
    }

    public void takePhoto() {
        // Aktuell dargestelltes Kamerabild in PNG-Datei abspeichern
        try {
            // Aktuell dargestelltes Bild holen
            Image img = imgTarget.getImage();
            // Timestamp erstellen, um Bild einzigartig zu benennen
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HHmmssms");
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String timestamp = sdf.format(ts);
            // Datei erstellen
            File file = new File("nao_dashboard/photos/naoCam-" + timestamp + ".png");
            file.getParentFile().mkdirs();
            file.createNewFile();
            // Bild konvertieren und in Datei schreiben
            BufferedImage buffImg = SwingFXUtils.fromFXImage(img, null);
            ImageIO.write( buffImg, "png", file);

        } catch(Exception e) {
            System.out.println("Error taking Photo: " + e.getMessage());
        }
    }

    class CameraTriggerTask extends TimerTask {

        @Override
        public void run(){
            // Diese Methode wird ca. alle 33ms aufgerufen um optimalerweise 30FPS zu erzeugen (unrealistisch)
            if ( cameraTaskAborted == false ) {
                try {
                    // Alle Bildinfos des aktuellen Frames vom NAO abbholen
                    ArrayList<Object> naoImage = (ArrayList<Object>) camera.getImageRemote(viewerId);
                    // RGB-Buffer des Bildes extrahieren (laut unserer Registration, genauer ist es BGR, was aber zu den RGB-Raeumen gehoert)
                    ByteBuffer buffer = (ByteBuffer) naoImage.get(6);
                    byte[] rawData = buffer.array();

                    // Passendes BufferedImage erzeugen: 320x240, BGR, 1 Byte pro Farbe (also nicht 'pre-multiplied')
                    // Aufgrund unserer Aufloesung haben wir 320x240x3 Bytes (3 Byte pro Pixel fuer Blau-, Gruen- und Rot-Wert)
                    BufferedImage buffImg = new BufferedImage(resWidth, resHeight, BufferedImage.TYPE_3BYTE_BGR);
                    // Raster mit Bild-Rohdaten und Color-Model erzeugen und Buff.Image damit befuellen
                    Raster raster = Raster.createRaster(buffImg.getSampleModel(), new DataBufferByte(rawData, rawData.length), new Point());
                    buffImg.setData(raster);

                    // BufferedImage in JavaFX-Image konvertieren und in ImageView darstellen
                    currentImg = SwingFXUtils.toFXImage(buffImg, null);
                    if ( cameraError == true ) {
                        // Wenn Fehler war und es jetzt wieder ging -> zuruecksetzen
                        cameraErrorFixed = true;
                    }
                    // Bild laden via runLater, da GUI nicht im Timer-Thread verändert werden darf
                    Platform.runLater(cameraHandler);
                    // Bild auf NAO-Seite freigeben
                    camera.releaseImage(viewerId);

                } catch (Exception e) {
                    if ((session == null) || (session.isConnected() == false)) {
                        // Hier muessen wir komplett abbrechen
                        disableCamera();
                    }
                    else if ( cameraError == false )
                    {
                        // Bei Fehler Hinweis anzeigen und Foto disablen
                        cameraError = true;
                        Platform.runLater(cameraHandler);
                    }
                }
            }
        }
    }

    class CameraHandler implements Runnable {

        @Override
        public void run() {
            if ( cameraTaskAborted == false ) {

                if ( cameraErrorFixed ) {
                    // Kamerafehler behoben
                    cameraError = false;
                    lblCameraLoading.setVisible(false);
                    btnPhoto.setDisable(false);
                }

                if ( cameraError ) {
                    // Fehler mit Kamera, Hinweis setzen und Photo disablen
                    lblCameraLoading.setVisible(true);
                    btnPhoto.setDisable(true);
                }
                else {
                    // Aktuelles Bild in View setzen
                    imgTarget.setImage(currentImg);
                }
            }
        }
    }

    // Um Kamera zu testen ohne NAO kann diese Methode benutzt werden, um ein Testbild zu laden
    // Einfach im CameraHandler an der Stelle der run()-Methode einfügen, wo das byte-Array geladen wird.
    // Das Laden des NAO-Bildes muss dann auskommentiert werden, da sonst bei Fehler der catch zuschlägt.
    private byte[] loadTestImg() throws Exception {
        byte[] rawData = new byte[230400];
        int index = 0;

        BufferedReader r = new BufferedReader(new FileReader(new File(getClass().getResource("camera_testimage.txt").toString())));
        while( r.ready() && index < 230400) {
            String s = r.readLine();
            rawData[index] = Byte.valueOf(s);
            index++;
        }

        return rawData;
    }
}
