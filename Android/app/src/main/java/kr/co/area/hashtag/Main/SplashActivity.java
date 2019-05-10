package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import kr.co.area.hashtag.Login.Login;

public class SplashActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(3000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, Login.class));
        //여기에 추가해야할 부분이 세션이 없을 때는 Login창으로 있으면 바로 메인 화면으로
        finish();
    }
}
