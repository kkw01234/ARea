package kr.co.area.hashtag.Recommend;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.Main.Mypage;
import kr.co.area.hashtag.R;

public class Recommendlist_2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendlist_2);
        String title = "";
        String address="";
        String id ="";
        LatLng pos = null;

        Bundle extra = getIntent().getExtras();

        if(extra == null){
            title="error";

        }else{
            title = extra.getString("title");
            //address = extra.getString("address");
            id=extra.getString("id");
            pos = (LatLng)extra.getParcelable("pos");
        }

        TextView Place_nameView = (TextView) findViewById(R.id.place_name);
        TextView AddressView = (TextView) findViewById(R.id.place_address);
        TextView OpeningHour = (TextView) findViewById(R.id.place_time);
        Place_nameView.setText(title);
        AddressView.setText(address);
        //Place p = getPlaceInformation(id,pos);
        //OpeningHour.setText(p.getOpeningHours().getWeekdayText().get(0));


    }
    /*
    public Place getPlaceInformation(String id,LatLng pos){//place의 정보를 받을 수 있는 메소드
        Place place = null;

        Places.initialize(getApplicationContext(),"AIzaSyDGUj-frLFa_pp5Jer5IKWUfRv1tQ-mrJI");
        PlacesClient places = Places.createClient(getApplicationContext());
        List<Place.Field> fields = new ArrayList<>();
        CancellationToken cancellationToken = new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        };

        FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(id,fields).setCancellationToken(cancellationToken).build();
        places.fetchPlace(fetchPlaceRequest);
        FetchPlaceResponse fetchPlaceResponse = FetchPlaceResponse.

        if(Places.isInitialized()) {
            place = Place.builder().setId(id).build();
            System.out.println(place.getId());
            System.out.println(place.getName());
        }
        else
            Log.i("헤헤헤헤헤","키 값을 확인하세요");

        return place;
    }
    */
}



