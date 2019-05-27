package kr.co.area.hashtag.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONObject;

import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.asyncTask.LoginTask;

public class SplashActivity extends Activity implements LocationListener { // 로고를 띄우면서, 주변의 식당 정보를 미리 받습니다
    Activity activity;
    final static String TAG = "SplashActivity";
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;
    private LocationManager locationManager;
    private Location location;
    boolean isNetworkEnabled;
    boolean isGPSEnabled;
    boolean isLogin;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

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
        System.out.println("ID : " + id);
        System.out.println("PWD : " + pwd);
        if (id != null && pwd != null) {
            try {
                String result = new LoginTask(activity).execute(id, pwd).get();
                JSONObject jObject = new JSONObject(result);
                String state = jObject.getString("result");
                if (state.equals("success") || state.equals("already login")) {
                    System.out.println("로그인됬습니다.");
                    isLogin = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!isLogin) {
            System.out.println("청소합니다~");
            SharedPreferences.Editor editor = auto.edit(); // 로그인이 안된 경우 깔끔히 청소
            editor.clear();
            editor.commit();
        }
    }

    protected void initLocationService(){
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            // Get GPS and network status
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    protected void getNearRest(){
        initLocationService();
        Intent intent;
        if(isLogin) intent = new Intent(this, HomeActivity.class);
        else intent = new Intent(this, LoginActivity.class);
        intent.putExtra("lat", location.getLatitude());
        intent.putExtra("lng", location.getLongitude());
        startActivity(intent);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    } // location 갱신

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
