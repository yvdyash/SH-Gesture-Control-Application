package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {

    private VideoView videov;
    private int gesture;
    private int replayCount = 0;
    private boolean firstPlay = true;
    private int cntr = 0;
//    private final int MAX_REPLAY_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //initialize the gesture
        Bundle bundle = getIntent().getExtras();
        gesture = bundle.getInt("GestureInd");
        firstPlay = true;
        cntr = 0;

        Button replayBtn = findViewById(R.id.button3);
        Button pracBtn = findViewById(R.id.button2);

        videov = findViewById(R.id.videoView);

        replayBtn.setText("Play");
        replayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // video will play 3 times when played first
                if(firstPlay){
                    playVideoThrice();
                    firstPlay = false;
                    cntr = 0;
                }
                else
                    playVideo();

                replayBtn.setText("Replay");

            }

            private void playVideo(){
                int vid = getVideoID();
                String expertVideoPath = "android.resource://com.example.assignment2/" + vid ;
                Uri uri = Uri.parse(expertVideoPath);
                videov.setVideoURI(uri);
                videov.start();
            }

            private void playVideoThrice(){
                Toast.makeText(MainActivity2.this, "The video will be played 3 times", Toast.LENGTH_SHORT).show();
                int vid = getVideoID();
                String expertVideoPath = "android.resource://com.example.assignment2/" + vid ;
                Uri uri = Uri.parse(expertVideoPath);
                videov.setVideoURI(uri);
                videov.start();
                videov.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(cntr<2) {
                            videov.start();
                            cntr++;
                        }
                    }
                });
            }

            private int getVideoID() {
                if(gesture == 0)
                    return R.raw.lighton;
                if(gesture == 1)
                    return R.raw.lightoff;
                if(gesture == 2)
                    return R.raw.fanon;
                if(gesture == 3)
                    return R.raw.fanoff;
                if(gesture == 4)
                    return R.raw.increasefanspeed;
                if(gesture == 5)
                    return R.raw.decreasefanspeed;
                if(gesture == 6)
                    return R.raw.setthermo;
                if(gesture == 7)
                    return R.raw.zero;
                if(gesture == 8)
                    return R.raw.one;
                if(gesture == 9)
                    return R.raw.two;
                if(gesture == 10)
                    return R.raw.three;
                if(gesture == 11)
                    return R.raw.four;
                if(gesture == 12)
                    return R.raw.five;
                if(gesture == 13)
                    return R.raw.six;
                if(gesture == 14)
                    return R.raw.seven;
                if(gesture == 15)
                    return R.raw.eight;
                if(gesture == 16)
                    return R.raw.nine;
                return -1;
            }
        });

        pracBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity3Intent = new Intent(view.getContext(), MainActivity3.class);
                Bundle bundle = new Bundle();
                bundle.putInt("GestureInd", gesture);
                mainActivity3Intent.putExtras(bundle);
                startActivity(mainActivity3Intent);
            }
        });

    }
}