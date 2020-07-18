package com.example.fmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = "MainActivity";

    //user credentials, get from LoginActivity
    String userEmail;
    String userPassword;

    //for keeping user calls or commands
    String userCommand = "";

    TextToSpeech tts;

    Intent speechRecognitionIntent;
    SpeechRecognizer speechRecognizer;

    // 1-> If yes go to sendMessageAgain function, 2-> Read an email, 3-> Send an email, 4-> Read mailbox again
    int listenFormat = 0;

    //1-> User pick a mail number for listen it, 2-> Check the number and read it to user, 3-> Ask for listen again
    int listenMessageFormat = 0;

    //1-> Get an e-mail address, 2-> Get the subject of the mail, 3-> Get the content of the mail
    int sendMessageFormat = 0;

    //Decide user want to change send entries or not
    int emailChange = 0;
    int subjectChange = 0;
    int contentChange = 0;

    javax.mail.Message[] messages;
    int messageCount = 0;

    //mail data for reading
    String readMailSender;
    String readMailDate;
    String readMailSubject;
    String readMailContent;

    //display mail data on screen
    TextView mailFrom;
    TextView mailDate;
    TextView mailSubject;
    TextView mailContent;

    //mail data for sending
    String sendMailReceiver;
    String sendMailSubject;
    String sendMailContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            userEmail = bundle.getString("email");
            userPassword = bundle.getString("password");
        }

        Log.d(LOG_TAG, "email: " + userEmail + " - password: " + userPassword);

        //setup TextToSpeech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(LOG_TAG, "Error!!! This Language is not supported");
                    }

                } else
                    Log.e(LOG_TAG, "Initialization Failed!");

                speechRecognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                speechRecognitionIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getBaseContext().getPackageName());

                //setup Speech Recognizer
                CustomRecognitionListener listener = new CustomRecognitionListener();
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getBaseContext());
                speechRecognizer.setRecognitionListener(listener);

                Speak("Reading your mailbox, please wait");
                boolean readSuccessfully = false;
                try {
                    readSuccessfully = new ReadMails().execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(readSuccessfully){
                    if(messageCount > 1)
                        Speak("You have " + messageCount + " mails.");
                    else if(messageCount == 1)
                        Speak("You have " + messageCount + " mail.");
                    else
                        Speak("Your mailbox is empty.");

                    if(messageCount > 0) {
                        Speak("Do you want to listen an email");
                        while (tts.isSpeaking()) ;
                        listenFormat = 2;
                        Listen();
                    }else{
                        Speak("Do you want to send an email");
                        while (tts.isSpeaking());
                        listenFormat = 3;
                        Listen();
                    }
                } else{
                    Speak("Failed when reading your mails, Do you want to try again");
                    while (tts.isSpeaking());
                    listenFormat = 1;
                    Listen();
                }

            }
        });

        mailFrom = findViewById(R.id.mailFrom);
        mailDate = findViewById(R.id.mailDate);
        mailSubject = findViewById(R.id.mailSubject);
        mailContent = findViewById(R.id.mailContent);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        speechRecognizer.destroy();
        tts.stop();
        tts.shutdown();
    }


    @Override
    protected void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    private boolean readMessageAgain() {
        try {
            return new ReadMails().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public class ReadMails extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Properties props = new Properties();
            // set this session up to use SSL for IMAP connections
            props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            // don't fallback to normal IMAP connections on failure.
            props.setProperty("mail.imap.socketFactory.fallback", "false");
            // use the simap port for imap/ssl connections.
            props.setProperty("mail.imap.socketFactory.port", "993");

            try {
                Session session = Session.getInstance(props);
                Store store = session.getStore("imap");

                Log.d(LOG_TAG, "email is " + userEmail + " - password is " + userPassword);
                store.connect("imap.gmail.com", userEmail, userPassword);

                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);

                messageCount = inbox.getMessageCount();                    //read mail counts in mailbox
                messages = inbox.getMessages();                            //read all mails in mailbox

                return true;
            } catch (Exception mex) {
                Log.d(LOG_TAG, "Read mail exception, " + mex);
            }

            return false;
        }
    }

    public class readMail extends AsyncTask<javax.mail.Message, Void, Boolean> {
        @Override
        protected Boolean doInBackground(javax.mail.Message... params) {
            javax.mail.Message message = params[0];

            try {
                javax.mail.Address[] in = message.getFrom();

                for (javax.mail.Address address : in) {
                    Log.d(LOG_TAG, "FROM:" + address.toString());
                    readMailSender = address.toString();
                }

                Multipart mp = null;
                String body = null;
                final String content;

                Object object = message.getContent();
                if (object instanceof String)
                    body = (String)object;
                else
                    mp = (Multipart)object;

                if(mp == null){
                    readMailContent = body;
                }else
                    readMailContent = getTextFromMultipart(mp);

                readMailDate = message.getSentDate().toString();
                readMailSubject = message.getSubject();

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mailContent.setText("Content: " + readMailContent);
                        mailDate.setText("Date: " + readMailDate);
                        mailFrom.setText("Sender: " + readMailSender);
                        mailSubject.setText("Subject: " + readMailSubject);
                    }
                });

                Log.d(LOG_TAG, "SENT DATE:" + message.getSentDate());
                Log.d(LOG_TAG, "SUBJECT:" + message.getSubject());
                Log.d(LOG_TAG, "CONTENT:" + readMailContent);

                return true;
            } catch (Exception mex) {
                Log.d(LOG_TAG, "Read mail exception, " + mex);
            }

            return false;
        }
    }

    /**
    *@param mp is content of a mail, this function gets strings inside it.
     */
    private String getTextFromMultipart(Multipart mp) throws javax.mail.MessagingException, IOException {
        String result = "";
        int count = mp.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mp.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                if(result.isEmpty())
                    result = bodyPart.getContent().toString();
                else
                    result = result + "\n" + bodyPart.getContent();
                break; //
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                if(result.isEmpty())
                    result = org.jsoup.Jsoup.parse(html).text();
                else
                    result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMultipart((Multipart) bodyPart.getContent());
            }
        }

        return result;
    }

    private void speakMail(){
        Speak("Message sender is " + readMailSender);
        Speak("Sent date is " + readMailDate);
        Speak("Subject is " + readMailSubject);
        Speak("Content of message is " + readMailContent);
        while (tts.isSpeaking());
    }

    private void sendMail(){
        new Thread(new Runnable() {
            String recipient = "dummymaill2403@gmail.com";

            @Override
            public void run() {
                Log.d(LOG_TAG, "sendMail");
                boolean isSuccessfullySended = false;
                try {
                    Log.d(LOG_TAG, "sendMail, subject: " + sendMailSubject + " - content: " + sendMailContent);
                    GMailSender sender = new GMailSender(userEmail, userPassword);
                    sender.sendMail(sendMailSubject, sendMailContent, userEmail, recipient);
                    isSuccessfullySended = true;
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }

                if(isSuccessfullySended)
                    Speak("Email is successfully sent to ");
                else
                    Speak("There is a problem occurred when try to send an email");

                Speak("Do you want to send another message");
                while (tts.isSpeaking());

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(LOG_TAG, "I am the UI thread");
                        Listen();
                    }
                });
            }
        }).start();
    }

    /**
     *@param speech, TextToSpeech library converts speech string to voice.
     */
    private void Speak(String speech){
        tts.speak(speech, TextToSpeech.QUEUE_ADD, null);
    }

    //start listening
    public void Listen() {
        speechRecognizer.startListening(speechRecognitionIntent);
    }

    public class CustomRecognitionListener implements RecognitionListener {
        private static final String TAG = "RecognitionListener";

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }

        @Override
        public void onError(int error) {
            Log.e(TAG, "error " + error);

            if(error == 6){
                speechRecognizer.cancel();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Listen();
                    }
                }, 1000);
            }

            if(error == 7) {
                Speak("Can not hear you, please try again");
                while (tts.isSpeaking());
                Listen();
            }
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults bundle is: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));

            if(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0)
                userCommand = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);

            Log.d(TAG, "onResults, result is: " + userCommand);
            Log.d(TAG, "listen format is: " + listenFormat);

            if(userCommand.contains("shut down") && userCommand.length() < 20){
                Speak("App is shut down");
                while (tts.isSpeaking());
                finish();
                tts.shutdown();
                speechRecognizer.destroy();
            }

            switch (listenFormat){
                case 1:
                    listenFormat = 0;
                    if(userCommand.contains("yes") ||userCommand.contains("okay")){
                        boolean isOkay = readMessageAgain();
                        if(!isOkay) {
                            Speak("Cannot read mail, app is going to shut down");
                            while (tts.isSpeaking());
                            finish();
                            tts.shutdown();
                            speechRecognizer.destroy();
                        }
                    }
                    break;

                case 2:
                    if((userCommand.contains("yes") ||userCommand.contains("okay"))
                            && listenMessageFormat == 0){
                        listenMessageFormat = 1;
                    }else if(listenMessageFormat == 0){
                        Speak("Do you want to send an email");
                        while(tts.isSpeaking());
                        listenFormat = 3;
                        Listen();
                    }

                    if(listenMessageFormat == 1){
                        Speak("Which mail dou you want to listen, you have " + messageCount + " mails, please say a number");
                        while (tts.isSpeaking());
                        listenMessageFormat = 2;
                        Listen();
                    }else if(listenMessageFormat == 2){
                        userCommand = userCommand.replaceAll("\\D+","");
                        Log.d(LOG_TAG, "userCommand after deleting non digit is " + userCommand);

                        if(userCommand.isEmpty()){
                            Speak("Please say a number for a mail that you want to listen");
                            while (tts.isSpeaking());
                            Listen();
                        }else{
                            int count = Integer.valueOf(userCommand);
                            if (count <= messageCount) {
                                Speak("Reading mail " + count + ", Please wait");
                                boolean isOkay = false;
                                try {
                                    isOkay = new readMail().execute(messages[count-1]).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(isOkay){
                                    speakMail();
                                    Speak("Do you want to listen this mail again");
                                    while (tts.isSpeaking());
                                    listenMessageFormat = 3;
                                    Listen();
                                }else{
                                    Speak("Cannot read mail " + count + " content.");
                                }
                            }else{
                                Speak("You have " + messageCount + "mails, you don't have any mail numbered "
                                        + count);
                                Speak("Please say a number for a mail that you want to listen");
                                while (tts.isSpeaking());
                                Listen();
                            }
                        }
                    }else if(listenMessageFormat == 3){
                        if(userCommand.contains("yes") ||userCommand.contains("okay")){
                            speakMail();
                            Speak("Do you want to listen this mail again");
                            while (tts.isSpeaking());
                            Listen();
                        }else{
                            listenFormat = 2;
                            listenMessageFormat = 0;
                            Speak("Do you want to listen another email");
                            while(tts.isSpeaking());
                            Listen();
                        }
                    }

                    break;

                case 3:
                    if(sendMessageFormat == 1){
                        if(emailChange == 0) {
                            userCommand = userCommand.replace(" ", "");
                            Log.d(LOG_TAG, "user command1 is " + userCommand);

                            userCommand = userCommand.replace("at", "@");
                            Log.d(LOG_TAG, "user command2 is " + userCommand);

                            String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(userCommand);

                            Log.d(LOG_TAG, "matcher is " + matcher.matches());
                            if (!matcher.matches()) {
                                Speak("You said " + userCommand + ". But you need to say a valid email address such as mymail@gmail.com");
                                while (tts.isSpeaking()) ;
                                Listen();
                            } else {
                                sendMailReceiver = userCommand;
                                emailChange = 1;
                                Speak("Receiver email is " + userCommand + ". Do you want to change the mail receiver");
                                while (tts.isSpeaking()) ;
                                Listen();
                            }
                        }else if(emailChange == 1){
                            emailChange = 0;
                            if(userCommand.contains("yes") ||userCommand.contains("okay")){
                                Speak("Please say the message receiver's mail address");
                                while(tts.isSpeaking());
                                Listen();
                            }else{
                                Speak("Now you can say the subject of the message.");
                                while(tts.isSpeaking());
                                sendMessageFormat = 2;
                                Listen();
                            }
                        }
                    }else if(sendMessageFormat == 2){
                        if(subjectChange == 0) {
                            Log.d(LOG_TAG, "Subject of message is " + userCommand);
                            sendMailSubject = userCommand;
                            Speak("Subject of message is " + sendMailSubject + ". Do you want to change the subject");
                            while (tts.isSpeaking()) ;
                            subjectChange = 1;
                            Listen();
                        }else if(subjectChange == 1){
                            subjectChange = 0;
                            if(userCommand.contains("yes") ||userCommand.contains("okay")){
                                Speak("Please say the new subject of the message");
                                while(tts.isSpeaking());
                                Listen();
                            }else{
                                Speak("Now, please say the content of the message");
                                while(tts.isSpeaking());
                                sendMessageFormat = 3;
                                Listen();
                            }
                        }
                    }else if(sendMessageFormat == 3){
                        if(contentChange == 0) {
                            Log.d(LOG_TAG, "Content of message is " + userCommand);
                            sendMailContent = userCommand;
                            Speak("Content of message is " + sendMailContent + ". Do you want to change the content");
                            while (tts.isSpeaking()) ;
                            contentChange = 1;
                            Listen();
                        }else if(contentChange == 1){
                            contentChange = 0;
                            if(userCommand.contains("yes") ||userCommand.contains("okay")){
                                Speak("Please say the new content of the message");
                                while(tts.isSpeaking());
                                Listen();
                            }else{
                                Speak("Your message is sending, please wait");
                                sendMail();
                                sendMessageFormat = 0;
                            }
                        }
                    }else if((userCommand.contains("yes") ||userCommand.contains("okay"))
                            && sendMessageFormat == 0){
                        sendMessageFormat = 1;
                        Speak("Which mail address do you want to send an mail");
                        while (tts.isSpeaking());
                        Listen();
                    }
                    /*else if(sendMessageFormat == 0 && messageCount > 0) {
                        Speak("Do you want to listen an email");
                        while (tts.isSpeaking()) ;
                        listenFormat = 2;
                        Listen();
                    }*/
                    else{
                        Speak("Do you want to read your mailbox again");
                        while (tts.isSpeaking());
                        listenFormat = 4;
                        Listen();
                    }

                    break;
                case 4:
                    if(userCommand.contains("yes") ||userCommand.contains("okay")){
                        Speak("I'm reading you mails, please wait");
                        boolean readSuccessfully = false;
                        try {
                            readSuccessfully = new ReadMails().execute().get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(readSuccessfully){
                            if(messageCount > 1)
                                Speak("You have " + messageCount + " mails.");
                            else if(messageCount == 1)
                                Speak("You have " + messageCount + " mail.");
                            else
                                Speak("Your mailbox is empty.");

                            if(messageCount > 0) {
                                Speak("Do you want to listen an email");
                                while (tts.isSpeaking()) ;
                                listenFormat = 2;
                                Listen();
                            }else{
                                Speak("Do you want to send an email");
                                while (tts.isSpeaking());
                                listenFormat = 3;
                                Listen();
                            }
                        } else{
                            Speak("Failed when reading your mails, Do you want to try again");
                            while (tts.isSpeaking());
                            listenFormat = 1;
                            Listen();
                        }
                    }else{
                        if(messageCount > 0) {
                            Speak("Do you want to listen an email");
                            while (tts.isSpeaking()) ;
                            listenFormat = 2;
                            Listen();
                        }else{
                            Speak("Do you want to send an email");
                            while (tts.isSpeaking());
                            listenFormat = 3;
                            Listen();
                        }
                    }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
}