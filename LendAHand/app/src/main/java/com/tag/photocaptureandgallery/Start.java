package com.tag.photocaptureandgallery;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.takeimage.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.*;


public class Start extends AppCompatActivity {
    //final Handler handler= new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // TODO Auto-generated method stub
        // Intent i = new Intent(Start.this, MapsActivity.class);
        //startActivity(i);


        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Intent intent = new Intent(Start.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Timer t = new Timer();

        t.schedule(task, 2000);
    }


}