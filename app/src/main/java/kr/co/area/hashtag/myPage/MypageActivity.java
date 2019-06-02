package kr.co.area.hashtag.myPage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import org.json.JSONArray;
import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.GetAllReviewByIdTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.asyncTask.UploadProfileImageTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.recommend.RecommendActivity;

public class MypageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg; // 네비게이션 창

    private TextView idView, nickView, emailView, gradeView;
    private ImageView changeImage; // 개인정보 창

    private TextView reviewTab, resTab;
    private boolean isReviewClick;

    final int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    static Bitmap bitmap;
    String userId;
    ImageView profileView;
    ColorStateList oldColors;
    ListView listview;
    MyReviewListViewAdapter reviewAdapter;
    MyReviewListViewAdapter likeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
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
        profile = headerView.findViewById(R.id.profileView);
        goMyPageImg = headerView.findViewById(R.id.go_mypage_img);
        userHi.setText(user.getString("userName", "???") + " 님,");
        userId = user.getString("userId", null);
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


        profileView = findViewById(R.id.profile_img);
        idView = findViewById(R.id.id_value);
        nickView = findViewById(R.id.nick_value);
        emailView = findViewById(R.id.email_value);
        changeImage = findViewById(R.id.change_profile);

        changeImage.setOnClickListener((v) -> {
            startActivity(new Intent(activity, ChangeMypageActivity.class));
            finish();
        });

        SharedPreferences auto = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        Glide.with(this).load(image).apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.circleCropTransform()).into(profileView);
        idView.setText(auto.getString("userId", "???"));
        nickView.setText(auto.getString("userName", "???"));
        emailView.setText(auto.getString("userEmail", "???"));

        reviewTab = findViewById(R.id.review_tab);
        resTab = findViewById(R.id.res_tab);
        reviewTab.setOnClickListener(tabClickListener);
        resTab.setOnClickListener(tabClickListener);
        oldColors = resTab.getTextColors();
        isReviewClick = true;


        listview = findViewById(R.id.my_review_list);
        listview.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        reviewAdapter = new MyReviewListViewAdapter(this);

        try {
            String result = new GetAllReviewByIdTask(this).execute().get();
            JSONArray jsonArray = new JSONArray(result);
            int len = jsonArray.length();
            for (int i = 0; i < len; ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String img = jsonObject.getString("img");
                String rest_name = jsonObject.getString("rest_name");
                String content = jsonObject.getString("content");
                String rate = jsonObject.getString("rate");
                String date = jsonObject.getString("date");

                reviewAdapter.addItem(img, rest_name, content, rate, date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listview.setAdapter(reviewAdapter);
    }

    View.OnClickListener tabClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.review_tab:
                if (isReviewClick) return;
                showReview();
                break;
            case R.id.res_tab:
                if (!isReviewClick) return;
                showRes();
                break;
        }
        isReviewClick = !isReviewClick;
        if (isReviewClick) {
            reviewTab.setTextColor(getResources().getColor(R.color.colorPrimary));
            resTab.setTextColor(oldColors);
        } else {
            reviewTab.setTextColor(oldColors);
            resTab.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    };

    private void showReview() {
        listview.setAdapter(reviewAdapter);
    }

    private void showRes() {
        listview.setAdapter(likeAdapter);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MypageActivity.this, HomeActivity.class));
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

    public void loadImagefromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                String result = new UploadProfileImageTask(activity).execute(scaled, userId).get();
                JSONObject jObject = new JSONObject(result);
                String state = jObject.getString("result");
                if (state.equals("success")) {
                    String url = "http://118.220.3.71:13565/download_file?category=download_my_image&u_id=" + userId;
                    Glide.with(this).clear(profileView);
                    Glide.with(this).load(url).apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.circleCropTransform()).into(profileView);
                    Glide.with(this).clear(profile);
                    Glide.with(this).load(url).apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .apply(RequestOptions.circleCropTransform()).into(profile);
                    Toast.makeText(this, "이미지가 변경되었습니다.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}

