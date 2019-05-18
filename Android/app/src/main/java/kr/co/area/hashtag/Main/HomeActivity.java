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

import kr.co.area.hashtag.Login.LoginActivity;
import kr.co.area.hashtag.Map.GoogleMapsActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Recommend.RecommendActivity;
import kr.co.area.hashtag.Write.WriteActivity;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.LogoutTask;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi, userEmail;
    private TextView goMyPage;
    private ImageView profile;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, map_button, AR_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        SharedPreferences pref1 = getSharedPreferences("image", MODE_PRIVATE);
        String image = pref1.getString("imagestrings", "");
        Bitmap bitmap = StringToBitMap(image);

        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.userHi);
        userEmail = headerView.findViewById(R.id.useremail);
        userHi.setText(user.getString("userName", "???") + "님 안녕하세요");
        userEmail.setText(user.getString("userEmail", ""));

        if (!(image.equals(""))) {
            profile = headerView.findViewById(R.id.profilView);
            profile.setImageBitmap(bitmap);
        }
        goMyPage = headerView.findViewById(R.id.goMyPage);
        goMyPage.setOnClickListener((v) -> {
            startActivity(new Intent(HomeActivity.this, Mypage.class));
            finish();
        });


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        map_button = (FloatingActionButton)findViewById(R.id.map_Button);
        AR_button = (FloatingActionButton)findViewById(R.id.AR_Button);

        fab.setOnClickListener(this);
        map_button.setOnClickListener(this);
        AR_button.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                logout();
                finishAndRemoveTask();
            } else {
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
                startActivity(new Intent(this, ARActivity.class));
                finish();
                break;
            case R.id.map_search:
                startActivity(new Intent(this, GoogleMapsActivity.class));
                finish();
                break;
            case R.id.kwd_search:
                break;
            case R.id.rec_path:
                startActivity(new Intent(this, RecommendActivity.class));
                break;
            case R.id.logout:
                logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                //Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.map_Button:
                anim();
                startActivity(new Intent(this, GoogleMapsActivity.class));
                finish();
                break;
            case R.id.AR_Button:
                anim();
                startActivity(new Intent(this, ARActivity.class));
                finish();
                break;
        }
    }
    public void anim() {

        if (isFabOpen) {
            map_button.startAnimation(fab_close);
            AR_button.startAnimation(fab_close);
            map_button.setClickable(false);
            AR_button.setClickable(false);
            isFabOpen = false;
        }
        else {
            map_button.startAnimation(fab_open);
            AR_button.startAnimation(fab_open);
            map_button.setClickable(true);
            AR_button.setClickable(true);
            isFabOpen = true;
        }
    }
}