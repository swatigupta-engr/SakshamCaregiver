package com.zuccessful.trueharmony.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.zuccessful.trueharmony.R;

public class Splash extends Activity {
    private static int SPLASH_SCREEN_TIME_OUT=3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //This method is used so that your splash activity
            //can cover the entire screen.

            setContentView(R.layout.splash_main);
            //this will bind your MainActivity.class file with activity_main.

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   Intent i=new Intent(Splash.this,   LoginActivity.class);
                    //Intent is used to switch from one activity to another.

               startActivity(i);

                    finish();
                    //the current activity will get finished.
                }
            }, SPLASH_SCREEN_TIME_OUT);
        }
    }

