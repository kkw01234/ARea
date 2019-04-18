package com.example.myapplication;

import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class WrittenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_written);
        Button btn2 = (Button) findViewById(R.id.button6);

        Intent intent = getIntent();

        TextView textView = (TextView) findViewById(R.id.viewText);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        textView.setText(WriteActivity.strText);
        ratingBar.setRating(WriteActivity.starnum);
        imageView.setImageBitmap(WriteActivity.scaled);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                startActivity(intent);
            }
        });
    }
}
