package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recommendbutton = (Button) findViewById(R.id.button);
        Button writebutton = (Button) findViewById(R.id.button4);

        recommendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCilck_button(v);
            }
        });
        writebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onCilck_button (View v){

        Intent intent_02 = new Intent(getApplicationContext(), recommend.class);
        startActivity(intent_02);

    }
}
