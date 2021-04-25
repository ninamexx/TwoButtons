package com.example.twobuttons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    AnimationDrawable loadingAnimation;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        loadingAnimation.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = (ImageView)findViewById(R.id.dots);
        imageView.setBackgroundResource(R.drawable.loadinganimation);
        loadingAnimation = (AnimationDrawable) imageView.getBackground();



        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class); startActivity(i);
                finish(); } }, 3000);

    }

}
