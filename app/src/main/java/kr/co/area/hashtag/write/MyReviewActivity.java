package kr.co.area.hashtag.write;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import kr.co.area.hashtag.asyncTask.GetOneReviewTask;
import kr.co.area.hashtag.asyncTask.WriteReviewTask;
import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;


public class MyReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_written);
        Button btn2 = findViewById(R.id.button6);

        Intent intent = getIntent();
        String review_id = intent.getStringExtra("review_id");
        System.out.println("리뷰 아이디 : " + review_id);

        try {
            String result = new GetOneReviewTask(this).execute(review_id).get();
            System.out.println(result);
            JSONObject jObject = new JSONObject(result);
            String state = jObject.getString("result");
            if (state.equals("success")) { // success인 경우 작성된 리뷰의 아이디를 받는다.

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView textView = findViewById(R.id.viewText);
        RatingBar ratingBar = findViewById(R.id.ratingBar1);
        ImageView imageView = findViewById(R.id.imageView1);
        TextView adView = findViewById(R.id.adView);

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
        startActivity(new Intent(MyReviewActivity.this, HomeActivity.class));
        finish();
    }
}
