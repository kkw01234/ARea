package kr.co.area.hashtag.ar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;


import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.main.RestActivity;

import static android.hardware.SensorManager.AXIS_MINUS_X;
import static android.hardware.SensorManager.AXIS_MINUS_Y;
import static android.hardware.SensorManager.AXIS_X;
import static android.hardware.SensorManager.AXIS_Y;
import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;
import static android.hardware.SensorManager.getOrientation;
import static android.hardware.SensorManager.getRotationMatrixFromVector;
import static android.hardware.SensorManager.remapCoordinateSystem;
import static android.view.Surface.ROTATION_180;
import static android.view.Surface.ROTATION_270;
import static android.view.Surface.ROTATION_90;

public class ARActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    final static String TAG = "ARActivity";
    private SurfaceView surfaceView;
    private FrameLayout cameraContainerLayout;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;
    private EditText meterText;
    private Button meterChange;
    private int meter;

    private SensorManager sensorManager;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private final static int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    public Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        cameraContainerLayout = findViewById(R.id.camera_container_layout);
        surfaceView = findViewById(R.id.surface_view);
        meter = 30;
        arOverlayView = new AROverlayView(this, meter);
        meterText = findViewById(R.id.inputMeter);
        meterChange = findViewById(R.id.freshButton);
        arOverlayView.setOnTouchListener((v, e) -> { // 터치 이벤트 view에 전달
            switch (e.getAction()) {
                case MotionEvent.ACTION_UP:
                    arOverlayView.touch(e.getX(), e.getY());
                    break;
            }
            return true;
        });
        meterChange.setOnClickListener((v) -> {
            meter = Integer.parseInt(meterText.getText().toString());
            arOverlayView.setMeter(meter);
            Toast.makeText(this, meter + "M 내부의 식당 검색합니다.", Toast.LENGTH_LONG).show();
        });
        requestLocationPermission();
    }

    @Override
    public void onResume() {
        initCamera();
        arOverlayView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        releaseCamera();
        arOverlayView.pause();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        releaseCamera();
        finish();
    }

    public void requestLocationPermission() { // 위치에 대한 Permission 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)  // 권한 없는 경우
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        else {
            initLocationService();
            requestCameraPermission();
        }

    }

    public void requestCameraPermission() { // 카메라에 대한 Permission 얻기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)  // 권한 없는 경우
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        else {
            initARCameraView();
            registerSensors();
            initAROverlayView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocationService();
                    requestCameraPermission();
                }
                return;
            case REQUEST_CAMERA_PERMISSIONS_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initARCameraView();
                    registerSensors();
                    initAROverlayView();
                }
                return;
        }
    }

    public void initAROverlayView() {
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    public void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(this, surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open();
                camera.startPreview();
                arCamera.setCamera(camera);
            } catch (RuntimeException ex) {
                Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            arCamera.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    private void registerSensors() { // sensor manager로 x, y, z축 이동 감지하기 위해 Listener 등록
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { // 센서 변화시
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) { // rotation vector는 여러 센서를 합해서 나온 값이라고 함
            float[] rotationMatrix = new float[16];
            getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            float[] projectionMatrix = arCamera.getProjectionMatrix();
            float[] rotatedProjectionMatrix = new float[16];
            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrix, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);
        }
    }

    public void showCalibrate() {
        Toast.makeText(this, "정확도를 위해 팔을 8자로 흔들어주세요", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                showCalibrate();
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                showCalibrate();
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                break;
        }
    }

    private void initLocationService() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    updateLatestLocation();
                }
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateLatestLocation();
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());

        }
    }

    private void updateLatestLocation() { // location을 AROverlayView에 전달
        if (arOverlayView != null && location != null) arOverlayView.updateCurrentLocation(location);
    }

    public void requestRestInfo(ARTouchPoint tp) {
        Intent intent = new Intent(this, RestActivity.class);
        intent.putExtra("id", tp.restID);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        updateLatestLocation();
    } // location 갱신될 때

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
