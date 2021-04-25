package com.example.twobuttons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //I know there is an issue here i might be able to fix with dependencies


        button = (Button) findViewById(R.id.compassbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCompass();
            }
        });

        button = (Button) findViewById(R.id.accbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAcc();
            }
        });
    }

    public void openCompass() {
        Intent intent = new Intent(this, CompassSensor.class);
        startActivity(intent);
    }
    public void openAcc() {
        Intent intent = new Intent(this, AccSensor.class);
        startActivity(intent);
    }
}