package kr.co.area.hashtag.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.etc.Request_Code;

public class GoogleMapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static final LatLng DEFAULT_LOCATION = new LatLng(37,127); //위치 Default 값
    private static final String TAG="Google Maps"; // Log띄울때 사용할 TAG;

    private static final int UPDATE_INTERVAL_MS = 15000; //업데이트 시간
    private static final int FASTEST_UPDATE_INTERVAL_MS = 15000;

    private final static int MAXENTRIES =5; //근처 5개 제한 둘때
    private String[] LikelyPlaceNames = null;
    private String[] LikelyAddresses = null;
    private String[] LikelyAttributions = null;
    private LatLng[] LikelyLatLngs  = null; //추후 클래스로 만들자

    private GoogleMap mMap;
    private Marker currentMarker = null;
    private GoogleApiClient googleApiClient = null;
    private String provider =null;
    private SupportMapFragment mapFragment;


    public void setCurrentLocation(Location location,String markerTitle,String markerSnippet){//현재위치 핀찍기
        if(currentMarker != null) currentMarker.remove();
        if(location != null){
            LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = this.mMap.addMarker(markerOptions);
            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = this.mMap.addMarker(markerOptions);

        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment); //이게 아직 오류나는 코드
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Location location = new Location("");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                setCurrentLocation(location,place.getName().toString(),place.getAddress().toString());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */




    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(googleApiClient != null){
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
            googleApiClient.disconnect();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setCurrentLocation(null,"위치정보를를 가져올수 없음","위치 퍼미션 GPS 활성 여부 확인");
        googleMap.getUiSettings().setCompassEnabled(true); //나침반 설정
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //권한
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION);
            if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED){
                //권한 없을 때
                //재요청
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_Code.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }else{//권한 유효
                if(googleApiClient == null){
                    buildGoogleApiClient();
                }
                if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    googleMap.setMyLocationEnabled(true);
                }
            }
        }else{ //23버전 이하는 권한 검사 필요 없음
            if(googleApiClient == null){
                buildGoogleApiClient();
            }
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this,this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        /*

         */
        googleApiClient.connect();
    }
    public boolean checkLocationServicesStatus(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) { //연결이 될경우
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!checkLocationServicesStatus(locationManager)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                    "위치 설정을 수정하십시오.");
            builder.setCancelable(true);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent callGPSSettingIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, Request_Code.GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }

        //LocationListner쓰자
        GPSLocationListner gpsLocationManager=null;
        int gpsCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int networkCheck = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //23버전 이상
        if (gpsCheck == PackageManager.PERMISSION_DENIED || networkCheck == PackageManager.PERMISSION_DENIED) {//권한이 없을때
            Toast.makeText(getApplicationContext(), "권한 승인이 필요합니다.", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(getParent(), Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getParent(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        Request_Code.PERMISSIONS_REQUEST_ACCESS_ALL_LOCATION);
            }
        } else { //권한 승인
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE); //정확도
            criteria.setPowerRequirement(Criteria.POWER_LOW); //전원 소비량
            criteria.setAltitudeRequired(true); //고도, 높이값 얻어올지말지 결정
            criteria.setBearingRequired(false); // 기본 정보 (방위 방향)
            criteria.setSpeedRequired(false); // 속도
            criteria.setCostAllowed(true);//위치정보 얻어오는데 들어가는 금전적비용
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(criteria, true);//한번만 받아오게되는게 문제네
            gpsLocationManager = new GPSLocationListner(locationManager, provider);
            Location location = locationManager.getLastKnownLocation(provider);
            if (!locationManager.isProviderEnabled(provider) && location != null) {
                locationManager.requestLocationUpdates(provider, 0, 0, gpsLocationManager);
                //setCurrentLocation(location, "현재 위치", String.format("%.2f %.2f", location.getLatitude(), location.getLongitude()));
            } else {
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 0, 0, gpsLocationManager);

            }
        }

        }else{//23버전 이하

        }



    }

    @Override
    public void onConnectionSuspended(int i) {
        if ( i ==  CAUSE_NETWORK_LOST )
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (i == CAUSE_SERVICE_DISCONNECTED )
            Log.e(TAG,"onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class GPSLocationListner implements LocationListener {
        private LocationManager locationManager;
        private long startTime = -1;
        private Location beforeLocation;

        public GPSLocationListner() {

        }

        public GPSLocationListner(LocationManager locationManager,String provider) {
            this.locationManager = locationManager;
            beforeLocation=new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged call..");
            if(startTime == -1){
                startTime =location.getTime();
            }
            float distance[] = new float[1];
            Log.i(TAG,Double.toString(location.getLatitude()));
            Location.distanceBetween(beforeLocation.getLatitude(),beforeLocation.getLongitude(),location.getLatitude(),location.getLongitude(),distance);
            long delay = location.getTime()-startTime;
            double speed = distance[0]/delay;
            double speedKMH = speed*3600;
            setCurrentLocation(location, "현재 위치", String.format("%.2f %.2f", location.getLatitude(), location.getLongitude())); //현재위치 계속 찍어주는거
            beforeLocation = location;

           // searchCurrentPlaces();


        }

        private void searchCurrentPlaces() { //아직 미사용
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(googleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    int i = 0;
                    LikelyPlaceNames = new String[MAXENTRIES];
                    LikelyAddresses = new String[MAXENTRIES];
                    LikelyAttributions = new String[MAXENTRIES];
                    LikelyLatLngs = new LatLng[MAXENTRIES];

                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        LikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                        LikelyAddresses[i] = (String) placeLikelihood.getPlace().getAddress();
                        LikelyAttributions[i] = (String) placeLikelihood.getPlace().getAttributions();
                        LikelyLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;
                        if (i > MAXENTRIES - 1) {
                            break;
                        }
                    }

                    placeLikelihoods.release();

                    Location location = new Location("");
                    location.setLatitude(LikelyLatLngs[0].latitude);
                    location.setLongitude(LikelyLatLngs[0].longitude);

                    setCurrentLocation(location, LikelyPlaceNames[0], LikelyAddresses[0]);
                }
            });

        }

        public void realtimeGPS() {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
