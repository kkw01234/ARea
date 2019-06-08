package kr.co.area.hashtag.recommendation_path;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.ReadPathTask;

public class RecommendPathMainActivity extends AppCompatActivity {
    private Activity activity;
    Recommendlist_adapter r_adapter;
    FavoriteAdapter f_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_change);
        this.activity = this;
        ListView listview ;
        RecyclerView recyclerView = findViewById(R.id.favorite_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);



//        //ImageButton rec1 = (ImageButton) findViewById(R.id.);
//        rec1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MapRcommendAction();
//            }
//        });
        Button writePathButton = findViewById(R.id.writepathbuttion);
        Button mypick = findViewById(R.id.Mypick);
        mypick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(
                        getApplicationContext(), .class); // 다음 넘어갈 클래스 지정
                startActivity(intent); // 다음 화면으로 넘어가기
                */
            }
        });

        //작성버튼으로 넘어가는 곳
        writePathButton.setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), WriteRecommendPathActivity.class);
            startActivity(intent);
        });
        r_adapter = new Recommendlist_adapter() ;
        resultRecent();

        // Adapter 생성


        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(r_adapter);


        f_adapter = new FavoriteAdapter(activity);
        resultFavorite();
        recyclerView.setAdapter(f_adapter);



        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    // get item
                    Recommendlist_item item = (Recommendlist_item) parent.getItemAtPosition(position) ;

                    String titleStr = item.getTitle() ;
                    String descStr = item.getDesc() ;
                    Drawable iconDrawable = item.getIcon() ;

                    //intent
                    Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
                    startActivity(intent);


                }
        }) ;

    }

    public void MapRcommendAction(){
        Intent intent = new Intent(getApplicationContext(), Map_recommend.class);
        startActivity(intent);
    }
    public String resultRecent(){
        try {
            ReadPathTask readPathTask = new ReadPathTask(activity,"load_recent_recommend_path");
            String result = readPathTask.execute().get();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = (JsonArray)parser.parse(result);
            for(int i=0;i<jsonArray.size();i++){
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                String id = object.get("id").getAsString();
                String title = object.get("title").getAsString();
                String writeName = object.get("writename").getAsString();
                //String url = "http://118.220.3.71:13565/download_file?cate=path_image&u_id=null&google_id="+id;
                r_adapter.addItem(ContextCompat.getDrawable(this, selectDrawable(i+1)), title, writeName) ;
            }
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public int selectDrawable(int i){
        switch (i){
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

    public void resultFavorite(){
        try {
            ReadPathTask readPathTask = new ReadPathTask(activity, "load_favorite_recommend_path");
            String result = readPathTask.execute().get();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = (JsonArray)parser.parse(result);
            for(int i=0;i<jsonArray.size();i++){
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                String id = object.get("id").getAsString();
                String title = object.get("title").getAsString();
                String writeName = object.get("writename").getAsString();
                String url = "http://118.220.3.71:13565/download_file?category=path_image&u_id=null&google_id="+id;
                int good = object.get("good").getAsInt();
                Recommendlist_item item = new Recommendlist_item();
                item.setUrl(url);
                item.setTitle(title);
                item.setDesc(writeName);
                item.setGood(good);
                f_adapter.additem(item);
            }
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}