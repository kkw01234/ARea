package kr.co.area.hashtag.recommendation_path;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.libraries.places.compat.Place;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.main.RestActivity;
import kr.co.area.hashtag.utils.PathPlace;

public class WriteRecommendPathActivity extends AppCompatActivity {


    public static ArrayList<PathPlace> placeList = new ArrayList<>();
    public static LinearLayout layout = null;
    private Button writebuttion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_path_write);

        ImageButton loadMap = findViewById(R.id.loadmap);
        layout = (LinearLayout) findViewById(R.id.writescroll);
        writebuttion = findViewById(R.id.writepathbuttion);
        loadMap.setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), WritePathMapActivity.class);
            if(placeList.size() != 0)
                intent.putExtra("list", placeList);
            startActivity(intent);
        });

        Intent intent = getIntent();
        if(intent!=null){
            getPlaceIntent(intent);
        }

        writebuttion.setOnClickListener((v)-> {

        });



    }


    //인텐트 값 저장

    private void getPlaceIntent(Intent intent){
        LinearLayout.LayoutParams layOutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
        try {
            String id = intent.getStringExtra("id");
            String name = intent.getStringExtra("name");
            Double latitude = intent.getDoubleExtra("latitude",0);
            Double longitude = intent.getDoubleExtra("longitude", 0);
            //String placeType = intent.getStringExtra("placeType");

            if(name == null)
                return;

            placeList.add(new PathPlace(id, name, null,latitude,longitude));
            for (int i=0;i<placeList.size();i++){
                TextView textView = new TextView(layout.getContext());
                textView.setText((i+1)+". "+placeList.get(i).name);
                textView.setLayoutParams(layOutParams);
                textView.setTextSize(23);
                PathPlace place;
                textView.setOnClickListener((v)->{
                    TextView view = (TextView) v;
                    String text = ((TextView)v).getText().toString();
                    int io = text.indexOf(".");
                    String str = text.substring(0,1);
                    System.out.println(str);
                    PathPlace pathPlace = placeList.get(Integer.parseInt(str)-1);
                    System.out.println(pathPlace.placeType);
                    //Intent intent1 = new Intent(getApplicationContext(), RestActivity.class);
                    //intent1.putExtra("id",);
                });
                layout.addView(textView);
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }


}





