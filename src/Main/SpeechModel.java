package Main;

import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.ALMemoryHelper;
import com.aldebaran.qi.helper.proxies.ALSpeechRecognition;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALBodyTemperature;
import com.aldebaran.qi.helper.proxies.ALMemory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;


public class SpeechModel {
    private MainMenuController mainController;
    private ALSpeechRecognition speechRecognition;
    protected Button btnSpeechRecognition;
    private Main mainClass;
    private Session session;
    public long eventId;
    public int dialogueCounter = 0;
    public int squatCounter = 0;
    public boolean squatEnabled = true;
    public boolean isSubscribed = false;
    public boolean understand = false;

    public SpeechModel(MainMenuController mainController){
        this.mainController = mainController;
        this.btnSpeechRecognition = mainController.btnSpeechRecognition;
    }

    public SpeechModel(Main mainClass) {
        this.mainClass = mainClass;
    }

    public void setSession(Session session) {
        this.session = session;
    }



    public void registerSpeechEvents(ALMemory memory) {
        try {
            speechRecognition = new ALSpeechRecognition(session);
            speechRecognition.setVisualExpressionMode(0);
            speechRecognition.setVisualExpression(false);
            speechRecognition.setLanguage(mainController.language);
            ArrayList<String> vocabulary = new ArrayList<String>();
            if (mainController.language.equals("German")){
                if (dialogueCounter == 0 && squatCounter == 0){
                    vocabulary.add("Hallo");
                    vocabulary.add("Kniebeuge");
                    vocabulary.add("Sitz");
                    //vocabulary.add("Test");

                }

                if (dialogueCounter == 1 && squatCounter == 0){
                    vocabulary.add("Gut");
                    vocabulary.add("Schlecht");
                    vocabulary.add("Naja");
                }

                if (dialogueCounter == 2 && squatCounter == 0){
                    vocabulary.add("Ja");
                    vocabulary.add("Nein");
                }

                if (squatCounter > 0){
                    vocabulary.add("Stopp");
                }

            }
            if (mainController.language.equals("English")){
                vocabulary.add("Hello");
                vocabulary.add("Squat");
                vocabulary.add("Sit");
                vocabulary.add("Test");
            }

            speechRecognition.setVocabulary(vocabulary,false);
            eventId = memory.subscribeToEvent("WordRecognized",
                    new EventCallback<Object>(){
                @Override
                public void onEvent(Object obj) {
                    reactToSpeech(obj);

                }
            });
            speechRecognition.subscribe("Test_ASR");
            System.out.println("Subscribed");
            System.out.println(vocabulary);
        } catch(Exception e) {
            System.out.println("Couldn't register SpeechRecognition events");
            System.out.println(e);
        }
    }



    public void unregisterSpeechEvents(ALMemory memory){
        try {
            memory.unsubscribeToEvent(eventId);
            speechRecognition.unsubscribe("Test_ASR");
            System.out.println("Unsubscribed");

        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public void reactToSpeech(Object obj) {
        ArrayList<Object> recognition = (ArrayList) obj;
        String word = (String) recognition.get(0);
        System.out.println(word);
        float confidence = (float) recognition.get(1);
        System.out.println(confidence);

        // Deutsche Befehle
        if (word.equals("Sitz") && confidence > 0.5) {
            unregisterSpeechEvents(mainController.memory);
            mainController.standZero();
            mainController.sitRelax();
        }

        if (word.equals("Kniebeuge") && confidence > 0.5) {
            try {
                while (squatEnabled && squatCounter < 15) {
                    unregisterSpeechEvents(mainController.memory);
                    mainController.standZero();
                    mainController.crouch();
                    mainController.standZero();
                    squatCounter++;
                    registerSpeechEvents(mainController.memory);
                    Thread.sleep(3000);

                }
            } catch (Exception e) {
                System.out.println(e);
            }

            if (squatCounter == 15) {
                unregisterSpeechEvents(mainController.memory);
                squatCounter = 0;
                mainController.saySomething("Mehr schaffe ich nicht");
                System.out.println("Mehr schaffe ich nicht");
            }

            //fals was anderes als stopp gesagt wird
            if (squatCounter != 0 && squatEnabled == false) {
                squatCounter = 0;
            }

        }

        if (word.equals("Stopp") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            squatEnabled = false;
            if (squatCounter == 1) {

                mainController.saySomething("ich habe eine Kniebeuge gemacht");
                System.out.println("ich habe eine Kniebeuge gemacht");
            } else {
                mainController.saySomething("ich habe " + squatCounter + " Kniebeugen gemacht");
                System.out.println("ich habe " + squatCounter + " Kniebeugen gemacht");
            }
            squatCounter = 0;
        }


        //Kurzer Dialog mit verschiedenen Pfaden wird gestartet.
        if (word.equals("Hallo") && confidence > 0.5) {
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Hallo");
            mainController.saySomething("Hi, wie geht es dir?");
            dialogueCounter = 1;
            registerSpeechEvents(mainController.memory);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println(e);
            }


        }

        //Dialog Level 1
        if (word.equals("Gut") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Gut");
            mainController.saySomething("Oh, das freut mich");
            System.out.println("Oh, das freut mich");
        }

        if (word.equals("Schlecht") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Schlecht");
            mainController.saySomething("Oh nein, hoffentlich geht es dir bald besser");
            System.out.println("Oh nein, hoffentlich geht es dir bald besser");
        }

        if (word.equals("Naja") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Naja");
            mainController.saySomething("Oh, soll dir einen Witz erzählen?");
            System.out.println("Oh, soll dir einen Witz erzählen?");
            dialogueCounter = 2;
            registerSpeechEvents(mainController.memory);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //Dialog Level 2
        if (word.equals("Nein") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Nein");
            mainController.saySomething("Na gut, dann nicht");
            System.out.println("Na gut, dann nicht");
            dialogueCounter = 0;
        }
        if (word.equals("Ja") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Ja");
            mainController.saySomething("Die Leute sagen immer Roboter haben keine Gefühle, dass macht mich immer so traurig");
            System.out.println("Die Leute sagen immer Roboter haben keine Gefühle, dass macht mich immer so traurig");
            dialogueCounter = 0;
        }


        //Englische Befehle
        if (word.equals("Squat") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            mainController.standZero();
            mainController.crouch();
            mainController.standZero();
        }

        if (word.equals("Sit") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            mainController.standZero();
            mainController.sitRelax();
        }

        if (word.equals("Hello") && confidence > 0.5) {
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            System.out.println("Hello");
            mainController.saySomething("Hi, nice to meet you");

        }

        //Wenn das verstandene Wort nicht aus dem Vokabular ist.
        else if (understand == false){
            understand = true;
            unregisterSpeechEvents(mainController.memory);
            dialogueCounter = 0;
            squatEnabled = false;
            if (mainController.language.equals("German")) {
                mainController.saySomething("Das habe ich leider nicht verstanden");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btnSpeechRecognition.setText("Listen");
                    }
                });
            }

            if (mainController.language.equals("English")) {
                mainController.saySomething("Excuse me, I did not understand that");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btnSpeechRecognition.setText("Listen");
                    }
                });


            }
        }

            //Falls man sich nicht in einem Dialog befindet wird der Button nach der Reaktion wieder auf "Listen" gesetzt.
            if (dialogueCounter == 0 && squatCounter == 0) {
                squatEnabled = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        btnSpeechRecognition.setText("Listen");
                    }
                });
            }

        }


    }
