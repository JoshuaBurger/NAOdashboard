package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALLeds;

public class LedModel {
    private Session session;
    private MainMenuController mainController;

    public LedModel(MainMenuController main) {
        this.mainController = main;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void turnledsOff(String name) {
        try {
            ALLeds leds = new ALLeds(session);
            leds.off(name);
        }
        catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                System.out.println("Connection lost");
                mainController.handleConnectionClosed();
            }
            System.out.println(e.getMessage());
        }
    }

    public void colorFaceLeds(String ledsName, String color) {
        try {
            ALLeds leds = new ALLeds(session);
            leds.fadeRGB(ledsName, color, 1F);
        }
        catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                System.out.println("Connection lost");
                mainController.handleConnectionClosed();
            }
            System.out.println(e.getMessage());
        }
    }

    public void setledsRGBcolor(String ledsName, float red, float green, float blue) {
        try {
            System.out.println("connection to nao, value red is: "+red+", value green is: "+green+", value blue is: "+blue);
            ALLeds ledsRGB = new ALLeds(session);
            ledsRGB.fadeRGB(ledsName, red, green, blue, 1F);
        }
        catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                System.out.println("Connection lost");
                mainController.handleConnectionClosed();
            }
            System.out.println(e.getMessage());
        }
    }
}