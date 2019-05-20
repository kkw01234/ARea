package kr.co.area.hashtag.recommend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.main.RestActivity;

public class DetailRecommendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recommend);
        Button P1 = (Button)findViewById(R.id.plus1);
        P1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(
                        getApplicationContext(), RestActivity.class); // 다음 넘어갈 클래스 지정
                        startActivity(intent); // 다음 화면으로 넘어가기
            }
        });
    }
    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(DetailRecommendActivity.this, HomeActivity.class));
        finish();
    }
}
