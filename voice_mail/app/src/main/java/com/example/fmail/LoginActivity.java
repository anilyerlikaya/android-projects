package com.example.fmail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Session;
import javax.mail.Store;

public class LoginActivity extends AppCompatActivity {
    private final String LOG_TAG = "LoginActivity";

    //get user credentials with visually
    Button confirmButton;
    EditText emailEdit;
    EditText passwordEdit;

    TextToSpeech tts;

    //user credentials
    String userEmail;
    String userPassword;

    //for keeping user calls or commands
    String userCommand = "";

    Intent speechRecognitionIntent;

    // 1->If yes, go startNewIntent, 2->Read email and password, 3->Yes or No for save credentials and go startNewIntent
    int listenFormat = 0;

    // 1 -> email, 2 -> password;
    int emailOrPassword = 0;

    SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Record audio connection for speech recognizer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        24);
            }
        }

        //setup TextToSpeech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);

                    if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e(LOG_TAG, "Error!!! This Language is not supported");
                    }

                    Speak("Welcome to f mail application. You can close the app with shut down command." +
                            "Please give your commands after the beep tone. Have fun.");

                    if(!isInternetAvailable()) {
                        Speak("Cannot connect to the network, app is going to shut down");
                        finish();
                        tts.shutdown();
                        speechRecognizer.destroy();
                    }
                }
                else
                    Log.e(LOG_TAG, "Initialization Failed!");

                //speech recognition intent
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

                //check for any saved information
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                userEmail = prefs.getString("email", "");
                userPassword = prefs.getString("password", "");
                Log.d(LOG_TAG, "shared pref, user email: " + userEmail + " - user password: " + userPassword);
                if (!userEmail.equals("") && !userPassword.equals("")) {
                    Speak("Old email and password found. Do you want to access " + userEmail + " email address");
                    while (tts.isSpeaking())
                    listenFormat = 1;
                    Listen();
                } else
                    listenCredentials();
            }

        });

        confirmButton = findViewById(R.id.credentialsConfirmButton);
        emailEdit = findViewById(R.id.emailEditText);
        passwordEdit = findViewById(R.id.passwordEditText);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer.cancel();

                checkCredentials();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        speechRecognizer.destroy();
        tts.stop();
        tts.shutdown();
    }

    private void listenCredentials(){
        Speak("You can say your email or enter your credentials to blocks");
        while (tts.isSpeaking());

        listenFormat = 2;
        emailOrPassword = 1;
        Listen();
    }

    //check given credentials by user
    private void checkCredentials(){
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        Log.d(LOG_TAG, "email matcher is matched? " + matcher.matches() + " - password is empty? " + password.isEmpty());

        if (password.isEmpty() || !matcher.matches()) {
            Speak("Wrong username or password, please try again");
        } else {
            //try to connect G-mail with given credentials.
            userEmail = email;
            userPassword = password;

            Boolean result = false;
            try {
                result = new ConnectMailServer().execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(result){
                //go to main activity
                Speak("Successfully access the mail account " + userEmail);
                Speak("Do you want to save these information for easy access next time");
                while (tts.isSpeaking());

                listenFormat = 3;
                Listen();
            }else
                Speak("Cannot access the mail account, please try again");
        }
    }

    private void startNewIntent() {
        Log.d(LOG_TAG, "in startNewIntent");

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", userEmail);
        bundle.putString("password", userPassword);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    //connect userMail address
    @SuppressLint("StaticFieldLeak")
    public class ConnectMailServer extends AsyncTask<Void, Void, Boolean> {
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

                return store.isConnected();
            }catch (Exception e) {
                Log.d(LOG_TAG, "Read mail exception, " + e);
            }

            return false;
        }
    }

    /**
    *@param speech, TextToSpeech library converts speech string to voice.
     */
    private void Speak(String speech){
        tts.speak(speech, TextToSpeech.QUEUE_ADD, null);
    }

    //checking internet connection, returns true or false
    public boolean isInternetAvailable() {
        int timeOut = 10000;
        InetAddress inetAddress = null;

        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException ignored) {
        } catch (ExecutionException ignored) {
        } catch (TimeoutException ignored) {
        }

        return inetAddress!=null && !inetAddress.equals("");
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
            Log.e(LOG_TAG, "error: " + error);

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

            switch(listenFormat){
                case 1:
                    listenFormat = 0;
                    if(userCommand.contains("yes") || userCommand.contains("okay")){
                        Log.d(TAG, "userCommand contains Yes or Okay");

                        startNewIntent();
                    }else{
                        listenCredentials();
                    }
                    break;
                case 2:
                    if(emailOrPassword == 1){
                        userCommand = userCommand.replace(" ", "");
                        Log.d(LOG_TAG, "user command1 is " + userCommand);

                        userCommand = userCommand.replace("at", "@");
                        Log.d(LOG_TAG, "user command2 is " + userCommand);

                        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(userCommand);

                        Log.d(LOG_TAG, "matcher is " + matcher.matches());
                        if(!matcher.matches()){
                            Speak("You said " + userCommand + ". But you need to say your email address.");
                            while (tts.isSpeaking());
                            Listen();
                        }else{
                            userEmail = userCommand;
                            emailEdit.setText(userCommand);
                            emailOrPassword = 2;
                            Speak("You email is " + userCommand + ". Now you can say your password " +
                                    "or change the mail address with cancel command.");
                            while (tts.isSpeaking());
                            Listen();
                        }
                    }else{
                        Log.d(LOG_TAG, "at emailOrPassword2");

                        if(userCommand.contains("cancel") || userCommand.contains("again")){
                            speechRecognizer.cancel();
                            emailOrPassword = 1;
                            listenFormat = 2;
                            emailEdit.setText("");
                            Speak("Please say your email address");
                            while (tts.isSpeaking());
                            Listen();
                            break;
                        }else {
                            userCommand = userCommand.replace(" ", "");
                            Log.d(LOG_TAG, "user command1 is " + userCommand);
                            userPassword = userCommand;

                            Speak("Try to access your mail address. Please wait a second.");
                            while(tts.isSpeaking());

                            boolean canConnect = false;
                            try {
                                canConnect = new ConnectMailServer().execute().get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (canConnect) {
                                emailOrPassword = 0;
                                Speak("Successfully access the mail account " + userEmail);
                                Speak("Do you want to save these information for easy access next time");
                                while (tts.isSpeaking());

                                listenFormat = 3;
                                Listen();
                            } else {
                                Speak("Wrong password, please try again");
                                while (tts.isSpeaking());
                                Listen();
                            }
                        }
                    }
                    break;
                case 3:
                    listenFormat = 0;
                    if(userCommand.contains("yes") || userCommand.contains("okay")){
                        Log.d(TAG, "userCommand contains Yes or Okay");
                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", userEmail);
                        editor.putString("password", userPassword);
                        editor.apply();
                    }
                    startNewIntent();
                    break;
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
