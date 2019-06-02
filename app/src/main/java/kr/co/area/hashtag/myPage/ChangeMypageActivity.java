package kr.co.area.hashtag.myPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.ChangeNameTask;
import kr.co.area.hashtag.asyncTask.CheckNameTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.recommend.RecommendActivity;

public class ChangeMypageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg; // 네비게이션 창

    TextView idView, emailView;
    EditText joinPwd, checkPwd, joinname;
    Button changeButton, checkNamebtn;
    boolean nameCheck = false;
    String userId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemypage);

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



        idView = (TextView) findViewById(R.id.idtextView);
        emailView = (TextView) findViewById(R.id.emailtextView);
        joinPwd = (EditText) findViewById(R.id.joinpw);
        checkPwd = (EditText) findViewById(R.id.checkjoinpw);
        joinname = (EditText) findViewById(R.id.nickname);
        changeButton = (Button) findViewById(R.id.changeBtn);
        checkNamebtn = (Button) findViewById(R.id.nickcheckbtn);

        SharedPreferences auto = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userId = auto.getString("userId",null);
        String userName = auto.getString("userName",null);
        String userEmail = auto.getString("userEmail", null);

        idView.setText(userId);
        emailView.setText(userEmail);
        joinname.setText(userName);

        checkNamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkName = joinname.getText().toString();
                if (checkName.equals("")) {
                    Toast.makeText(ChangeMypageActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    joinname.setFocusableInTouchMode(true);
                    joinname.requestFocus();
                    return;
                }
                try {
                    String result = new CheckNameTask(activity).execute(checkName).get();
                    /*
                     *  제이슨 구조 해독해서, result 값 알기
                     * */
                    JSONObject jObject = new JSONObject(result);
                    String state = jObject.getString("result");
                    System.out.println(state);
                    if (state.equals("avail")) {
                        Toast.makeText(ChangeMypageActivity.this, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        nameCheck = true;
                    } else if (state.equals("dup")) {
                        Toast.makeText(ChangeMypageActivity.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                        joinname.setFocusableInTouchMode(true);
                        joinname.requestFocus();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            String userPwd = joinPwd.getText().toString();
            String checkPw = checkPwd.getText().toString();
            @Override
            public void onClick(View v) {
                if (!userPwd.equals(checkPw)) {
                    Toast.makeText(ChangeMypageActivity.this, "비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
                    joinPwd.setFocusableInTouchMode(true);
                    joinPwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinPwd,0);
                    return;
                }
                if (userPwd.equals("")) {
                    Toast.makeText(ChangeMypageActivity.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                    joinPwd.setFocusableInTouchMode(true);
                    joinPwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinPwd,0);
                    return;
                }
                if (checkPw.equals("")) {
                    Toast.makeText(ChangeMypageActivity.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                    checkPwd.setFocusableInTouchMode(true);
                    checkPwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinPwd,0);
                    return;
                }
                /*닉네임 수정 서버*/
                String checkName = joinname.getText().toString();
                if (checkName.equals("")) {
                    Toast.makeText(ChangeMypageActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String result = new ChangeNameTask(activity).execute(checkName).get();
                    /*
                     *  제이슨 구조 해독해서, result 값 알기
                     * */
                    JSONObject jObject = new JSONObject(result);
                    String state = jObject.getString("result");
                    System.out.println(state);
                    if (state.equals("avail")) {
                        Toast.makeText(ChangeMypageActivity.this, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        nameCheck = true;
                    } else if (state.equals("dup")) {
                        Toast.makeText(ChangeMypageActivity.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(ChangeMypageActivity.this, MypageActivity.class));
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