package com.example.twobuttons;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.hardware.SensorEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CompassSensor extends AppCompatActivity implements SensorEventListener {

    // device sensor manager
    private SensorManager SensorManage;

    // define the compass picture that will be use
    private ImageView compassimage;

    // record the angle turned of the compass picture
    private float DegreeStart = 0f;

    TextView DegreeTV;
    Vibrator vibrator;
    float lastDegree;
    private TextToSpeech mTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_compass_sensor);
        }
        //catch (Error e){
        //    System.out.println("Bold font not currently working");
        //}

        catch (Throwable t) {       //I know there is an issue here i might be able to fix with dependencies
        System.out.println("Bold text setting failed, no fix yet");
        }
        //
        compassimage = (ImageView) findViewById(R.id.compass_image);

        // TextView that will display the degree
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);

        // initialize your android device sensor capabilities
        SensorManage = (SensorManager) getSystemService(SENSOR_SERVICE);

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
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        SensorManage.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // code for system's orientation sensor registered listeners
        SensorManage.registerListener(this, SensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        //Test to change colour when degree is 360 or 0, aka heading north (own code)
        if(degree <= 10 || degree >= 350){
            //long[] pattern = {0, 100, 1000, 200, 2000};

            DegreeTV.setTextColor(Color.parseColor("#F26419"));


            if((degree <= 10 || degree >= 350) && (lastDegree > 10 && lastDegree < 350)){
                //HÄR SKA TTS in
                if(mTTS.isSpeaking()){
                    mTTS.playSilence(3000,TextToSpeech.QUEUE_FLUSH, null);
                }

                else{
                    mTTS.setSpeechRate(0.8f);
                    mTTS.speak("Heading North", TextToSpeech.QUEUE_ADD, null);
                    mTTS.playSilence(5000,TextToSpeech.QUEUE_ADD, null);
                }

                vibrator.vibrate(150);
                }

        }
        else{
            DegreeTV.setTextColor(Color.parseColor("#33658A"));
        }

        DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");

        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(210);

        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
        lastDegree = degree;



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

     @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}