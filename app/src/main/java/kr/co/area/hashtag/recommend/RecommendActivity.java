package kr.co.area.hashtag.recommend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import kr.co.area.hashtag.Good_rec;
import kr.co.area.hashtag.Map_recommend;
import kr.co.area.hashtag.New_rec;
import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;


public class RecommendActivity extends AppCompatActivity{

    private Button Button1, Button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Button1 = (Button)findViewById(R.id.button1);
        Button2 = (Button)findViewById(R.id.button2);

        Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRecommentAction();
            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodRecommentAction();
            }
        });

        ImageButton rec1 = (ImageButton) findViewById(R.id.rec1);
        rec1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailRecommentAction();
            }
        });

        ImageButton rec3 = (ImageButton) findViewById(R.id.rec3);
        rec3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapRcommendAction();
            }
        });


    }



    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(RecommendActivity.this, HomeActivity.class));
        finish();
    }

    public void detailRecommentAction(){
        Intent intent = new Intent(getApplicationContext(),DetailRecommendActivity.class);
        startActivity(intent);
    }

    public void newRecommentAction(){
        Intent intent = new Intent(getApplicationContext(),New_rec.class);
        startActivity(intent);
    }

    public void goodRecommentAction(){
        Intent intent = new Intent(getApplicationContext(), Good_rec.class);
        startActivity(intent);
    }

    public void MapRcommendAction(){
        Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
        startActivity(intent);
    }
}
