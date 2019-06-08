package kr.co.area.hashtag.recommendation_path;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.libraries.places.compat.ui.SupportPlaceAutocompleteFragment;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.main.RestActivity;
import kr.co.area.hashtag.utils.PathPlace;

public class WritePathMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    //현재 위치가 필요하다면
    ArrayList<Marker> previous_marker = null;
    ArrayList<PathPlace> polylist = null;
    ArrayList<Polyline> polylines = new ArrayList<>();
    GoogleMap googleMap;
    LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97); //서울역

    Marker searchMarker= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_path_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        previous_marker = new ArrayList<>();

        setupAutoCompleteFragment(mapFragment);

        Intent intent = getIntent();
        Serializable seri = intent.getSerializableExtra("list");
        if(seri != null) {
            polylist = (ArrayList<PathPlace>) seri;
            System.out.println(polylist.size());
            for(int i=0;i<polylist.size()-2;i++){
                LatLng src = new LatLng(polylist.get(i).latitude,polylist.get(i).longitude);
                LatLng dest = new LatLng(polylist.get(i+1).latitude,polylist.get(i+1).longitude);

                Polyline line =googleMap.addPolyline(new PolylineOptions().add(
                        new LatLng(src.latitude,src.latitude),
                        new LatLng(dest.latitude,dest.longitude)
                ).width(5).color(Color.RED).geodesic(true));
                polylines.add(line);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION,25);
        googleMap.animateCamera(cameraUpdate);

        googleMap.setOnInfoWindowClickListener((marker) -> {
            Intent intent = new Intent(getBaseContext(), WriteRecommendPathActivity.class);
            Place place = (Place) marker.getTag();
            intent.putExtra("id", place.getId());
            intent.putExtra("name", place.getName());
            //intent.putExtra("placeType",place.getPlaceTypes().get(0));
            intent.putExtra("From", "Map");
            intent.putExtra("latitude", place.getLatLng().latitude);
            intent.putExtra("longitude",place.getLatLng().longitude);
            startActivity(intent);
        });
    }


    //찾은 Place의 마커를 찾는 메소드
    private void setupAutoCompleteFragment(SupportMapFragment mapFragment) {
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(new com.google.android.libraries.places.compat.AutocompleteFilter.Builder().setCountry("KR").build());
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {


            @Override
            public void onPlaceSelected(com.google.android.libraries.places.compat.Place place) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getLatLng());
                markerOptions.title((String) place.getName());
                markerOptions.snippet((String) place.getAddress());
                for (Marker marker : previous_marker) {
                    marker.remove();
                }
                previous_marker.clear();
                if (searchMarker != null)
                    searchMarker.remove();

                searchMarker = googleMap.addMarker(markerOptions);
                searchMarker.setTag(place);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(place.getLatLng());
                googleMap.animateCamera(cameraUpdate, 2000, null);
            }

            @Override
            public void onError(Status status) {
                Log.e("Error", status.getStatusMessage());
            }
        });

    }
}

