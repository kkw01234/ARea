package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import kr.co.area.hashtag.R;

public class ReviewpageActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    Bitmap drawable;
    ListView reviewlist;

    private LayoutInflater listInflater;
    private boolean listLockListView;
    private reviewListViewAdapter listadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        String restname = intent.getStringExtra("name");
        TextView rtname = findViewById(R.id.reviewrestname);
        rtname.setText(restname);

        reviewlist = findViewById(R.id.review);
        listLockListView = true;

//        // 푸터를 등록. setAdapter 이전에 해야함.
//        listInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        reviewlist.addFooterView(listInflater.inflate(R.layout.listview_footer, null));

        // 스크롤 리스너 등록
        reviewlist.setOnScrollListener(this);

        // Adapter 생성
        listadapter = new reviewListViewAdapter() ;

        listadapter.addItem(drawable,(float) 2.5, "kjy","내용") ;
        listadapter.addItem(drawable,(float) 2.5, "kjy","내용") ;
        listadapter.addItem(drawable,(float) 2.5, "kjy","내용") ;
        reviewlist.setAdapter(listadapter);

    }

    //뒤로가기
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        int count = totalItemCount - visibleItemCount;

        if (firstVisibleItem >= count && totalItemCount != 0 && listLockListView == false) {
            Log.i("list", "Loading next items");
            listadapter.addItem(drawable,(float) 2.5, "kjy","내용") ;
            reviewlist.setAdapter(listadapter);
            listLockListView = true;
        }

    }
}
