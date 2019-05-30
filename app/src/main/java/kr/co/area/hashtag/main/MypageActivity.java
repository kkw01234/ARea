package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.UploadProfileImageTask;
import kr.co.area.hashtag.write.WriteActivity;


public class MypageActivity extends AppCompatActivity {

    TextView username;
    Button change, okbutton, writebutton;
    int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    static Bitmap bitmap;
    String userId;
    ImageView profileView;
    Activity activity;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MypageActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        activity = this;
        username = findViewById(R.id.textName);
        change = findViewById(R.id.changeMy);
        okbutton = findViewById(R.id.okbtn);
        writebutton = findViewById(R.id.writebtn);
        profileView = findViewById(R.id.profilimg);

        SharedPreferences auto = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        userId = auto.getString("userId", null);
        String userName = auto.getString("userName", "???");
        username.setText(userName + "님 프로필");

        String image = "http://118.220.3.71:13565/download_file?category=download_my_image&u_id=" + userId;
        Glide.with(this).load(image).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(profileView);

        change.setOnClickListener((View v) -> {
            Intent myintent = new Intent(getApplicationContext(), ChangeMypage.class);
            startActivity(myintent);
        });

        writebutton.setOnClickListener((View v) -> {
            Intent wrintent = new Intent(getApplicationContext(), WriteActivity.class);
            startActivity(wrintent);
        });

        okbutton.setOnClickListener((View v) -> {
            // 이미지
            try {
                String result = new UploadProfileImageTask(activity).execute(scaled, userId).get();
                System.out.println(result);
                JSONObject jObject = new JSONObject(result);
                String state = jObject.getString("result");
                if (state == "success") {
                    String url = "http://118.220.3.71:13565/download_file?category=download_my_image&u_id=" + userId;
                    Glide.with(activity).load(url).into(profileView);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    //프로필 사진

    public void loadImagefromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                profileView.setImageBitmap(scaled);
            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}

