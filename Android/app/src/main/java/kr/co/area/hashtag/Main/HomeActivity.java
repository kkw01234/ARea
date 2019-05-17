package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import kr.co.area.hashtag.Login.LoginActivity;
import kr.co.area.hashtag.Map.GoogleMapsActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Recommend.RecommendActivity;
import kr.co.area.hashtag.Write.WriteActivity;
import kr.co.area.hashtag.asyncTask.LogoutTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi,userEmail;
    private TextView goMyPage;
    private ImageView profile;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dummy);
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
        SharedPreferences pref1 = getSharedPreferences("image",MODE_PRIVATE);
        String image = pref1.getString("imagestrings","");
        Bitmap bitmap = StringToBitMap(image);

        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.userHi);
        userEmail = headerView.findViewById(R.id.useremail);
        userHi.setText(user.getString("userName", "???") + "님 안녕하세요");
        userEmail.setText(user.getString("userEmail",""));

        if(!(image.equals(""))) {
            profile = headerView.findViewById(R.id.profilView);
            profile.setImageBitmap(bitmap);
        }
        goMyPage = headerView.findViewById(R.id.goMyPage);
        goMyPage.setOnClickListener((v) -> {
            startActivity(new Intent(HomeActivity.this, Mypage.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            }
            else {
                backPressedTime = tempTime;
            }
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.ar_search:
                startActivity(new Intent(this, AR.class));
                finish();
                break;
            case R.id.map_search:
                startActivity(new Intent(this, GoogleMapsActivity.class));
                finish();
                break;
            case R.id.kwd_search:
                startActivity(new Intent(this, RecommendActivity.class));
                finish();
                break;
            case R.id.rec_path:
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.write:
                startActivity(new Intent(this, WriteActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        try {
            new LogoutTask(activity).execute().get();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}