package kr.co.area.hashtag.Recommend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import kr.co.area.hashtag.Main.Mypage;
import kr.co.area.hashtag.R;

public class Recommendlist_2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendlist_2);
        String title = "";
        String address="";

        Bundle extra = getIntent().getExtras();

        if(extra == null){
            title="error";

        }else{
            title = extra.getString("title");
            address = extra.getString("address");
        }

        TextView Place_nameView = (TextView) findViewById(R.id.place_name);
        TextView AddressView = (TextView) findViewById(R.id.place_address);
        Place_nameView.setText(title);
        AddressView.setText(address);

    }
}



