package kr.co.area.hashtag.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.area.hashtag.Map.GoogleMapsActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Recommend.RecommendActivity;
import kr.co.area.hashtag.Recommend.WriteActivity;

public class MainActiviy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Recommendbutton = (Button) findViewById(R.id.recommendButton);
        Button Writebutton = (Button) findViewById(R.id.WriteButton);
        final Button ARSearchButton = (Button) findViewById(R.id.ARSearchButton);
        Button MapSearchButton = (Button) findViewById(R.id.MapSearchButton);

        Recommendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendFunction(v);
            }
        });
        Writebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFunction(v);
            }
        });
        ARSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARSearchFunction(v);
            }
        });
        MapSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapSearchButton(v);
            }
        });
    }

    public void recommendFunction (View v){
        Intent recommendintent = new Intent(getApplicationContext(), RecommendActivity.class);
        startActivity(recommendintent);
    }

    public void WriteFunction(View v){
        Intent writeintent = new Intent(getApplicationContext(), WriteActivity.class);
        startActivity(writeintent);
    }

    public void ARSearchFunction(View v){

    }


    public void MapSearchButton(View v){
        Intent mapintent = new Intent(getApplicationContext(),GoogleMapsActivity.class);
        startActivity(mapintent);
    }
}
