package com.example.twobuttons;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;
import java.util.NoSuchElementException;

public class AccSensor extends AppCompatActivity implements SensorEventListener {
    private TextView xTextView, yTextView, zTextView, directionText;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerAvailable, itIsNotFirstTime = false;
    private float currentX, currentY, currentZ, lastX, lastY, lastZ;
    private float xDifference, yDifference, zDifference;
    private float shakeThreshHold = 5f;
    private Vibrator vibrator;
    private String[] direction = {" ", " "};
    private TextToSpeech mTTS;
    private boolean alertIsShowing = false;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_acc_sensor);
        }
        catch (Throwable t) { //I know there is an issue here i might be able to fix with dependencies
            System.out.println("Bold text setting failed, no fix yet");
        }

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#86BBD8"));

        xTextView = findViewById(R.id.X);
        yTextView = findViewById(R.id.Y);
        zTextView = findViewById(R.id.Z);
        directionText = findViewById(R.id.directionText);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerAvailable = true;
        } else {
            xTextView.setText("Accelerometer sensor is not available");
            isAccelerometerAvailable = false;
        }

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        //mButtonSpeak.setEnabled(true);
                        Log.e("TTS", "TTS is working");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xTextView.setText(Math.round(sensorEvent.values[0] * 100.0) / 100.0 + " m/s² ");
        yTextView.setText(Math.round(sensorEvent.values[1] * 100.0) / 100.0 + " m/s² ");
        zTextView.setText(Math.round(sensorEvent.values[2] * 100.0) / 100.0 + " m/s² ");

        currentX = sensorEvent.values[0];
        currentY = sensorEvent.values[1];
        currentZ = sensorEvent.values[2];

        if(itIsNotFirstTime){
            xDifference = Math.abs(lastX - currentX);
            yDifference = Math.abs(lastY - currentY);
            zDifference = Math.abs(lastZ - currentZ);

            if((xDifference > shakeThreshHold && yDifference > shakeThreshHold ) ||
                    (xDifference > shakeThreshHold && zDifference > shakeThreshHold) ||
                    (yDifference > shakeThreshHold && zDifference > shakeThreshHold)){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AlertDialog.Builder shaking = new AlertDialog.Builder(this);

                    shaking.setMessage("You're shaking me! I'll vibrate every time you do that.");
                    shaking.setTitle("Weee");

                    if(!alertIsShowing){
                        shaking.show();
                        alertIsShowing = true;
                    }



                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    vibrator.vibrate(500);
                    //deprecated in API 26 ??
                }

            }

            if(currentX < -0.5) {
                direction[0] = "Right";
            } else if (currentX > 0.5) {
                direction[0] = "Left";
            } else {
                direction[0] = null;
            }

            // Gives direction depending on y-axis value
            if(currentY < -0.5) {
                direction[1] = "Down";
            } else if (currentY > 0.5) {
                direction[1] = "Up";
            } else {
                direction[1] = null;
            }
        }



        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;

        itIsNotFirstTime = true;


        if(direction[0] != null || direction[1] != null) {
            mTTS.playSilence(2000,TextToSpeech.QUEUE_FLUSH, null);
            if(direction[0] != null && direction[1] != null) {
                directionText.setText(direction[0] + " and " + direction[1]);
            } else if (direction[0] != null) {
                directionText.setText(direction[0]);
            } else if (direction[1] != null) {
                directionText.setText(direction[1]);
            }

            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#86BBD8")); //  background to lightblue if  yellow
        } else {
            directionText.setText("Flat");
            mTTS.setSpeechRate(0.8f);

            mTTS.speak("Device is flat on surface", TextToSpeech.QUEUE_ADD, null);
            mTTS.playSilence(5000,TextToSpeech.QUEUE_ADD, null);
            //Decided to not add vibration when device is flat since it sometimes made the device not flat

            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#F6AE2D")); //  background to Yellow
            }




    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isAccelerometerAvailable)
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerAvailable)
            sensorManager.unregisterListener(this);
    }
}