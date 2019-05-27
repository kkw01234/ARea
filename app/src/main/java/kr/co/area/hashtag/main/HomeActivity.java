package kr.co.area.hashtag.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.recommend.RecommendActivity;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AbsListView.OnScrollListener, PlacesListener {
    private static final String TAG = "HomeActivity";
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile,homeLogo;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, map_button, AR_button;
    private ListView mListView;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    // GPSTracker class
    private GpsInfo gps;
    double latitude,longitude;
    LatLng currentPosition;
    private GoogleMap mGoogleMap = null;
    private int mcount = 0;

    List<Marker> previous_marker = null;
    List<String> idlist = new ArrayList<>();
    Vector<Layout> layouts = new Vector<>();

    // 스크롤 로딩
    private LayoutInflater mInflater;
    private boolean mLockListView;
    private ListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        SharedPreferences pref1 = getSharedPreferences("image", MODE_PRIVATE);
        String image = pref1.getString("imagestrings", "");
        Bitmap bitmap = StringToBitMap(image);


        callPermission();  // 권한 요청을 해야 함

        position();
        callPermission();  // 권한 요청을 해야 함

        Intent intent = getIntent();
        currentPosition = new LatLng(intent.getDoubleExtra("lat", 0),
                intent.getDoubleExtra("lng", 0));
        // = new LatLng(latitude,longitude);

        showPlaceInformation(currentPosition);

        mListView = (ListView) findViewById(R.id.morelist);
        mLockListView = true;

        // 푸터를 등록. setAdapter 이전에 해야함.
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView.addFooterView(mInflater.inflate(R.layout.listview_footer, null));

        // 스크롤 리스너 등록
        mListView.setOnScrollListener(this);

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        mListView.setAdapter(adapter);

        TextView extext = (TextView) findViewById(R.id.tv_list_footer);
        extext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(idlist.get(mcount));
                String id = idlist.get(mcount++);
                getPlace(id);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("성공");
                Intent intent = new Intent(getBaseContext(), RestActivity.class);
                String id1 = idlist.get(position);

                intent.putExtra("id", id1);
                startActivity(intent);
            }
        });


        //헤더부분
        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.userHi);
        profile = headerView.findViewById(R.id.profilView);
        homeLogo = headerView.findViewById(R.id.LogoBtn);
        userHi.setText(user.getString("userName", "???") + "님\n안녕하세요");

        if (!(image.equals(""))) {
            profile.setImageBitmap(bitmap);
        }
        profile.setOnClickListener(headListener);
        homeLogo.setOnClickListener(headListener);


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        map_button = (FloatingActionButton)findViewById(R.id.map_Button);
        AR_button = (FloatingActionButton)findViewById(R.id.AR_Button);

        fab.setOnClickListener(this);
        map_button.setOnClickListener(this);
        AR_button.setOnClickListener(this);

    }
    //사용자의 위치 수신




    View.OnClickListener headListener = (view) -> {
        switch (view.getId()) {
            case R.id.LogoBtn:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.profilView:
                startActivity(new Intent(activity, MypageActivity.class));
                finish();
                break;

        }
    };

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
            case R.id.kwd_search:
                break;
            case R.id.rec_path:
                startActivity(new Intent(this, RecommendActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(this, MypageActivity.class));
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
                //Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.map_Button:
                anim();
                startActivity(new Intent(this, GoogleMapsActivity.class));
                finish();
                break;
            case R.id.AR_Button:
                anim();
                startActivity(new Intent(this, ARActivity.class));
                finish();
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
        }
        else {
            map_button.startAnimation(fab_open);
            AR_button.startAnimation(fab_open);
            map_button.setClickable(true);
            AR_button.setClickable(true);
            isFabOpen = true;
        }
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

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
    };

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false)
        {
            Log.i("list", "Loading next items");
            String id = idlist.get(mcount++);
            getPlace(id);
            //addItems(1);
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }

    //현재위치
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

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
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    public void getPlace(String id) { //DB에 데이터가 있는지 확인 // 없을경우 REST API 실행, 있을경우 DB 불러옴
        try {
            mLockListView = true;
            String result = new PlaceTask(this).execute(id).get();
            Log.i("GetPlace", result);
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);
            JsonElement str = obj.get("result");
//            if (str.getAsString().equals("fail")) {
//                inDatabase = false;
//                getPlaceInformation(id);
//                getPlace(id);
//                return;
//            }
            JsonElement name = obj.get("rest_name"); // 레스토랑 이름
            JsonElement addr = obj.get("rest_address"); // 레스토랑 주소
            JsonElement text = obj.get("rest_text"); // 레스토랑 설명
            JsonElement time = obj.get("rest_time"); // 레스토랑 오픈 시간
            JsonElement phone = obj.get("rest_phone"); // 레스토랑 전화번호

            adapter.addItem(null, name.getAsString(), addr.getAsString()) ;
            mListView.setAdapter(adapter);
            mLockListView = false;

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

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(() -> {
            if (places == null || places.size() == 0)
                return;

            for (noman.googleplaces.Place place : places) {
                LatLng latLng
                        = new LatLng(place.getLatitude()
                        , place.getLongitude());
                Log.i(TAG, latLng.latitude + " " + latLng.longitude);
                List<Address> address = getCurrentAddress(latLng);
                String markerSnippet = null;
                if (address != null)
                    markerSnippet = address.get(0).getAddressLine(0);
                else
                    markerSnippet = latLng.latitude + " " + latLng.longitude;
                //String altitude = getAltitude(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(place.getName());
                markerOptions.snippet(markerSnippet);

                idlist.add(place.getPlaceId());
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    public void showPlaceInformation(LatLng location) {

        System.out.println(location);

        new NRPlaces.Builder()
                .listener(HomeActivity.this)
                .key(getResources().getString(R.string.google_maps_key))
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(500) //500 미터 내에서 검색
                .type(PlaceType.RESTAURANT) //음식점
                .build()
                .execute();
    }
}