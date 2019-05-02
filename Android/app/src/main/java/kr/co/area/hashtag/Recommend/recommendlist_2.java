package kr.co.area.hashtag.Recommend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import kr.co.area.hashtag.Main.Mypage;
import kr.co.area.hashtag.R;

public class recommendlist_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendlist_2);

        //마이페이지로 넘어가기버튼실행
        Button P2 = (Button)findViewById(R.id.plus2);
        P2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), Mypage.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어가기
            }
        });
    }
}



