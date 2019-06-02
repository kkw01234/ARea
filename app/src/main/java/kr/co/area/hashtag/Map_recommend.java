package kr.co.area.hashtag;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.recommend.RecommendActivity;

public class Map_recommend extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // 구글 맵 참조변수 생성
    GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_recommend);

        // SupportMapFragment을 통해 레이아웃에 만든 fragment의 ID를 참조하고 구글맵을 호출한다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // getMapAsync must be called on the main thread.


        // BitmapDescriptorFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());


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
        startActivity(new Intent(Map_recommend.this, Recommend_change.class));
        finish();
    }


}


