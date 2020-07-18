package com.example.android.photouploader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    private Button CaptureImageFromCamera,UploadImageToServer;

    private ImageView ImageViewHolder;

    private EditText imageName;

    public static final int RequestPermissionCode  = 1 ;
    public static final int CAM_REQUEST = 1314;

    private Bitmap bitmap;

    private Uri filePath;
    private String realFilePath = null;
    private String copyRealFilePath = null;

    //ArrayList to store image uri and name when internet connection lost
    ArrayList<Images> imageFiles = new ArrayList<Images>();

    String GetImageNameFromEditText;

    private static String UPLOAD_URL ="http://192.168.64.2/ImageUpload/upload.php";

    private String fileName = "imageData's.tmp";

    private static final String LOG_TAG = "Automatic network control¸";
    private NetworkChangeReceiver receiver;
    static boolean isConnected = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!InternetStatus.getInstance(getApplicationContext()).isOnline()) {
            // read from file
            imageFiles = getListFromFile();
        }

        // broadcastReceiver start which check network connection.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        CaptureImageFromCamera = (Button)findViewById(R.id.buttonChoose);
        ImageViewHolder = (ImageView)findViewById(R.id.imageView);
        UploadImageToServer = (Button) findViewById(R.id.buttonUpload);
        imageName = (EditText)findViewById(R.id.editText);

        EnableRuntimePermissionToAccessCamera();

        CaptureImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAM_REQUEST);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAM_REQUEST);
                }
                catch (Exception exc){
                    Toast.makeText(getBaseContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        UploadImageToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageNameFromEditText = imageName.getText().toString();

                // check if image file location is allocated
                if(realFilePath != null) {
                    //check if internet connection is on
                    if (InternetStatus.getInstance(getApplicationContext()).isOnline()) {
                        uploadImage();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Ooops! No WiFi/Mobile Networks Connected!", Toast.LENGTH_SHORT).show();

                        Images image = new Images(realFilePath, GetImageNameFromEditText);
                        imageFiles.add(image);

                        // write image datas to file(imageData's.tmp) from imageFiles
                        saveArrayList(imageFiles);

                        GetImageNameFromEditText = null;
                        realFilePath = null;
                    }

                    bitmap.recycle();
                    bitmap = null;
                    ImageViewHolder.setImageBitmap(null);
                    imageName.setVisibility(View.INVISIBLE);
                    imageName.setText(null);
                }
                else {
                    Toast.makeText(MainActivity.this, "Please take a photo first!!!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage(){
        int control = 1;
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        try {
            pd.setMessage("loading");
            pd.show();
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addParameter("name", GetImageNameFromEditText)
                    .addFileToUpload(realFilePath, "image")
                    .setMaxRetries(2)
                    .startUpload();

        } catch (Exception e) {
            control = 2;
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if(control == 1)
            Toast.makeText(this, "Successfully uploading image to server", Toast.LENGTH_LONG).show();


        GetImageNameFromEditText = null;
        realFilePath = null;

        pd.cancel();
    }

    // Star activity for result method to Set captured image on image view after click.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageName.setVisibility(View.VISIBLE);
        imageName.setHint("Name For Image");

        if(requestCode == CAM_REQUEST && resultCode == RESULT_OK){

            bitmap = (Bitmap) data.getExtras().get("data");

            ImageViewHolder.setImageBitmap(bitmap);

            filePath = getImageUri(getApplicationContext(), bitmap);
            realFilePath = getRealPathFromURI(filePath);

            imageName.setVisibility(View.VISIBLE);
            imageName.setHint("Enter Photo Name");
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    // Requesting runtime permission to access camera.
    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(MainActivity.this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    //write arrayList to file
    private void saveArrayList(ArrayList<Images> arrayList) {
        for(int i=0; i<arrayList.size(); i++){
            Log.v("saveArrayList: ", " path: " + arrayList.get(i).getImageFilePath()
                    + " name: " + arrayList.get(i).getImageName());
        }

        try {
            FileOutputStream fos = openFileOutput( fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(arrayList);
            oos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //read imagePath and imageName from file
    private ArrayList<Images> getListFromFile() {
        ArrayList<Images> arrayList = new ArrayList<Images>();

        try {
            FileInputStream fis = openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            arrayList = (ArrayList<Images>) ois.readObject();
            ois.close();
            fis.close();

            deleteFile(fileName);
        } catch (IOException | ClassNotFoundException e) {
            Log.v("getListFromFile", "   No file founds");
            e.printStackTrace();
        }

        for (int i = 0; i < arrayList.size(); i++) {
            Log.v("getListFromFile: ", " path: " + arrayList.get(i).getImageFilePath()
                    + " name: " + arrayList.get(i).getImageName());
        }

        return arrayList;
    }

    @SuppressLint("LongLogTag")
    @Override
    //Activity Kapatıldığı zaman receiver durduralacak.Uygulama arka plana alındığı zamanda receiver çalışmaya devam eder.
    protected void onDestroy() {
        Log.v(LOG_TAG, "onDestory");
        super.onDestroy();

        saveArrayList(imageFiles);

        //receiver durduruluyor.
        unregisterReceiver(receiver);
    }

    //Follow changes of network connection and send images to server if connection on.
    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            isNetworkAvailable(context); //receiver çalıştığı zaman çağırılacak method

        }

        @SuppressLint("LongLogTag")
        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE); //Sistem ağını dinliyor internet var mı yok mu

            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                            if(!isConnected){ //internet varsa
                                isConnected = true;
                                Log.v(LOG_TAG, "internete Bağlandınız!");

                                if(realFilePath != null)
                                    copyRealFilePath = realFilePath;


                                File f = getFileStreamPath(fileName);
                                if (f.length() == 0) {
                                    Log.v("length is ", "zero.");
                                } else {
                                    imageFiles = getListFromFile();
                                }

                                if(imageFiles.size() > 0){
                                    for(int index=0; index<imageFiles.size(); index++){
                                        Log.v("sending", "sending");
                                        realFilePath = imageFiles.get(index).getImageFilePath();
                                        GetImageNameFromEditText = imageFiles.get(index).getImageName();

                                        uploadImage();
                                    }
                                }

                                imageFiles = new ArrayList<Images>();
                                realFilePath = copyRealFilePath;
                            }
                            return true;
                        }
                    }
                }
            }
            isConnected = false;
            Toast.makeText(context, "İnternet Yok", Toast.LENGTH_LONG).show();
            Log.v(LOG_TAG, "İnternet Yok!");

            return false;
        }
    }

}