package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Recommend.ImageGridAdapter;
import kr.co.area.hashtag.asyncTask.ProfileTask;


public class Mypage extends AppCompatActivity {

    static TextView userid;
    TextView username;
    Button change, okbutton;
    int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    ImageView profileView;
    Image img;
    Activity activity;
    String userimg;

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
        activity = this;
        userid = (TextView) findViewById(R.id.textId);
        username = (TextView) findViewById(R.id.textName);
        change = (Button) findViewById(R.id.changeMy);
        okbutton = (Button) findViewById(R.id.okbtn);

        SharedPreferences auto = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userId = auto.getString("userId",null);
        String userName = auto.getString("userName",null);
        String image = auto.getString("userImg","");
        Bitmap bitmap = StringToBitMap(image);

        userid.setText(userId);
        username.setText(userName);
        if(!(image.equals(""))) {
            profileView = (ImageView) findViewById(R.id.profilimg);
            profileView.setImageBitmap(bitmap);
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(getApplicationContext(), ChangeMypage.class);
                startActivity(myintent);
            }
        });

        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                // 이미지
                Bitmap bitmap = scaled;

 // 기타 필요한 내용
                String attachmentName = "bitmap";
                String attachmentFileName = "bitmap.bmp";
                String crlf = "\r\n";
                String twoHyphens = "--";
                String boundary =  "*****";

// request 준비
                HttpURLConnection httpUrlConnection = null;
                URL url = null;
                try {
                    url = new URL("http://118.220.3.71:13565/write_image");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setDoOutput(true);

                try {
                    httpUrlConnection.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
                httpUrlConnection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);

// content wrapper시작
                DataOutputStream request = null;
                try {
                    request = new DataOutputStream()httpUrlConnection.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    request.writeBytes(twoHyphens + boundary + crlf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.writeBytes("Content-Disposition: form-data; name=\"" +
                            attachmentName + "\";filename=\"" +
                            attachmentFileName + "\"" + crlf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.writeBytes(crlf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

// Bitmap을 ByteBuffer로 전환
                byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                for (int i = 0; i < bitmap.getWidth(); ++i) {
                    for (int j = 0; j < bitmap.getHeight(); ++j) {
                        //we're interested only in the MSB of the first byte,
                        //since the other 3 bytes are identical for B&W images
                        pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                    }
                }
                try {
                    request.write(pixels);
                } catch (IOException e) {
                    e.printStackTrace();
                }

// content wrapper종료
                try {
                    request.writeBytes(crlf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.writeBytes(twoHyphens + boundary +
                            twoHyphens + crlf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

// buffer flush
                try {
                    request.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    request.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

// Response받기
                InputStream responseStream = null;
                try {
                    responseStream = new
                            BufferedInputStream(httpUrlConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader responseStreamReader =
                        new BufferedReader(new InputStreamReader(responseStream));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while (true) {
                    try {
                        if (!((line = responseStreamReader.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stringBuilder.append(line).append("\n");
                }
                try {
                    responseStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String response = stringBuilder.toString();


//Response stream종료
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

// connection종료
                httpUrlConnection.disconnect();
*/
  //              System.out.println(userimg);
 //               Bitmap image = scaled;
  //              String result = new ProfileTask(activity).execute(String.valueOf(image)).get();


                if (!(userimg.equals(image))){
                   try {
                       String result = new ProfileTask(activity).execute(String.valueOf(bitmap)).get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        System.out.println(state);
                        if (state.equals("success")) {
                            Toast.makeText(Mypage.this, "이미지가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else if (state.equals("fail"))
                            Toast.makeText(Mypage.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
               }
                startActivity(new Intent(Mypage.this, HomeActivity.class));
                finish();
            }
        });
        //-----------------------------------------------------------------------
        // 사진들을 보여줄 GridView 뷰의 어댑터 객체를 정의하고 그것을 이 뷰의 어댑터로 설정.

//        GridView gridViewImages = (GridView)findViewById(R.id.gridViewImages);
//        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(this, imageIDs);
//        gridViewImages.setAdapter(imageGridAdapter);
    }

    //프로필 사진

    public void loadImagefromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

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
                userimg = BitMapToString(scaled);

                profileView = (ImageView) findViewById(R.id.profilimg);
                profileView.setImageBitmap(scaled);

            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;

    }
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}

