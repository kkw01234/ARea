package kr.co.area.hashtag.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        Button MypageButton = (Button) findViewById(R.id.MypageButton);

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
        MypageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MypageFunction(v);
            }
        });

    }

    //메뉴만들기
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //메뉴 눌렀을때 넘어갈 화면 정의
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu1:
                Intent intent = new Intent(MainActiviy.this, AR.class);
                startActivity(intent);
                break;
            case R.id.menu2:
                Intent intent2 = new Intent(MainActiviy.this, GoogleMapsActivity.class);
                startActivity(intent2);
                break;
            case R.id.menu3:
                Intent intent3 = new Intent(MainActiviy.this, RecommendActivity.class);
                startActivity(intent3);
                break;
            case R.id.menu4:
                Intent intent4 = new Intent(MainActiviy.this, WriteActivity.class);
                startActivity(intent4);
                break;
            case R.id.menu5:
                Intent intent5 = new Intent(MainActiviy.this, Mypage.class);
                startActivity(intent5);
                break;
        }
        return true;

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

    public void MypageFunction(View v){
        Intent mypageintent = new Intent(getApplicationContext(),Mypage.class);
        startActivity(mypageintent);
    }
}
