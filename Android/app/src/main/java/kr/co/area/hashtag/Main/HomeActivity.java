package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import kr.co.area.hashtag.Login.Login;
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
    private TextView userHi;
    private TextView goMyPage;

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
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences user = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.userHi);
        userHi.setText(user.getString("userName", "???") + "님 안녕하세요");
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.ar_search) {
            startActivity(new Intent(this, AR.class));
            finish();
        } else if (id == R.id.map_search) {
            startActivity(new Intent(this, GoogleMapsActivity.class));
            finish();
        } else if (id == R.id.kwd_search) {
        } else if (id == R.id.rec_path) {
            startActivity(new Intent(this, RecommendActivity.class));
            finish();
        } else if (id == R.id.logout) {
            String result = null;
            try {
                result = new LogoutTask(activity).execute().get();
                JSONObject jObject = new JSONObject(result);
                String state = jObject.getString("result");
                startActivity(new Intent(this, Login.class));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.write) {
            startActivity(new Intent(this, WriteActivity.class));
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
