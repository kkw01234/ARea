package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Recommend.ImageGridAdapter;


public class Mypage extends AppCompatActivity {

    static TextView userid;
    TextView username;
    Button change;

    private int[] imageIDs = new int[] {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
            R.drawable.image8,
            R.drawable.image9,
            R.drawable.image10,
            R.drawable.image11,
            R.drawable.image12,
            R.drawable.image13,
    };

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Mypage.this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        userid = (TextView) findViewById(R.id.textId);
        username = (TextView) findViewById(R.id.textName);
        change = (Button) findViewById(R.id.changeMy);

        SharedPreferences auto = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userId = auto.getString("userId",null);
        String userName = auto.getString("userName",null);

        userid.setText(userId);
        username.setText(userName);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getApplicationContext(), ChangeMypage.class);
                startActivity(myintent);
            }
        });
        //-----------------------------------------------------------------------
        // 사진들을 보여줄 GridView 뷰의 어댑터 객체를 정의하고 그것을 이 뷰의 어댑터로 설정.

        GridView gridViewImages = (GridView)findViewById(R.id.gridViewImages);
        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(this, imageIDs);
        gridViewImages.setAdapter(imageGridAdapter);
    }
}

