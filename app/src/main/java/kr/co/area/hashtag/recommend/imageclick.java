package kr.co.area.hashtag.recommend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.content.Intent;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.main.HomeActivity;

public class imageclick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageclick);

        //----------------------------------------------------------------
        // 확대되는 이미지를 보여주기 위해 ImageView 뷰를 설정.
        ImageView imageView = (ImageView) findViewById(R.id.writeimage);
        setImage(imageView);
    }

        private void setImage(ImageView imageView) {
            //----------------------------------------------------------------
            // 초기 액티비티의 GridView 뷰의 이미지 항목을 클릭할 때 생성된 인텐트는
            // 이 액티비티는 getIntent 메소드를 호출하여 접근할 수 있음.
            Intent receivedIntent = getIntent();

            //----------------------------------------------------------------
            // 확대되는 이미지의 리소스 ID를 인텐트로부터 읽어들이고,
            // 그것을 ImageView 뷰의 이미지 리소스로 설정.

            int imageID = (Integer)receivedIntent.getExtras().get("image ID");
            imageView.setImageResource(imageID);
        }
    public void onBackPressed() {
        startActivity(new Intent(imageclick.this, HomeActivity.class));
        finish();
    }
}




