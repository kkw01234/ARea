package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

public class WriteActivity extends AppCompatActivity {

    private static int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    ImageView imgView;
    static final String TAG = "WriteActivity";
    static String strText;
    static Image img;
    static float starnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Button btn1 = (Button) findViewById(R.id.button5);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed1 = (EditText) findViewById(R.id.editText);
                RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
                ImageView img1 = (ImageView) findViewById(R.id.imageView);
                starnum = rb.getRating();
                strText = ed1.getText().toString() ;
                Intent intent = new Intent(getApplicationContext(), WrittenActivity.class);
                startActivity(intent);
            }
        });

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

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                imgView = (ImageView) findViewById(R.id.imageView);
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

