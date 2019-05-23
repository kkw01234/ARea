package kr.co.area.hashtag.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import kr.co.area.hashtag.login.LoginActivity;
import kr.co.area.hashtag.map.GoogleMapsActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.recommend.RecommendActivity;
import kr.co.area.hashtag.write.WriteActivity;
import kr.co.area.hashtag.ar.ARActivity;
import kr.co.area.hashtag.asyncTask.LogoutTask;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AbsListView.OnScrollListener {
    private Activity activity;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private TextView userHi;
    private ImageView profile,homeLogo;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, map_button, AR_button;
    private ArrayList<MyItem> marItem;
    private MyListAdapter 	  mMyAdapte;
    private ListView mListView;
    private MyItem 			  items;

    // 스크롤 로딩
    private LayoutInflater mInflater;
    private boolean mLockListView;


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



        mListView = (ListView) findViewById(R.id.morelist);
        marItem = new ArrayList<MyItem>();
        mLockListView = true;

        // 푸터를 등록. setAdapter 이전에 해야함.
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView.addFooterView(mInflater.inflate(R.layout.listview_footer, null));

        // 스크롤 리스너 등록
        mListView.setOnScrollListener(this);

        mMyAdapte = new MyListAdapter(this, R.layout.custom_layout, marItem);
        mListView.setAdapter((ListAdapter) mMyAdapte);

        addItems(2);

        //헤더부분
        headerView = navigationView.getHeaderView(0);
        userHi = headerView.findViewById(R.id.userHi);
        profile = headerView.findViewById(R.id.profilView);
        homeLogo = headerView.findViewById(R.id.LogoBtn);
        userHi.setText(user.getString("userName", "???") + "님\n안녕하세요");

        if (!(image.equals(""))) {
            profile.setImageBitmap(bitmap);
        }
        profile.setOnClickListener(headListener);
        homeLogo.setOnClickListener(headListener);


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        map_button = (FloatingActionButton)findViewById(R.id.map_Button);
        AR_button = (FloatingActionButton)findViewById(R.id.AR_Button);

        fab.setOnClickListener(this);
        map_button.setOnClickListener(this);
        AR_button.setOnClickListener(this);

    }



    View.OnClickListener headListener = (view) -> {
        switch (view.getId()) {
            case R.id.LogoBtn:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.profilView:
                startActivity(new Intent(activity, MypageActivity.class));
                finish();
                break;

        }
    };

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFabOpen) {
            map_button.startAnimation(fab_close);
            AR_button.startAnimation(fab_close);
            map_button.setClickable(false);
            AR_button.setClickable(false);
            isFabOpen = false;
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
            case R.id.setting:
                startActivity(new Intent(this, MypageActivity.class));
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


    // 리스트뷰 출력 항목
    class MyItem
    {
        MyItem(String _coustId)
        {
            sCustId = _coustId;
        }
        String sCustId;
    }

    // 어댑터 클래스
    class MyListAdapter extends BaseAdapter
    {
        Context cContext;
        LayoutInflater lInflater;
        ArrayList<MyItem> alSrc;
        int layout;

        public MyListAdapter(Context _context, int _layout, ArrayList<MyItem> _arrayList)
        {
            cContext  = _context;
            lInflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            alSrc	  = _arrayList;
            layout    = _layout;
        }

        @Override
        public int getCount()
        {
            return alSrc.size();
        }

        @Override
        public Object getItem(int position)
        {
            return alSrc.get(position).sCustId;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }


        // 각 뷰의 항목 생성
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final int pos = position;
            if(convertView == null)
            {
                convertView = lInflater.inflate(layout, parent, false);
            }

            final String getCustId = alSrc.get(pos).sCustId;

            ImageView img1 = (ImageView) findViewById(R.id.homeimg1);
            TextView text1 = (TextView) findViewById(R.id.hometext1);

            return convertView;
        }
    }

    // 더미 아이템 추가
    private void addItems(final int size)
    {
        // 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
        mLockListView = true;
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                for(int i = 0 ; i < size ; i++)
                {
                    items = new MyItem("more " + i);
                    marItem.add(items);
                }
                // 모든 데이터를 로드하여 적용하였다면 어댑터에 알리고
                // 리스트뷰의 락을 해제합니다.
                mMyAdapte.notifyDataSetChanged();
                mLockListView = false;
            }
        };
        // 속도의 딜레이를 구현하기 위한 꼼수
        Handler handler = new Handler();
        handler.postDelayed(run, 5000);
    }
    // ============================================== //
    // public Method
    // ============================================== //
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
        // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정합니다.
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false)
        {
            Log.i("list", "Loading next items");
            addItems(2);
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }
}