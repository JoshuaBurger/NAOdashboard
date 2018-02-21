package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import java.util.List;

public class MovementModel {

    private MainMenuController mainController;
    private Session session = null;
    private ALMotion motion = null;

    private boolean isWalking = false;

    public MovementModel(MainMenuController main) {
        this.mainController = main;
    }

    public void setSession(Session session) {
        this.session = session;
        if ( session == null ) {
            motion = null;
        }
        else {
            // Motion-Proxy erstellen
            try {
                motion = new ALMotion(session);
            } catch (Exception e){
                System.out.println("Error creating motion proxy on session");
            }
        }
    }

    public void moveHead(String direction) {
        List<Float> radians;
        String jointName = "";
        double currentHeadYawRad = 0.0;
        double currentHeadPitchRad = 0.0;
        double newRadiant = 0.0;

        try {
            // Kopf-Stiffness setzen, um zu steuern
            motion.setStiffnesses("Head", 1.0);

            // Aktuelle Radianten des NAO holen
            radians = motion.getAngles("Head", true);
            if ( radians.size() >= 2 ) {
                currentHeadYawRad = radians.get(0);
                currentHeadPitchRad = radians.get(1);
            }
            // Neuen Radiant anhand des momentanen Radiants setzen.
            // Wenn der Neue außerhalb des zul. Bereichs liegt, auf max/min setzen.
            // Bereich HeadYaw: 119,5 bis -119,5 bzw 2,0875 bis -2,0875 (minus: rechts)
            // Bereich HeadPitch: -38,5 bis 29,5 bzw. -0.6720 bis 0.5149 (minus: hoch)
            switch(direction) {
                case "up":
                    jointName = "HeadPitch";
                    newRadiant = currentHeadPitchRad - Math.toRadians(13.6);
                    if ( currentHeadPitchRad < -0.6720 ) {
                        currentHeadPitchRad = -0.6720;
                    }
                    break;
                case "down":
                    jointName = "HeadPitch";
                    newRadiant = currentHeadPitchRad + Math.toRadians(13.6);
                    if ( currentHeadPitchRad > 0.51499 ) {
                        currentHeadPitchRad = 0.51499;
                    }
                    break;
                case "left":
                    jointName = "HeadYaw";
                    newRadiant = currentHeadYawRad + Math.toRadians(23.9);
                    if ( currentHeadYawRad > 2.0875 ) {
                        currentHeadYawRad = 2.0875;
                    }
                    break;
                case "right":
                    jointName = "HeadYaw";
                    newRadiant = currentHeadYawRad - Math.toRadians(23.9);
                    if ( currentHeadYawRad < -2.0875 ) {
                        currentHeadYawRad = -2.0875;
                    }
                    break;
                default:
                    throw new Exception("Unknown direction: " + direction);
            }

            // Kopfbewegung durchfuehren (Achse, Radian, Zeit, Absolutheit)
            motion.angleInterpolation(jointName,newRadiant, 1.0, true);

            // Kopf-Stiffness zuruecksetzen
            motion.setStiffnesses("Head", 0.0);
        } catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println("Error while moving head: " + e.getMessage());
            }
        }
    }

    public void goToPosture(String postureName) {
        try {
            ALRobotPosture posture = new ALRobotPosture(session);
            posture.goToPosture(postureName, 6F);
        }
        catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println(e.getMessage());
            }
        }
    }

    public void goToRest(boolean shallRest) {
        try {
            if ( shallRest ) {
                motion.rest();
            }
            else {
                motion.wakeUp();
            }
        }
        catch (Exception e) {
            if ( (session == null) || (session.isConnected() == false) ) {
                mainController.handleConnectionClosed(true);
            }
            else {
                System.out.println(e.getMessage());
            }
        }
    }

    public void move(float xAxis, float yAxis, float zAxis) {
        // Verhindern, dass move vielfach aufgerufen wird, da NAO sonst im Laufen hängenbleibt
        if ( isWalking == false ) {
            isWalking = true;
            try {
                goToPosture("Stand");
                motion.move(xAxis, yAxis, zAxis);
            } catch (Exception e) {
                if ((session == null) || (session.isConnected() == false)) {
                    mainController.handleConnectionClosed(true);
                } else {
                    System.out.println("Error while moving: " + e.getMessage());
                }
            }
        }
    }
    public void stopWalking() {
        isWalking = false;
        try {
            motion.stopMove();
            goToPosture("Stand");
        }
        catch (Exception e) {
            System.out.println(e.getMessage() + ", stop walking failed");
        }
    }
}
