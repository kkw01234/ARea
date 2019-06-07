package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.FavoriteTask;
import kr.co.area.hashtag.asyncTask.GetAllReviewByRestTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.asyncTask.PlaceTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MypageActivity;
import kr.co.area.hashtag.recommend.RecommendActivity;
import kr.co.area.hashtag.write.WriteReviewActivity;

public class RestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg;


    boolean islike = false;
    TextView Place_nameView, AddressView, Place_Text_View, OpeningHour, PhoneView, dataPoint, myPoint, reviewPoint, updateData, countReview, moreReview, moreMenu;
    ImageView wordcloud, restImage;
    ListView reviewlist;
    RatingBar rating;
    String id = "";
    String restname;
    Button about_btn, writebtn;

    private reviewListViewAdapter listadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
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

        Bundle extra = getIntent().getExtras();

        if (extra == null) {
            id = "error";
        } else {
            id = extra.getString("id");
        }
        writebtn = findViewById(R.id.evaluate_button);
        Place_nameView = findViewById(R.id.place_name);
        AddressView = findViewById(R.id.place_address);
        OpeningHour = findViewById(R.id.place_time);
        PhoneView = findViewById(R.id.place_phone);
        Place_Text_View = findViewById(R.id.place_price);
        dataPoint = findViewById(R.id.datascorepoint);
        updateData = findViewById(R.id.update);
        countReview = findViewById(R.id.countReview);
        moreReview = findViewById(R.id.moreReview);
        moreMenu = findViewById(R.id.place_price_more);
        reviewPoint = findViewById(R.id.reviewscore);
        rating = findViewById(R.id.agstar);
        restImage = findViewById(R.id.rest_image);

        getPlace(id);


        wordcloud = findViewById(R.id.wordcloudimg);
        String cloudImg = "http://118.220.3.71:13565/download_file?category=load_wordcloud&u_id=" + userId + "&google_id=" + id;
        Glide.with(this).load(cloudImg).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(wordcloud);
        reviewlist = findViewById(R.id.reviewList);

        //날짜
//        TextView date = findViewById(R.id.update);
//        date.setText(+ "기준");

        // Adapter 생성
        listadapter = new reviewListViewAdapter(this);

        getReviews();
        // 리스트뷰 참조 및 Adapter달기
        reviewlist.setAdapter(listadapter);
        setListViewHeightBasedOnChildren(reviewlist);

        moreReview.setOnClickListener((v) -> {
            startActivity(new Intent(this, ReviewpageActivity.class).putExtra("rest_id", id).putExtra("rest_name", restname));
        });

        //메뉴 더보기
        moreMenu.setOnClickListener((v) -> {
            startActivity(new Intent(this, MenupageActivity.class).putExtra("rest_id", id));
        });

        writebtn.setOnClickListener((v) -> {
            startActivity(new Intent(getApplicationContext(), WriteReviewActivity.class).putExtra("rest_id", id).putExtra("rest_name", restname));
        });

        about_btn = findViewById(R.id.favorite_button);
        about_btn.setSelected(islike);
        about_btn.setOnClickListener((v) -> {
            try {
                String result = new FavoriteTask(this).execute(id).get();
                JSONObject jsonObject = new JSONObject(result);
                islike = (jsonObject.getString("result").equals("true")) ? true : false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            about_btn.setSelected(islike);
        });    //버튼 터치시 이벤트

    }

    View.OnClickListener headListener = (view) -> {
        switch (view.getId()) {
            case R.id.profileView:
                startActivity(new Intent(activity, MypageActivity.class));
                finish();
                break;

        }
    };

    private void setListViewHeightBasedOnChildren(ListView listView) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < Math.min(listadapter.getCount(), 3); i++) {
            View listItem = listadapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listadapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    //뒤로가기
    @Override
    public void onBackPressed() { // AR로부터 온 화면인지, 지도에서 온 화면인지...
        finish();
    }

    private void getReviews() {
        try {
            String result = new GetAllReviewByRestTask(this).execute(id).get();
            JSONArray jsonArray = new JSONArray(result);
            float sum = 0;
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                float rate = Float.parseFloat(jsonObject.getString("rate"));
                sum += rate;
                if (i >= 3) continue;
                String date = jsonObject.getString("date");
                String img = jsonObject.getString("img");
                String name = jsonObject.getString("user_name");
                String text = jsonObject.getString("content");
                listadapter.addItem(date, img, rate, name, text);
            }
            countReview.setText(jsonArray.length() + "건");
            if (jsonArray.length() > 0) {
                float rate = sum / jsonArray.length();
                rating.setRating(rate);
                reviewPoint.setText((Math.round(rate * 100) / 100.0) + "점");
            } else {
                rating.setRating(0);
                reviewPoint.setText("0점");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlace(String id) { //DB에 데이터가 있는지 확인 // 없을경우 REST API 실행, 있을경우 DB 불러옴
        try {
            String result = new PlaceTask(this).execute(id).get();
            System.out.println(result);
            JSONObject jsonObject = new JSONObject(result);
            String name = jsonObject.getString("rest_name"); // 레스토랑 이름
            String addr = jsonObject.getString("rest_address"); // 레스토랑 주소
            String text = jsonObject.getString("rest_text"); // 레스토랑 메뉴
            if (text.equals("") || text.equals("null")) text = "정보없음";
            String time = jsonObject.getString("rest_time"); // 레스토랑 오픈 시간
            if (time.equals("") || time.equals("null")) time = "정보없음";
            String phone = jsonObject.getString("rest_phone"); // 레스토랑 전화번호
            if (phone.equals("") || phone.equals("null")) phone = "정보없음";
            String point = jsonObject.getString("rest_point");
            String rest_crawling_date = jsonObject.getString("rest_crawling_date");
            String image = jsonObject.getString("rest_image");
            islike = (jsonObject.getString("favor").equals("true")) ? true : false;
            Glide.with(this).load("http://118.220.3.71:13565/download_file?category=download_review_image&u_id=" + image + "&google_id=" + id)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(restImage);
            Place_nameView.setText(name);
            AddressView.setText(addr);
            OpeningHour.setText(time);
            PhoneView.setText(phone);
            Place_Text_View.setText(text);
            if (point != null) dataPoint.setText(point + "점");
            if (rest_crawling_date != null) updateData.setText(rest_crawling_date);
            restname = name;
        } catch (Exception e) {
            e.printStackTrace();
        }
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



