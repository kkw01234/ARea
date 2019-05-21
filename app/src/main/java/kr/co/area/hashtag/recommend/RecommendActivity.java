package kr.co.area.hashtag.recommend;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.newRec_Fragment;


public class RecommendActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Button1, Button2;
    private Fragment fr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Button1 = (Button)findViewById(R.id.button1);
        Button2 = (Button)findViewById(R.id.button2);

        Button1.setOnClickListener(this);
        Button2.setOnClickListener(this);


        if(findViewById(R.id.fragment_container) != null){
            //이전 상태에서 복구되는 경우 프래그먼트 새로 생성할 필요 없음
            if(savedInstanceState != null) return;

            newRec_Fragment firstFragment = new newRec_Fragment();
            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,firstFragment).commit();

        }
//        ImageButton rec1 = (ImageButton) findViewById(R.id.rec1);
//
//        rec1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    detailRecommentAction();
//            }
//        });



    }

    public void onClick(View v){

        switch (v.getId()) {
            case R.id.button1:
                fr= new newRec_Fragment();
                selectFragment(fr);
                break;

            case R.id.button2:
                fr= new topRec_Fragment();
                selectFragment(fr);
                break;
        }

    }

    public void selectFragment(Fragment fr){

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fr);
        fragmentTransaction.commit();
    }

    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(RecommendActivity.this, HomeActivity.class));
        finish();
    }

    public void detailRecommentAction(){
        Intent intent = new Intent(getApplicationContext(),DetailRecommendActivity.class);
        startActivity(intent);
    }
}
