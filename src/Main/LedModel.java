package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALLeds;

public class LedModel {
    private Main mainClass;
    private Session session;

    public LedModel(Main mainClass) {
        this.mainClass = mainClass;
    }
    public void setSession(Session session) {
        this.session = session;
    }

    public void ledsOff() {
        try {
            ALLeds leds = new ALLeds(session);
            String name = "FaceLeds";
            leds.off(name);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void colorFaceLeds(String ledsName, String color, float duration) {
        try {
            ALLeds leds = new ALLeds(session);
            leds.fadeRGB(ledsName, color, duration);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setledsRGBcolor(float red, float green, float blue) {
        //String name = "FaceLeds";
        try {
            ALLeds ledsRGB = new ALLeds(session);
            ledsRGB.fadeRGB("FaceLeds", red, green, blue, 1F);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}