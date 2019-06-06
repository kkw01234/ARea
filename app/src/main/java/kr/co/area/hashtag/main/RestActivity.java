package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.GetAllReviewByRestTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MypageActivity;
import kr.co.area.hashtag.recommend.RecommendActivity;
import kr.co.area.hashtag.write.WriteReviewActivity;

public class RestActivity extends AppCompatActivity implements AbsListView.OnScrollListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi, posInfo;
    private ImageView profile, goMyPageImg;


    boolean islike = false;
    TextView Place_nameView, AddressView, Place_Text_View, OpeningHour, PhoneView, dataPoint, myPoint, reviewPoint, updateData;
    ImageView wordcloud;
    ListView reviewlist;
    String id = "";
    String isFrom, restname;
    Button about_bt;

    private LayoutInflater listInflater;
    private boolean listLockListView;
    private reviewListViewAdapter listadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences user = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);

        // 네비게이션 헤더부분
        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.user_name);
        goMyPageImg = headerView.findViewById(R.id.go_mypage_img);
        profile = headerView.findViewById(R.id.profileView);
        userHi.setText(user.getString("userName", "???") + " 님,");
        String userId = user.getString("userId", null);
        String image = "http://118.220.3.71:13565/download_file?category=download_my_image&u_id=" + userId;
        Glide.with(this).load(image).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).into(profile);

        profile.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
            finish();
        });
        goMyPageImg.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
            finish();
        });


        Bundle extra = getIntent().getExtras();

        if (extra == null) {
            id = "error";
        } else {
            id = extra.getString("id");
            isFrom = extra.getString("From");
        }
        Place_nameView = findViewById(R.id.place_name);
        AddressView = findViewById(R.id.place_address);
        OpeningHour = findViewById(R.id.place_time);
        PhoneView = findViewById(R.id.place_phone);
        Place_Text_View = findViewById(R.id.place_price);
        dataPoint = findViewById(R.id.datascorepoint);
        updateData = findViewById(R.id.update);
        getPlace(id);
        wordcloud = findViewById(R.id.wordcloudimg);
        String cloudImg = "http://118.220.3.71:13565/download_file?category=load_wordcloud&u_id=" + userId+"&google_id="+id;
        Glide.with(this).load(cloudImg).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(wordcloud);
        ScrollView reviewscroll = findViewById(R.id.scrollview);
        reviewlist = findViewById(R.id.reviewlist);
        listLockListView = true;

        //날짜
//        TextView date = findViewById(R.id.update);
//        date.setText(+ "기준");

        // 푸터를 등록. setAdapter 이전에 해야함.
        listInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reviewlist.addFooterView(listInflater.inflate(R.layout.moreview_footer, null));

        // 스크롤 리스너 등록
        reviewlist.setOnScrollListener(this);

        // Adapter 생성
        listadapter = new reviewListViewAdapter(this) ;

        getReviews();
        // 리스트뷰 참조 및 Adapter달기
        reviewlist.setAdapter(listadapter);

        //후기 리스트 스크롤
        reviewlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                reviewscroll.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // 후기 더보기
        TextView reviewmore = (TextView) findViewById(R.id.tv_more_footer);
//        reviewmore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listadapter.getCount() < 3){
//                    listadapter.addItem("2019.05.31",drawable,(float) 2.5, "kjy","내용") ;
//                    reviewlist.setAdapter(listadapter);
//                }
//                else {
//                    Intent intent = new Intent(getApplicationContext(), ReviewpageActivity.class);
//                    intent.putExtra("name",restname);
//                    startActivity(intent);
//                }
//
//            }
//        });

        //메뉴 더보기
        TextView menumore = (TextView) findViewById(R.id.place_price_more);
        menumore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenupageActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        Button writebtn = (Button) findViewById(R.id.evaluate_button);
        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wrintent = new Intent(getApplicationContext(), WriteReviewActivity.class);
                wrintent.putExtra("rest", id);
                wrintent.putExtra("name", restname);
                startActivity(wrintent);
            }
        });

        about_bt = (Button) findViewById(R.id.favorite_button);
        about_bt.setOnClickListener(this);	//버튼 터치시 이벤트

    }

    View.OnClickListener headListener = (view) -> {
        switch (view.getId()) {
            case R.id.profileView:
                startActivity(new Intent(activity, MypageActivity.class));
                finish();
                break;

        }
    };

    //뒤로가기
    @Override
    public void onBackPressed() { // AR로부터 온 화면인지, 지도에서 온 화면인지...
        switch (isFrom) {
            case "AR":
                startActivity(new Intent(this, ARActivity.class));
                break;
            case "HOME":
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case "MAP":
                startActivity(new Intent(this, GoogleMapsActivity.class));
                break;
        }
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

    private void getReviews(){
        try {
            String result = new GetAllReviewByRestTask(this).execute(id).get();
            System.out.println(result);
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; ++i) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String date = jsonObject.getString("date");
                String img = jsonObject.getString("img");
                float rate = Float.parseFloat(jsonObject.getString("rate"));
                String name = jsonObject.getString("user_name");
                String text = jsonObject.getString("content");
                listadapter.addItem(date, img, rate, name, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            JsonElement point = obj.get("rest_point");
            JsonElement rest_crawling_date = obj.get("rest_crawling_date");

            Place_nameView.setText(name.getAsString());
            AddressView.setText(addr.getAsString());
            OpeningHour.setText(time.getAsString());
            PhoneView.setText(phone.getAsString());
            Place_Text_View.setText(text.getAsString());
            if (point != null) {
                dataPoint.setText(point.getAsString());
            }
            if(rest_crawling_date != null){
                updateData.setText(rest_crawling_date.getAsString());
            }

            restname = name.getAsString();

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
            //listadapter.addItem("2019.05.31",drawable,(float) 2.5, "kjy","내용") ;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.ar_search:
                startActivity(new Intent(this, ARActivity.class));
                finish();
                break;
            case R.id.map_search:
                startActivity(new Intent(this, GoogleMapsActivity.class));
                finish();
                break;
            case R.id.rec_path:
                startActivity(new Intent(this, RecommendActivity.class));
                break;
            case R.id.logout:
                logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        try {
            new LogoutTask(activity).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.favorite_button :
                if (islike == false){
                    islike = true;
                }
                else
                    islike = false;
                about_bt.setSelected(islike);
                break;
        }
    }
}



