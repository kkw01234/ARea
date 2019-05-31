package kr.co.area.hashtag.write;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;


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
        TextView adView = (TextView) findViewById(R.id.adView);
        textView.setText(WriteReviewActivity.reviewText);
        ratingBar.setRating(WriteReviewActivity.reviewPoint);
        adView.setText(WriteReviewActivity.reviewAddress);
        imageView.setImageBitmap(WriteReviewActivity.scaled);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish(); //이전으로 되돌아갈때는 Intent로 옮기면 안되고 finish()로 해주면 됩니다.
            }
        });
    }
    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(WrittenActivity.this, HomeActivity.class));
        finish();
    }
}
