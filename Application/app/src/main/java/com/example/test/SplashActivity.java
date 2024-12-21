package com.example.test;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, "onCreate: SplashActivity started");

        moveMain(2);
    }

    private void moveMain(int sec){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               Log.d(TAG, "run: Starting LoginActivity");
               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
               finish();
               Log.d(TAG, "run: SplashActivity finished");
            }
        }, 1000 * sec);
    }
}
