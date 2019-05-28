package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.DetailPlaceTask;
import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.asyncTask.PlaceWriteTask;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.write.WriteActivity;

public class RestActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    private Activity activity;
    boolean isFromAR = false;
    boolean islike = false;
    TextView Place_nameView,AddressView,OpeningHour,PhoneView,dataPoint,myPoint,reviewPoint;
    ImageView wordcloud;
    ListView reviewlist;
    String id = "";

    private LayoutInflater listInflater;
    private boolean listLockListView;
    private reviewListViewAdapter listadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        activity = this;
        Bundle extra = getIntent().getExtras();

        if (extra == null) {
            id = "error";
        } else {
            id = extra.getString("id");
            isFromAR = extra.getBoolean("fromVR");
        }
        Place_nameView = findViewById(R.id.place_name);
        AddressView = findViewById(R.id.place_address);
        OpeningHour = findViewById(R.id.place_time);
        PhoneView = findViewById(R.id.place_phone);

        getPlace(id);

        reviewlist = findViewById(R.id.reviewlist);
        listLockListView = true;

        // 푸터를 등록. setAdapter 이전에 해야함.
        listInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reviewlist.addFooterView(listInflater.inflate(R.layout.listview_footer, null));

        // 스크롤 리스너 등록
        reviewlist.setOnScrollListener(this);

        // Adapter 생성
        listadapter = new reviewListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        reviewlist.setAdapter(listadapter);

        TextView extext1 = (TextView) findViewById(R.id.tv_list_footer);
        extext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listadapter.addItem((float) 2.5, "kjy","내용") ;
                reviewlist.setAdapter(listadapter);
            }
        });

        Button writebtn = (Button) findViewById(R.id.evaluate_button);
        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wrintent = new Intent(getApplicationContext(), WriteActivity.class);
                wrintent.putExtra("rest",id);
                startActivity(wrintent);
            }
        });

        Button about_bt = (Button) findViewById(R.id.favorite_button);
        about_bt.setOnTouchListener(new View.OnTouchListener() {	//버튼 터치시 이벤트
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) // 버튼을 누르고 있을 때
                    about_bt.setBackgroundResource(android.R.drawable.ic_dialog_info);
                if(event.getAction() == MotionEvent.ACTION_UP){ //버튼에서 손을 떼었을 때
                    if(islike == false) {
                        about_bt.setBackgroundResource(android.R.drawable.alert_dark_frame);
                        Toast.makeText(RestActivity.this, "좋아요.", Toast.LENGTH_SHORT).show();
                        islike = true;
                    }
                    else {
                        about_bt.setBackgroundResource(android.R.drawable.alert_light_frame);
                        Toast.makeText(RestActivity.this, "좋아요 취소.", Toast.LENGTH_SHORT).show();
                        islike = false;
                    }
                }
                return false;
            }
        });
    }

    //뒤로가기
    @Override
    public void onBackPressed() { // AR로부터 온 화면인지, 지도에서 온 화면인지...
        if (isFromAR) startActivity(new Intent(this, ARActivity.class));
        else startActivity(new Intent(this, GoogleMapsActivity.class));
        finish();
    }

//    public void getPlaceInformation(String id) {
//        JsonObject obj = null;
//        try { //place의 정보를 받을 수 있는 메소드
//            String result = new DetailPlaceTask(this).execute(id).get();
////            JsonParser parser = new JsonParser();
////            System.out.println(result);
////            obj = (JsonObject) parser.parse(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        JsonObject object = (JsonObject) obj.get("result");
//        JsonElement phone = object.get("formatted_phone_number");
//        JsonObject opening_hours = (JsonObject) object.get("opening_hours");
//        if (opening_hours != null || opening_hours.has("weekday_text")) {
//            JsonArray weekday_text = (JsonArray) opening_hours.get("weekday_text");
//            OpeningHour.setText("");
//            if (weekday_text != null) {
//                for (int i = 0; i < weekday_text.size(); i++) {
//                    OpeningHour.append(weekday_text.get(i).getAsString());
//                }
//            }
//        }
//        PhoneView.setText(phone.getAsString());

    // saveDB(id, title, address, pos, null, OpeningHour.getText().toString(), phone.getAsString());
//    }

    public void getPlace(String id) { //DB에 데이터가 있는지 확인 // 없을경우 REST API 실행, 있을경우 DB 불러옴
        try {
            String result = new PlaceTask(this).execute(id).get();
            Log.i("GetPlace", result);
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);
            JsonElement name = obj.get("rest_name"); // 레스토랑 이름
            JsonElement addr = obj.get("rest_address"); // 레스토랑 주소
            JsonElement text = obj.get("rest_text"); // 레스토랑 설명
            JsonElement time = obj.get("rest_time"); // 레스토랑 오픈 시간
            JsonElement phone = obj.get("rest_phone"); // 레스토랑 전화번호

            Place_nameView.setText(name.getAsString());
            AddressView.setText(addr.getAsString());
            OpeningHour.setText(time.getAsString());
            PhoneView.setText(phone.getAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0 && listLockListView == false)
        {
            Log.i("list", "Loading next items");
            listadapter.addItem((float) 2.5, "kjy","내용") ;
            //addItems(1);
        }
    }

//    public void saveDB(String google_id, String restName, String restAddress, LatLng restPoint, String restText, String restTime, String restPhone) {
//        try {
//            String result = new PlaceWriteTask(this)
//                    .execute(google_id, restName, restAddress, Double.toString(restPoint.latitude), Double.toString(restPoint.longitude),
//                            restText, restTime, restPhone)
//                    .get();
//            Log.i("TAGsaveDB", result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}



