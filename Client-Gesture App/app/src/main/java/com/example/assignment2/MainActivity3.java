package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private int gesture;
    private Uri fileUri;
    private File videoGestureFile;
    private int REQUEST_CODE = 101;
    private String[] gestureNames = new String[]{"LightOn", "LightOff", "FanOn", "FanOff", "FanUp", "FanDown", "SetThermo", "Num0", "Num1", "Num2", "Num3", "Num4", "Num5", "Num6", "Num7", "Num8", "Num9"};
    static int[] practiceNum = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String DEFAULT_REQUEST_URL = "http://192.168.0.5:5000/upload_video";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        EditText serverAddress = findViewById(R.id.editText);
        serverAddress.setText(DEFAULT_REQUEST_URL);

        //initialize the gesture
        Bundle bundle = getIntent().getExtras();
        gesture = bundle.getInt("GestureInd");

        Button recordBtn = findViewById(R.id.button4);
        Button uploadBtn = findViewById(R.id.button5);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(videoGestureFile == null){
                    Toast.makeText(MainActivity3.this, "Video file null. Please record a valid video first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String charset = "UTF-8";

                try {
                    HTTPRequestUtility sender = new HTTPRequestUtility(serverAddress.getText().toString(), charset);
                    sender.setFile("gestureVideo", videoGestureFile);
                    List<String> response = sender.execute();

                    for (String resp : response) {
                        Log.i("LOG", resp);
                    }

                    Toast.makeText(MainActivity3.this, "Upload of "+gestureNames[gesture]+" to "+serverAddress.getText().toString()+" Complete!", Toast.LENGTH_SHORT).show();

                    //taking the user to the first screen
                    Intent mainActivity = new Intent(view.getContext(), MainActivity.class);
                    startActivity(mainActivity);

                } catch (IOException ex) {
                    Log.e("ERROR", ex.getMessage());
                    Toast.makeText(MainActivity3.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void startRecording()
    {
        practiceNum[gesture]+=1;
        videoGestureFile = new File(getExternalFilesDir(null), gestureNames[gesture]+"_PRACTICE_"+practiceNum[gesture]+".mp4");

        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);
        cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);

        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);

        cameraIntent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true);
        cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

        // Samsung
        cameraIntent.putExtra("camerafacing", "front");
        cameraIntent.putExtra("previous_mode", "front");

        // Huawei
        cameraIntent.putExtra("default_camera", "1");
        cameraIntent.putExtra("default_mode", "com.huawei.camera2.mode.photo.PhotoMode");

        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);

        fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName() + ".provider", videoGestureFile);
        Log.i("Saved Video file path", fileUri.getPath().toString());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, REQUEST_CODE);

    }
}


class HTTPRequestUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    public HTTPRequestUtility(String requestURL, String charset)
            throws IOException {

        this.charset = charset;

        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);

        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    public void setFile(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();


        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    public List<String> execute() throws IOException {
        List<String> response = new ArrayList<String>();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
    }
}