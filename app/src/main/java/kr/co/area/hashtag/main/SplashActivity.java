package kr.co.area.hashtag.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONObject;

import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.asyncTask.LoginTask;

public class SplashActivity extends Activity { // 로고를 띄우면서, 주변의 식당 정보를 미리 받습니다
    Activity activity;
    final static String TAG = "SplashActivity";
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;
    boolean isLogin;
    double lat;
    double lng;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        tryLogin();
        requestLocationPermission();
    }

    protected void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)  // 권한 없는 경우
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        else requestCameraPermission();
    }

    protected void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)  // 권한 없는 경우
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        else getNearRest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestCameraPermission();
                break;
            case REQUEST_CAMERA_PERMISSIONS_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getNearRest();
                break;
        }
    }

    protected void tryLogin() {
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String id = auto.getString("autoId", null);
        String pwd = auto.getString("autoPwd", null);
        if (id != null && pwd != null) {
            try {
                String result = new LoginTask(activity).execute(id, pwd).get();
                JSONObject jObject = new JSONObject(result);
                String state = jObject.getString("result");
                if (state.equals("success") || state.equals("already login")) isLogin = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!isLogin) {
            SharedPreferences.Editor editor = auto.edit(); // 로그인이 안된 경우 깔끔히 청소
            editor.clear();
            editor.commit();
        }
    }

    protected void getLocation() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GpsInfo gps = new GpsInfo(this);
        lat = gps.getLatitude();
        lng = gps.getLongitude();
    }

    protected void getNearRest() {
        getLocation();
        Intent intent;
        if (isLogin) intent = new Intent(this, HomeActivity.class);
        else intent = new Intent(this, LoginActivity.class);
        intent.putExtra("lat", 37.56);
        intent.putExtra("lng", 126.97);
//        intent.putExtra("lat", lat);
//        intent.putExtra("lng", lng);
        startActivity(intent);
        finish();
    }
}
