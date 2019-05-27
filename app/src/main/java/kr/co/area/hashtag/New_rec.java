package kr.co.area.hashtag;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import kr.co.area.hashtag.write.ListViewAdapter;

public class New_rec extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rec);


        ListView listview ;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

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

    }
}
