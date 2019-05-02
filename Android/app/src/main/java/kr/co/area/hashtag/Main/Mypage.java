package kr.co.area.hashtag.Main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.Recommend.ImageGridAdapter;


public class Mypage extends AppCompatActivity {

    private int[] imageIDs = new int[] {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        //-----------------------------------------------------------------------
        // 사진들을 보여줄 GridView 뷰의 어댑터 객체를 정의하고 그것을 이 뷰의 어댑터로 설정합니다.

        GridView gridViewImages = (GridView)findViewById(R.id.gridViewImages);
        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(this, imageIDs);
        gridViewImages.setAdapter(imageGridAdapter);
    }
}

