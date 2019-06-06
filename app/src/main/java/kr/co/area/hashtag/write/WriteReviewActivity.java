package kr.co.area.hashtag.write;


import kr.co.area.hashtag.asyncTask.WriteReviewTask;
import kr.co.area.hashtag.asyncTask.WriteReviewWithImageTask;
import kr.co.area.hashtag.main.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import kr.co.area.hashtag.R;

public class WriteReviewActivity extends AppCompatActivity {

    int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    ImageView imgView;
    static String reviewText, reviewAddress;
    static float reviewPoint;
    Activity activity;

    //xml
    ImageView img1;
    TextView name;
    EditText ed1;
    RatingBar rb;
    Button wrbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        activity = this;

        Intent intent = getIntent();
        String id = intent.getStringExtra("rest");
        String restname = intent.getStringExtra("name");
        System.out.println(id);

        name = findViewById(R.id.restname_text);
        img1 = findViewById(R.id.writeimage);
        ed1 = findViewById(R.id.wirtetext);
        rb = findViewById(R.id.writepoint);
        wrbtn = findViewById(R.id.writebtn);

        name.setText(restname);

        wrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reviewText = ed1.getText().toString();
                reviewPoint = rb.getRating();
                if (scaled != null) {
                    try {
                        String result = new WriteReviewWithImageTask(activity).execute(scaled, id, reviewText, Float.toString(reviewPoint)).get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        if (state.equals("success")) { // success인 경우 작성된 리뷰의 아이디를 받는다.
                            String review_id = jObject.getString("review_id");
                            Toast.makeText(activity.getApplicationContext(), "리뷰 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, MyReviewActivity.class);
                            intent.putExtra("review_id", review_id);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String result = new WriteReviewTask(activity).execute(id, reviewText, Float.toString(reviewPoint)).get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        if (state.equals("success")) { // success인 경우 작성된 리뷰의 아이디를 받는다.
                            String review_id = jObject.getString("review_id");
                            Toast.makeText(activity.getApplicationContext(), "리뷰 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, MyReviewActivity.class);
                            intent.putExtra("review_id", review_id);
                            startActivity(intent);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(WriteReviewActivity.this, HomeActivity.class));
        finish();
    }

    //로드버튼 클릭시 실행
    public void loadImagefromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //이미지 선택작업을 후의 결과 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                imgView = (ImageView) findViewById(R.id.writeimage);
                imgView.setImageBitmap(scaled);

            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}



