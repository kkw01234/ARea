package kr.co.area.hashtag.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.PlaceTask;

public class MenupageActivity extends AppCompatActivity {
    TextView Place_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Place_menu = findViewById(R.id.menulist);

        Intent intent = getIntent();
        String id = intent.getStringExtra("rest_id");
        getPlace(id);

    }

    public void getPlace(String id) { //DB에 데이터가 있는지 확인 // 없을경우 REST API 실행, 있을경우 DB 불러옴
        try {
            String result = new PlaceTask(this).execute(id).get();
            Log.i("GetPlace", result);
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);
            JsonElement text = obj.get("rest_text"); // 레스토랑 설명

            Place_menu.setText(text.getAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //뒤로가기
    public void onBackPressed() {
        finish();
    }

}
