package kr.co.area.hashtag.write;


import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.asyncTask.WriteReviewTask;
import kr.co.area.hashtag.asyncTask.WriteReviewWithImageTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.main.HomeActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MypageActivity;
import kr.co.area.hashtag.recommendation_path.RecommendPathMainActivity;

public class WriteReviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "WriteReviewActivity";
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi, posInfo;
    private ImageView profile, goMyPageImg;

    int PICK_IMAGE_REQUEST = 1;
    static Bitmap scaled;
    ImageView imgView;
    static String reviewText, reviewAddress;
    static float reviewPoint;

    //xml
    private ImageView img1;
    private TextView name;
    private EditText ed1;
    private RatingBar rb;
    private Button wrbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
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
        });
        goMyPageImg.setOnClickListener((view) -> {
            startActivity(new Intent(activity, MypageActivity.class));
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("rest_id");
        String restname = intent.getStringExtra("rest_name");
        System.out.println(id);

        name = findViewById(R.id.restname_text);
        img1 = findViewById(R.id.writeimage);
        ed1 = findViewById(R.id.wirtetext);
        rb = findViewById(R.id.writepoint);
        wrbtn = findViewById(R.id.writebtn);

        name.setText(restname);

        wrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reviewText = ed1.getText().toString();
                reviewPoint = rb.getRating();
                if (scaled != null) {
                    try {
                        String result = new WriteReviewWithImageTask(activity).execute(scaled, id, reviewText, Float.toString(reviewPoint)).get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        if (state.equals("duplicate")) {
                            Toast.makeText(activity.getApplicationContext(), "이미 이 식당에 대해 리뷰를 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                        } else if (state.equals("success")) { // success인 경우 작성된 리뷰의 아이디를 받는다.
                            String review_id = jObject.getString("review_id");
                            Toast.makeText(activity.getApplicationContext(), "리뷰 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String result = new WriteReviewTask(activity).execute(id, reviewText, Float.toString(reviewPoint)).get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        if (state.equals("duplicate")) {
                            Toast.makeText(activity.getApplicationContext(), "이미 이 식당에 대해 리뷰를 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                        } else if (state.equals("success")) { // success인 경우 작성된 리뷰의 아이디를 받는다.
                            String review_id = jObject.getString("review_id");
                            Toast.makeText(activity.getApplicationContext(), "리뷰 작성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //뒤로가기
    public void onBackPressed() {
        finish();
    }

    //로드버튼 클릭시 실행
    public void loadImagefromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //이미지 선택작업을 후의 결과 처리
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                Uri uri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                imgView = (ImageView) findViewById(R.id.writeimage);
                imgView.setImageBitmap(scaled);

            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
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
                break;
            case R.id.map_search:
                startActivity(new Intent(this, GoogleMapsActivity.class));
                break;
            case R.id.rec_path:
                startActivity(new Intent(this, RecommendPathMainActivity.class));
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



