package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.GetAllReviewByRestTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MypageActivity;
import kr.co.area.hashtag.recommend.RecommendActivity;

public class ReviewpageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg;

    ListView reviewlist;

    private reviewListViewAdapter listadapter;
    private String id, restname;
    private TextView reviewCount, rtname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences user = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);

        // 네비게이션 헤더부분
        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.user_name);
        goMyPageImg = headerView.findViewById(R.id.go_mypage_img);
        profile = headerView.findViewById(R.id.profileView);
        userHi.setText(user.getString("userName", "???") + " 님,");
        String userId = user.getString("userId", null);
        String image = "http://118.220.3.71:13565/download_file?category=download_my_image&u_id=" + userId;
        Glide.with(this).load(image).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.circleCropTransform()).into(profile);
        profile.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
            finish();
        });
        goMyPageImg.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
            finish();
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("rest_id");
        restname = intent.getStringExtra("rest_name");
        rtname = findViewById(R.id.reviewrest_text);
        reviewCount = findViewById(R.id.reviewnum);
        rtname.setText(restname);

        reviewlist = findViewById(R.id.review);

        // Adapter 생성
        listadapter = new reviewListViewAdapter(this) ;
        getReviews();
        reviewlist.setAdapter(listadapter);
        reviewlist.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

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
            reviewCount.setText(jsonArray.length() + " 건");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //뒤로가기
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.ar_search:
                startActivity(new Intent(this, ARActivity.class));
                finish();
                break;
            case R.id.map_search:
                startActivity(new Intent(this, GoogleMapsActivity.class));
                finish();
                break;
            case R.id.rec_path:
                startActivity(new Intent(this, RecommendActivity.class));
                break;
            case R.id.logout:
                logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        try {
            new LogoutTask(activity).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
