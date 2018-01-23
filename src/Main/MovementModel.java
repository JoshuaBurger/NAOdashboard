package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;

public class MovementModel {

    private Main mainClass;
    private Session session;

    private double currentHeadYawRad;
    private double currentHeadPitchRad;

    public MovementModel(Main mainClass) {
        this.mainClass = mainClass;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void moveHead(String direction) {
        ALMotion motion;
        String jointName = "";
        double radians[];
        Double newRadiant = 0.0;
        // Motion-Proxy erstellen (schlaegt fehl bei fehlender Verbindung)
        try {
            motion = new ALMotion(session);
        } catch (Exception e){
            System.out.println("Connection lost.");
            try {
                mainClass.startConnectMenu();
            } catch(Exception e1) {
                System.out.println("Connection menu cannot be opened.");
            }
            return;
        }

        try {
            // Kopf-Stiffness setzen, um zu steuern
            motion.setStiffnesses("Head", 1.0);
            //currentHeadYawRad = Math.toRadians(motion.getAngles("HeadYaw", false));
            // Neuen Radiant anhand des momentanen Radiants setzen.
            // Wenn dieser außerhalb des zul. Bereichs liegt, auf max/min setzen.
            // Bereich HeadYaw: 119,5 bis -119,5 bzw 2,0875 bis -2,0875 (minus: rechts)
            // Bereich HeadPitch: -38,5 bis 29,5 bzw. -0.6720 bis 0.5149 (minus: hoch)
            switch(direction) {
                case "up":
                    jointName = "HeadPitch";
                    newRadiant = currentHeadPitchRad -= Math.toRadians(13.6);
                    if ( currentHeadPitchRad < -0.6720 ) {
                        currentHeadPitchRad = -0.6720;
                    }
                    break;
                case "down":
                    jointName = "HeadPitch";
                    newRadiant = currentHeadPitchRad += Math.toRadians(13.6);
                    if ( currentHeadPitchRad > 0.51499 ) {
                        currentHeadPitchRad = 0.51499;
                    }
                    break;
                case "left":
                    jointName = "HeadYaw";
                    newRadiant = currentHeadYawRad += Math.toRadians(23.9);
                    if ( currentHeadYawRad > 2.0875 ) {
                        currentHeadYawRad = 2.0875;
                    }
                    break;
                case "right":
                    jointName = "HeadYaw";
                    newRadiant = currentHeadYawRad -= Math.toRadians(23.9);
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
            System.out.println("Problem while moving head:");
            e.printStackTrace();
        }
    }
}
