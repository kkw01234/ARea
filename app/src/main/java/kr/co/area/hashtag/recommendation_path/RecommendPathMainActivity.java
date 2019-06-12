package kr.co.area.hashtag.recommendation_path;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.asyncTask.ReadPathTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MypageActivity;

public class RecommendPathMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "RecommendPathActivity";
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg;
    private Button writeButton;

    private ListView listView;
    private RecyclerView recyclerView;
    Recommendlist_adapter r_adapter;
    FavoriteAdapter f_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_change);
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

        writeButton = findViewById(R.id.writepathbuttion);
        writeButton.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), WriteRecommendPathActivity.class);
            startActivity(intent);
        });

        // 리스트뷰 참조 및 Adapter달기
        listView = findViewById(R.id.recent_view);
        listView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        r_adapter = new Recommendlist_adapter();
        listView.setAdapter(r_adapter);
        resultRecent();

        recyclerView = findViewById(R.id.favorite_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        f_adapter = new FavoriteAdapter(activity);
        recyclerView.setAdapter(f_adapter);
        resultFavorite();

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Recommendlist_item item = (Recommendlist_item) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
                startActivity(intent);
            }
        });
    }

    public void MapRcommendAction() {
        Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
        startActivity(intent);
    }

    public String resultRecent() {
        try {
            ReadPathTask readPathTask = new ReadPathTask(activity, "load_recent_recommend_path");
            String result = readPathTask.execute().get();
            JSONArray jsonArray = new JSONArray(result);
            System.out.println("RECENT : " + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String writeName =jsonObject.getString("writename");
                //String url = "http://118.220.3.71:13565/download_file?cate=path_image&u_id=null&google_id="+id;
                r_adapter.addItem(ContextCompat.getDrawable(this, selectDrawable(i + 1)), title, writeName);
            }
            r_adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int selectDrawable(int i) {
        switch (i) {
            case 1:
                return R.drawable.ic_looks_1_black_24dp;
            case 2:
                return R.drawable.ic_looks_2_black_24dp;
            case 3:
                return R.drawable.ic_looks_3_black_24dp;
            case 4:
                return R.drawable.ic_looks_4_black_24dp;
            case 5:
                return R.drawable.ic_looks_5_black_24dp;
        }
        return 0;
    }

    public void resultFavorite() {
        try {
            ReadPathTask readPathTask = new ReadPathTask(activity, "load_favorite_recommend_path");
            String result = readPathTask.execute().get();
            JSONArray jsonArray = new JSONArray(result);
            System.out.println("FAVORITE : " + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String writeName =jsonObject.getString("writename");
                String url = "http://118.220.3.71:13565/download_file?category=path_image&u_id=null&google_id=" + id;
                int good = jsonObject.getInt("good");

                Recommendlist_item item = new Recommendlist_item();
                item.setUrl(url);
                item.setTitle(title);
                item.setDesc(writeName);
                item.setGood(good);
                f_adapter.additem(item);
            }
            f_adapter.notifyDataSetChanged();
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