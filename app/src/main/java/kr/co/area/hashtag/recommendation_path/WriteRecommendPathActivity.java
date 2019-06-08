package kr.co.area.hashtag.recommendation_path;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.WritePathTask;
import kr.co.area.hashtag.utils.PathPlace;

public class WriteRecommendPathActivity extends AppCompatActivity {

    private Activity activity;
    public static List<PathPlace> placeList = new ArrayList<>();
    public static LinearLayout layout = null;
    private EditText pathName = null;
    private Bitmap bitmap = null;
    private ImageView imageView = null;
    private EditText writeContent = null;
    public int PICK_IMAGE_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_path_write);
        activity = this;
        pathName = findViewById(R.id.pathname);
        ImageButton loadMap = findViewById(R.id.loadmap);
        layout = (LinearLayout) findViewById(R.id.writescroll);

        imageView = findViewById(R.id.rec_Gallery);
        writeContent = findViewById(R.id.writecontent);
        loadMap.setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), WritePathMapActivity.class);
            //if(placeList.size() != 0)
                //intent.putExtra("list", placeList);
            startActivity(intent);
        });

        Intent intent = getIntent();
        if(intent!=null){
            getPlaceIntent(intent);
        }
        Button writeButton = findViewById(R.id.writePathButton);
        System.out.println(writeButton.toString());
        writeButton.setOnClickListener((v)-> {
            if(bitmap != null) {
                try {
                    String result = new WritePathTask(activity).execute(pathName.getText().toString(), bitmap, placeList, writeContent.getText().toString()).get();
                    JsonParser parser = new JsonParser();
                    JsonObject object = (JsonObject)parser.parse(result);
                    String res = object.get("result").getAsString();
                    if(res.equals("true")){
                        Toast.makeText(getApplicationContext(), "입력완료",Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(getApplicationContext(), "모두 입력하지 않으셨거나 오류가 발생했습니다",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(WriteRecommendPathActivity.this, RecommendPathMainActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    //인텐트 값 저장

    private void getPlaceIntent(Intent intent){
        LinearLayout.LayoutParams layOutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        try {
            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            Double latitude = intent.getDoubleExtra("latitude",0);
            Double longitude = intent.getDoubleExtra("longitude", 0);
            //String placeType = intent.getStringExtra("placeType");

            if(name == null)
                return;

            placeList.add(new PathPlace(id, name, null,latitude,longitude));
            for (int i=0;i<placeList.size();i++){
                TextView textView = new TextView(layout.getContext());
                textView.setText((i+1)+". "+placeList.get(i).name);
                textView.setLayoutParams(layOutParams);
                textView.setTextSize(23);
                PathPlace place;
                textView.setOnClickListener((v)->{
                    TextView view = (TextView) v;
                    String text = ((TextView)v).getText().toString();
                    int io = text.indexOf(".");
                    String str = text.substring(0,1);
                    System.out.println(str);
                    PathPlace pathPlace = placeList.get(Integer.parseInt(str)-1);
                    System.out.println(pathPlace.placeType);
                    //Intent intent1 = new Intent(getApplicationContext(), RestActivity.class);
                    //intent1.putExtra("id",);
                });
                layout.addView(textView);
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loadImageFromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                System.out.println("dfsfds");
                Uri uri = data.getData();

                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap2.getHeight() * (1024.0 / bitmap2.getWidth()));
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, 1024, nh, true);
                imageView.setImageBitmap(bitmap2);
                this.bitmap =bitmap2;
                System.out.println(this.bitmap.getRowBytes());

            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}





