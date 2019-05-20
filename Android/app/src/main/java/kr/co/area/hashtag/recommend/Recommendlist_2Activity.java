package kr.co.area.hashtag.recommend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.DetailPlaceTask;
import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.asyncTask.PlaceWriteTask;

public class Recommendlist_2Activity extends AppCompatActivity {
    boolean indatabase = false;

    TextView Place_nameView;
    TextView AddressView;
    TextView OpeningHour;
    TextView PhoneView;
    String title = "";
    String address="";
    String id ="";
    LatLng pos = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendlist_2);

        Bundle extra = getIntent().getExtras();

        if(extra == null){
            title="error";

        }else{
            title = extra.getString("title");
            address = extra.getString("address");
            id=extra.getString("id");
            pos = extra.getParcelable("pos");

        }

        Place_nameView = (TextView) findViewById(R.id.place_name);
        AddressView = (TextView) findViewById(R.id.place_address);
        OpeningHour = (TextView) findViewById(R.id.place_time);
        PhoneView = (TextView) findViewById(R.id.place_phone);
        Place_nameView.setText(title);
        AddressView.setText(address);

        getPlace(id);


    }
    //뒤로가기
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Recommendlist_2Activity.this, HomeActivity.class));
        finish();
    }

    public void getPlaceInformation(String id) {
        JsonObject obj=null;
        try {//place의 정보를 받을 수 있는 메소드
            String result = new DetailPlaceTask(this).execute(id).get();
            JsonParser parser = new JsonParser();
            obj = (JsonObject) parser.parse(result);

        }catch(Exception e){
            e.printStackTrace();
        }

        JsonObject object = (JsonObject) obj.get("result");
        JsonElement phone = object.get("formatted_phone_number");
        JsonObject opening_hours = (JsonObject) object.get("opening_hours");
        if(opening_hours != null||opening_hours.has("weekday_text")) {
            JsonArray weekday_text = (JsonArray) opening_hours.get("weekday_text");
            OpeningHour.setText("");
            if (weekday_text != null) {
                for (int i = 0; i < weekday_text.size(); i++) {
                    OpeningHour.append(weekday_text.get(i).getAsString());
                }
            }
        }
        PhoneView.setText(phone.getAsString());

        saveDB(id,title,address,pos,null,OpeningHour.getText().toString(),phone.getAsString());
    }
    public void getPlace(String id){ //DB에 데이터가 있는지 확인 // 없을경우 REST API 실행, 있을경우 DB 불러옴
        try {
            String result = new PlaceTask(this).execute(id).get();
            Log.i("GETPLACE", result);
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);
            JsonElement str = obj.get("result");
            if(str.getAsString().equals("fail")) {
                indatabase =false;
                getPlaceInformation(id);
                return;
            }

            JsonElement text = obj.get("rest_text");
            JsonElement time = obj.get("rest_time");
            JsonElement phone = obj.get("rest_phone");
            OpeningHour.setText(time.getAsString());
            PhoneView.setText(phone.getAsString());


        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void saveDB(String google_id,String restName,String restAddress,LatLng restPoint, String restText, String restTime, String restPhone){
        try {
            String result = new PlaceWriteTask(this)
                    .execute(google_id, restName, restAddress, Double.toString(restPoint.latitude),Double.toString(restPoint.longitude),
                            restText, restTime, restPhone)
                    .get();
            Log.i("TAGsaveDB",result);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}



