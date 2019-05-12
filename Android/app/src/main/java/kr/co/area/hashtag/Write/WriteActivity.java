package kr.co.area.hashtag.Write;


import kr.co.area.hashtag.utils.RequestHttpURLConnection;
import kr.co.area.hashtag.utils.Parameter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;


import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.area.hashtag.Login.Join;
import kr.co.area.hashtag.Login.Login;
import kr.co.area.hashtag.R;

public class WriteActivity extends AppCompatActivity {

    int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    ImageView imgView;
    String TAG = "WriteActivity";
    Image img;
    static String reviewText, reviewAddress, reviewId, reviewUser;
    static float reviewPoint;
    boolean reviewShare = false;

    //xml
    ImageView img1;
    EditText ed1;
    static EditText address;
    RatingBar rb;
    Switch share;
    Button wrbtn, adbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        img1 = (ImageView) findViewById(R.id.imageView);
        ed1 = (EditText) findViewById(R.id.editText);
        address = (EditText) findViewById(R.id.adText);
        rb = (RatingBar) findViewById(R.id.ratingBar);
        wrbtn = (Button) findViewById(R.id.button5);
        adbtn = (Button) findViewById(R.id.adbutton);
        share = (Switch) findViewById(R.id.shareswitch);

        adbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adintent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(adintent);
            }
        });
        class WriteTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... strings) {
                RequestHttpURLConnection conn = new RequestHttpURLConnection();
                String url = "http://118.220.3.71:13565/register_user";
                ArrayList<Parameter> params = new ArrayList<>();
                params.add(new Parameter("id", strings[0]));
                params.add(new Parameter("pwd", strings[1]));
                params.add(new Parameter("name", strings[2]));
                params.add(new Parameter("email", strings[3]));
                String result = conn.request(url, params, getApplicationContext());
                System.out.println(result);
                return result;
            }
        }

        share.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reviewShare = isChecked;
            }

        });
        wrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences auto = getSharedPreferences("userInform", Activity.MODE_PRIVATE);
                reviewUser = auto.getString("userName", null);
                System.out.println(reviewUser);
                reviewText = ed1.getText().toString() ;
                reviewPoint = rb.getRating();
                reviewAddress = address.getText().toString();

//                try {
//                    String result = new WriteTask().execute(reviewUser,reviewAddress,reviewText,Double.toString(reviewPoint)).get();
//
//                    JSONObject jObject = new JSONObject(result);
//                    String state = jObject.getString("result");
//                    System.out.println(state);
//
//                    if (state.equals("success")) {
//                        Toast.makeText(WriteActivity.this, "글작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
//                        Intent wrintent = new Intent(getApplicationContext(), WrittenActivity.class);
//                        startActivity(wrintent);
//                    }
//                    else if (state.equals("email fail")) {
//                        Toast.makeText(WriteActivity.this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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

