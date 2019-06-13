package kr.co.area.hashtag.recommendation_path;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.GetOneRecTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MyFavoriteListViewAdapter;
import kr.co.area.hashtag.myPage.MypageActivity;

public class MapRecommendActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MapRecommendActivity";
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg;
    private Button writeButton;

    //자세한 정보를 나타내느
    // 구글 맵 참조변수 생성
    GoogleMap mMap;

    private String id;
    private TextView titleView, contentView;
    private ListView listView;
    private MyFavoriteListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_recommend);
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
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.circleCropTransform()).into(profile);
        profile.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
        });
        goMyPageImg.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
        });
        // related Navigation


        // SupportMapFragment을 통해 레이아웃에 만든 fragment의 ID를 참조하고 구글맵을 호출한다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // getMapAsync must be called on the main thread.


        // BitmapDescriptorFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());

        titleView = findViewById(R.id.recTitle);
        contentView = findViewById(R.id.informRec);
        listView = findViewById(R.id.recList);
        adapter = new MyFavoriteListViewAdapter(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("rec_id");
        getInform();
    }

    private void getInform() {
        try { // 정보 받기
            String result = new GetOneRecTask(this).execute(id).get();
            System.out.println(result);
            JSONObject jsonObject = new JSONObject(result);
            String title = jsonObject.getString("title");
            String content = jsonObject.getString("content");
            // adapter.addItem(img, rest_name, content, rate, date);
            JSONArray routes = new JSONArray(jsonObject.getJSONArray("route"));
            for(int i = 0 ; i < routes.length() ; ++i){
                JSONObject route = routes.getJSONObject(i);
            }
            titleView.setText(title);
            contentView.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // 구글 맵 객체를 불러온다.
        mMap = googleMap;

        // 서울에 대한 위치 설정
        LatLng seoul = new LatLng(37.5335402, 127.00864849999993);
        LatLng seoul2 = new LatLng(37.5315144, 127.0055198);
        LatLng seoul3 = new LatLng(37.5386494, 127.00208229999998);
        LatLng seoul4 = new LatLng(37.5425156, 127.00245990000007);

        // 구글 맵에 표시할 마커에 대한 옵션 설정
        MarkerOptions makerOptions = new MarkerOptions();
        MarkerOptions makerOptions2 = new MarkerOptions();
        MarkerOptions makerOptions3 = new MarkerOptions();
        MarkerOptions makerOptions4 = new MarkerOptions();

        makerOptions
                .position(seoul)
                .title("오마일");

        makerOptions2
                .position(seoul2)
                .title("한방통닭");
        makerOptions3
                .position(seoul3)
                .title("패션5");

        makerOptions4
                .position(seoul4)
                .title("오세영식당");

        // 마커를 생성한다.
        mMap.addMarker(makerOptions);
        mMap.addMarker(makerOptions2);
        mMap.addMarker(makerOptions3);
        mMap.addMarker(makerOptions4);

        //마커클릭 이벤트 처리
        mMap.setOnMarkerClickListener(this);

        //카메라를 마커 위치로 옮긴다. // 줌 설정했음
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul2, 13));

        //========================================

        //폴리라인 추가
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(37.5335402, 127.00864849999993), new LatLng(37.5315144, 127.0055198))
                .add(new LatLng(37.5315144, 127.0055198), new LatLng(37.5386494, 127.00208229999998))
                .add(new LatLng(37.5386494, 127.00208229999998), new LatLng(37.5425156, 127.00245990000007))
                .width(5)
                .color(Color.RED));
    }


    //마커클릭시 세부정보로 넘어가는 부분 구현해야함
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.ar_search:
                startActivity(new Intent(this, ARActivity.class));
                break;
            case R.id.map_search:
                startActivity(new Intent(this, GoogleMapsActivity.class));
                break;
            case R.id.rec_path:
                startActivity(new Intent(this, RecommendPathMainActivity.class));
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


}


