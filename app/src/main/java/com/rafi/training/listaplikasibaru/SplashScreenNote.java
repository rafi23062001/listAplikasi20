package com.rafi.training.listaplikasibaru;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenNote extends AppCompatActivity {

    private int waktu_loading=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_note);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent note=new Intent(SplashScreenNote.this,MainActivity.class);
                startActivity(note);
                finish();
            }
        },waktu_loading);
    }
}
