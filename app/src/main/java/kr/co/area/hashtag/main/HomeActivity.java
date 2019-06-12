package kr.co.area.hashtag.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import kr.co.area.hashtag.myPage.MyFavoriteListViewAdapter;
import kr.co.area.hashtag.recommendation_path.RecommendPathMainActivity;
import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.myPage.MypageActivity;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, PlacesListener {

    private static final String TAG = "HomeActivity";
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi, posInfo;
    private ImageView profile, goMyPageImg;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, map_button, AR_button;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    // GPSTracker class
    private GpsInfo gps;
    private ImageView refreshImage;
    double lat, lng;
    LatLng currentPosition;


    private ListView mListView;
    private MyFavoriteListViewAdapter adapter;
    private int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        count = 0;
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

        mListView = findViewById(R.id.morelist);
        mListView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // Adapter 생성
        adapter = new MyFavoriteListViewAdapter(this);

        // 리스트뷰 참조 및 Adapter달기
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RestActivity.class);
                ListViewItem item = (ListViewItem) adapter.getItem(position);
                intent.putExtra("id", item.getPlaceId());
                intent.putExtra("From", "HOME");
                startActivity(intent);
            }
        });

        callPermission();  // 권한 요청

        Intent intent = getIntent();
        posInfo = findViewById(R.id.pos_info);
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        if (lat == 0 && lng == 0) position(); // 값을 받아오지 못한 경우
        currentPosition = new LatLng(lat, lng);
        posInfo.setText(getCurrentAddress(currentPosition).get(0).getAddressLine(0));

        if(currentPosition != null)
            showPlaceInformation(currentPosition);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = findViewById(R.id.fab);
        map_button = findViewById(R.id.map_Button);
        AR_button = findViewById(R.id.AR_Button);

        fab.setOnClickListener(this);
        map_button.setOnClickListener(this);
        AR_button.setOnClickListener(this);

        refreshImage = findViewById(R.id.refreshImage);
        refreshImage.setOnClickListener((v) -> {
            callPermission();
            clickRefresh();
        });

    }
    //사용자의 위치 수신

    @Override
    public void onResume(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (isFabOpen) {
            map_button.startAnimation(fab_close);
            AR_button.startAnimation(fab_close);
            map_button.setClickable(false);
            AR_button.setClickable(false);
            isFabOpen = false;
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFabOpen) {
            map_button.startAnimation(fab_close);
            AR_button.startAnimation(fab_close);
            map_button.setClickable(false);
            AR_button.setClickable(false);
            isFabOpen = false;
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                finish();
            } else {
                backPressedTime = tempTime;
            }
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.map_Button:
                anim();
                startActivity(new Intent(this, GoogleMapsActivity.class));
                break;
            case R.id.AR_Button:
                anim();
                startActivity(new Intent(this, ARActivity.class));
                break;
        }
    }

    public void anim() {

        if (isFabOpen) {
            map_button.startAnimation(fab_close);
            AR_button.startAnimation(fab_close);
            map_button.setClickable(false);
            AR_button.setClickable(false);
            isFabOpen = false;
        } else {
            map_button.startAnimation(fab_open);
            AR_button.startAnimation(fab_open);
            map_button.setClickable(true);
            AR_button.setClickable(true);
            isFabOpen = true;
        }
    }


    private void clickRefresh(){
        Toast.makeText(this, "주소를 새로 받습니다.", Toast.LENGTH_LONG).show();
        position();
        currentPosition = new LatLng(lat, lng);
        posInfo.setText(getCurrentAddress(currentPosition).get(0).getAddressLine(0));
        showPlaceInformation(currentPosition);
    }

    public void position() {
        // 권한 요청을 해야 함
        if (!isPermission) {
            callPermission();
            return;
        }

        gps = new GpsInfo(HomeActivity.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
    }

    //현재위치
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    public void getPlace(String id) { //DB에 데이터가 있는지 확인 // 없을경우 REST API 실행, 있을경우 DB 불러옴
        try {
            String result = new PlaceTask(this).execute(id).get();
            Log.i("GetPlace", result);

            JSONObject jsonObject = new JSONObject(result);
            String img = jsonObject.getString("img");
            String rest_name = jsonObject.getString("rest_name");
            String address = jsonObject.getString("address");
            String score = jsonObject.getString("score");
            String rest_id = jsonObject.getString("rest_id");

            adapter.addItem(img, rest_id, rest_name, address, score);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Address> getCurrentAddress(LatLng latlng) { //주소

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1); //위치를 주소로 바꿔줌
            //geocoder.getFromLocationName() -> 주소를 위치로

        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return null;
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return null;

        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return null;

        } else {
            //Address address = addresses.get(0);
            return addresses;
        }

    }

    @Override
    public void onPlacesFailure(PlacesException e) {
    }

    @Override
    public void onPlacesStart() {
        adapter.clearList();
    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(() -> {
            if (places == null || places.size() == 0 || count >= 10)
                return;
            for (Place place : places) {
                if(count++ >= 10) return;
                getPlace(place.getPlaceId());
            }
        });
    }

    @Override
    public void onPlacesFinished() {
    }

    public void showPlaceInformation(LatLng location) {
        new NRPlaces.Builder()
                .listener(this)
                .key(getResources().getString(R.string.google_maps_key))
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(200) //200 미터 내에서 검색
                .type(PlaceType.RESTAURANT) //음식점
                .build()
                .execute();
    }
}