package kr.co.area.hashtag.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import kr.co.area.hashtag.Login.Login;
import kr.co.area.hashtag.asyncTask.LoginTask;

public class SplashActivity extends Activity {
    Activity activity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String id = auto.getString("autoId", null);
        String pwd = auto.getString("autoPwd", null);
        System.out.println("ID :" + id);
        System.out.println("PWD :" + pwd);
        if (id != null && pwd != null) {
            try {
                String result = new LoginTask(activity).execute(id, pwd).get();
                JSONObject jObject = new JSONObject(result);
                String state = jObject.getString("result");
                if (state.equals("success") || state.equals("already login")) {
                    Toast.makeText(this, "자동 로그인", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                } else {
                    SharedPreferences.Editor editor = auto.edit();
                    editor.clear();
                    editor.commit();
                    startActivity(new Intent(this, Login.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences.Editor editor = auto.edit();
            editor.clear();
            editor.commit();
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }
}
