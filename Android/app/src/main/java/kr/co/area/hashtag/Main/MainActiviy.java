package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import kr.co.area.hashtag.R;

public class MainActiviy extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Adding Toolbar to the activity
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        // Initializing the TabLayout

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("AR검색"));
        tabLayout.addTab(tabLayout.newTab().setText("홈"));
        tabLayout.addTab(tabLayout.newTab().setText("지도검색"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    //메뉴만들기
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    //메뉴 눌렀을때 넘어갈 화면 정의
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu1:
//                Intent intent = new Intent(MainActiviy.this, AR.class);
//                startActivity(intent);
//                break;
//            case R.id.menu2:
//                Intent intent2 = new Intent(MainActiviy.this, GoogleMapsActivity.class);
//                startActivity(intent2);
//                break;
//            case R.id.menu3:
//                Intent intent3 = new Intent(MainActiviy.this, RecommendActivity.class);
//                startActivity(intent3);
//                break;
//            case R.id.menu4:
//                Intent intent4 = new Intent(MainActiviy.this, WriteActivity.class);
//                startActivity(intent4);
//                break;
//            case R.id.menu5:
//                Intent intent5 = new Intent(MainActiviy.this, Mypage.class);
//                startActivity(intent5);
//                break;
//            case R.id.menu6:
//                String result = null;
//                try {
//                    result = new LogoutTask(activity).execute().get();
//                    JSONObject jObject = new JSONObject(result);
//                    String state = jObject.getString("result");
//                    System.out.println(state);
//                    Intent intent6 = new Intent(MainActiviy.this, LoginActivity.class);
//                    startActivity(intent6);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//        }
        return true;

    }

}