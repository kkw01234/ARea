package kr.co.area.hashtag.recommend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;


public class RecommendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        ImageButton rec1 = (ImageButton) findViewById(R.id.rec1);

        rec1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    detailRecommentAction();
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
}
