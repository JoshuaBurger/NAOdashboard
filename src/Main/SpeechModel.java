package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

public class SpeechModel {

    private Main mainClass;
    private Session session;

    public SpeechModel(Main mainClass) {
        this.mainClass = mainClass;
    }

    public void setSession(Session session) {
        this.session = session;
    }


}
