package kr.co.area.hashtag.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.GetAllReviewByRestTask;

public class ReviewpageActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    Bitmap drawable;
    ListView reviewlist;

    private LayoutInflater listInflater;
    private boolean listLockListView;
    private reviewListViewAdapter listadapter;
    private String id, restname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        id = intent.getStringExtra("rest");
        restname = intent.getStringExtra("name");
        TextView rtname = findViewById(R.id.reviewrest_text);
        rtname.setText(restname);

        reviewlist = findViewById(R.id.review);
        listLockListView = true;

//        // 푸터를 등록. setAdapter 이전에 해야함.
//        listInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        reviewlist.addFooterView(listInflater.inflate(R.layout.listview_footer, null));

        // 스크롤 리스너 등록
        reviewlist.setOnScrollListener(this);

        // Adapter 생성
        listadapter = new reviewListViewAdapter(this) ;
        getReviews();
//        listadapter.addItem("2019.05.31",drawable,(float) 2.5, "kjy","내용") ;
//        listadapter.addItem("2019.05.31",drawable,(float) 2.5, "kjy","내용") ;
//        listadapter.addItem("2019.05.31",drawable,(float) 2.5, "kjy","내용") ;
        reviewlist.setAdapter(listadapter);

    }



    private void getReviews(){
        try {
            String result = new GetAllReviewByRestTask(this).execute(id).get();
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; ++i) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String date = jsonObject.getString("date");
                String img = jsonObject.getString("img");
                float rate = Float.parseFloat(jsonObject.getString("rate"));
                String name = jsonObject.getString("user_name");
                String text = jsonObject.getString("content");
                listadapter.addItem(date, img, rate, name, text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//            listadapter.addItem("2019.05.31",drawable,(float) 2.5, "kjy","내용") ;
            reviewlist.setAdapter(listadapter);
            listLockListView = true;
        }

    }
}
