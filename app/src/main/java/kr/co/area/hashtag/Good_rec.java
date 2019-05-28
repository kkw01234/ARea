package kr.co.area.hashtag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import kr.co.area.hashtag.main.RestActivity;

public class Good_rec extends AppCompatActivity implements View.OnClickListener{

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_rec);

        //이미지뷰 클릭리스너로 인텐트하기
        image = (ImageView) findViewById(R.id.imageView5);
        image.setOnClickListener(this);
    }


    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView5:
                // imageview5가 클릭될 시 할 코드작성
                Intent intent = new Intent(
                        getApplicationContext(), RestActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어가기
                break;
        }

    }

}
