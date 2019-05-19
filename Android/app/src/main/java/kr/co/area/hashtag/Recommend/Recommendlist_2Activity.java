package kr.co.area.hashtag.Recommend;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.libraries.places.compat.Place;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.Main.HomeActivity;
import kr.co.area.hashtag.Main.Mypage;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.DetailPlaceTask;
import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.utils.Parameter;
import kr.co.area.hashtag.utils.RequestHttpURLConnection;

public class Recommendlist_2Activity extends AppCompatActivity {
    boolean indatabase = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendlist_2);
        String title = "";
        String address="";
        String id ="";
        LatLng pos = null;

        Bundle extra = getIntent().getExtras();

        if(extra == null){
            title="error";

        }else{
            title = extra.getString("title");
            address = extra.getString("address");
            id=extra.getString("id");
            pos = (LatLng)extra.getParcelable("pos");

        }

        TextView Place_nameView = (TextView) findViewById(R.id.place_name);
        TextView AddressView = (TextView) findViewById(R.id.place_address);
        TextView OpeningHour = (TextView) findViewById(R.id.place_time);
        TextView PhoneView = (TextView) findViewById(R.id.place_phone);
        Place_nameView.setText(title);
        AddressView.setText(address);
        JsonObject object = getPlaceInformation(id);
        JsonElement phone = object.get("formatted_phone_number");
        JsonObject opening_hours = (JsonObject) object.get("opening_hours");
        if(opening_hours.has("weekday_text")) {
            JsonArray weekday_text = (JsonArray) opening_hours.get("weekday_text");
            OpeningHour.setText("");
            if (weekday_text != null) {
                for (int i = 0; i < weekday_text.size(); i++) {
                    OpeningHour.append(weekday_text.get(i).getAsString());
                }
            }
        }
        PhoneView.setText(phone.getAsString());
        if(!indatabase){
            saveDB();
        }


    }
    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(Recommendlist_2Activity.this, HomeActivity.class));
        finish();
    }

    public JsonObject getPlaceInformation(String id) {
        try {//place의 정보를 받을 수 있는 메소드
            String result = new DetailPlaceTask(this).execute(id).get();
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);
            return (JsonObject) obj.get("result");
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }
    public JsonObject getPlace(String id){
        try {
            String result = new PlaceTask(this).execute().get();
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);
            JsonElement str = (JsonElement) obj.get("result");
            if(str.getAsString().equals("fail")) {
                indatabase =false;
                return getPlaceInformation(id);
            }else{
                indatabase =true;
                return (JsonObject) str;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void saveDB(){

    }
}



