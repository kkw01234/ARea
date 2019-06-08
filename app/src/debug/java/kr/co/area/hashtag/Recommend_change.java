package kr.co.area.hashtag;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import kr.co.area.hashtag.recommend.Recommendlist_adapter;
import kr.co.area.hashtag.recommend.Recommendlist_item;
import kr.co.area.hashtag.recommendation_path.Map_recommend;
import kr.co.area.hashtag.recommendation_path.WriteRecommendPathActivity;

public class Recommend_change extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_change);

        ListView listview ;
        Recommendlist_adapter adapter;

        ImageButton rec1 = (ImageButton) findViewById(R.id.favorite1);
        rec1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapRcommendAction();
            }
        });
        Button writePathButton = findViewById(R.id.writepathbuttion);
        Button mypick = (Button)findViewById(R.id.Mypick);
        mypick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(
                        getApplicationContext(), Recommend_write.class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어가기
            }
        });

        //작성버튼으로 넘어가는 곳
        writePathButton.setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), WriteRecommendPathActivity.class);
            startActivity(intent);
        });

        // Adapter 생성
        adapter = new Recommendlist_adapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);



        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_looks_one_black_24dp),
                "행궁동 카페투어", "#루프탑 #디저트 #파스타") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_looks_two_black_24dp),
                "광교 데이트코스", "#애견카페 #보드게임 #영화") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_looks_3_black_24dp),
                "한식투어", "#동대문 떡볶이 #비빔밥 ") ;
        // 네 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_looks_4_black_24dp),
                "모듬곱창", "#강남 #대창 #신촌 #화사");
        // 다섯 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_looks_5_black_24dp),
                "보드게임카페", "#서울 #홍대 ");
        // 여섯 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_looks_6_black_24dp),
                "24시카페", "#아주대 #탐탐 ");


        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                Recommendlist_item item = (Recommendlist_item) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;

                //intent
                Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
                startActivity(intent);


            }
        }) ;

    }

    public void MapRcommendAction(){
        Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
        startActivity(intent);
    }
}