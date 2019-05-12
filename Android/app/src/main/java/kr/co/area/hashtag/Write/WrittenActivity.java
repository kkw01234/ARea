package kr.co.area.hashtag.Write;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Write.WriteActivity;


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
        textView.setText(WriteActivity.reviewText);
        ratingBar.setRating(WriteActivity.reviewPoint);
        imageView.setImageBitmap(WriteActivity.scaled);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish(); //이전으로 되돌아갈때는 Intent로 옮기면 안되고 finish()로 해주면 됩니다.
            }
        });
    }
}
