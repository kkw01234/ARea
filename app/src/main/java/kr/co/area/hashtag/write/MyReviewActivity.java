package kr.co.area.hashtag.write;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONObject;

import kr.co.area.hashtag.asyncTask.GetOneReviewTask;
import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;


public class MyReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_written);

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
        ImageView imageView = findViewById(R.id.favorite_imageView);

        textView.setText(WriteReviewActivity.reviewText);
        ratingBar.setRating(WriteReviewActivity.reviewPoint);
        imageView.setImageBitmap(WriteReviewActivity.scaled);

    }

    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(MyReviewActivity.this, HomeActivity.class));
        finish();
    }
}
