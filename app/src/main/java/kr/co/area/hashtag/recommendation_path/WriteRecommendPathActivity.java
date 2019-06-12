package kr.co.area.hashtag.recommendation_path;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.LogoutTask;
import kr.co.area.hashtag.asyncTask.WritePathTask;
import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.myPage.MypageActivity;
import kr.co.area.hashtag.utils.PathPlace;

public class WriteRecommendPathActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile, goMyPageImg;

    public static List<PathPlace> placeList;
    public static LinearLayout layout = null;
    private EditText pathName = null;
    private Bitmap bitmap = null;
    private ImageView imageView = null;
    private EditText writeContent = null;
    public int PICK_IMAGE_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_path_write);
        activity = this;
        placeList = new ArrayList<>();
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

        pathName = findViewById(R.id.pathname);
        ImageButton loadMap = findViewById(R.id.loadmap);
        layout = findViewById(R.id.writescroll);

        imageView = findViewById(R.id.rec_Gallery);
        writeContent = findViewById(R.id.writecontent);
        loadMap.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), WritePathMapActivity.class);
            //if(placeList.size() != 0)
            //intent.putExtra("list", placeList);
            startActivity(intent);
        });
        Button writeButton = findViewById(R.id.writePathButton);
        writeButton.setOnClickListener((v) -> {
            if (bitmap != null) {
                try {
                    String result = new WritePathTask(activity).execute(pathName.getText().toString(), bitmap, placeList, writeContent.getText().toString()).get();
                    JsonParser parser = new JsonParser();
                    JsonObject object = (JsonObject) parser.parse(result);
                    String res = object.get("result").getAsString();
                    if (res.equals("success")) {
                        Toast.makeText(getApplicationContext(), "등록에 성공했습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "모두 입력하지 않으셨거나 오류가 발생했습니다", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //인텐트 값 저장

    @Override
    public void onResume(){
        layout.removeAllViews();
        LinearLayout.LayoutParams layOutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        for (int i = 0; i < placeList.size(); i++) {
            TextView textView = new TextView(layout.getContext());
            textView.setText((i + 1) + ". " + placeList.get(i).name);
            textView.setLayoutParams(layOutParams);
            textView.setTextSize(23);
            textView.setOnClickListener((v) -> {
                String text = ((TextView) v).getText().toString();
                String str = text.substring(0, 1);
                PathPlace pathPlace = placeList.get(Integer.parseInt(str) - 1);
            });
            layout.addView(textView);
        }
        super.onResume();
    }

    public static void addPlace(String id, String name, Double latitude, Double longitude){
        placeList.add(new PathPlace(id, name, latitude, longitude));
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
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImageFromGallery(View view) {
        //Intent 생성
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_PIC과 차이점?
        intent.setType("image/*"); //이미지만 보이게
        //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //이미지를 하나 골랐을때
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
                //data에서 절대경로로 이미지를 가져옴
                System.out.println("dfsfds");
                Uri uri = data.getData();

                Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //이미지가 한계이상(?) 크면 불러 오지 못하므로 사이즈를 줄여 준다.
                int nh = (int) (bitmap2.getHeight() * (1024.0 / bitmap2.getWidth()));
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, 1024, nh, true);
                imageView.setImageBitmap(bitmap2);
                this.bitmap = bitmap2;
                System.out.println(this.bitmap.getRowBytes());

            } else {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


}





