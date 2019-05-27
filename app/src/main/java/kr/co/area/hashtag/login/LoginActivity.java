package kr.co.area.hashtag.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import kr.co.area.hashtag.main.HomeActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.LoginTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;

public class LoginActivity extends Activity {
    EditText userId, userPwd;
    TextView autoLoginInfo;
    Button loginBtn, joinBtn, logoutBtn;
    Switch autoLogin;
    boolean isAuto;
    Activity activity;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        isAuto = true;
        userId = findViewById(R.id.userId);
        userPwd = findViewById(R.id.userPwd);
        loginBtn = findViewById(R.id.loginBtn);
        joinBtn = findViewById(R.id.joinBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        autoLogin = findViewById(R.id.autoLogin);
        autoLoginInfo = findViewById(R.id.autoLoginInfo);
        autoLogin.setOnCheckedChangeListener((c, b) -> {
            isAuto = b;
            if(isAuto) autoLoginInfo.setText("ON");
            else autoLoginInfo.setText("OFF");
        });
        autoLogin.setChecked(true);
        loginBtn.setOnClickListener(btnListener);
        joinBtn.setOnClickListener(btnListener);
        logoutBtn.setOnClickListener(btnListener);
        userPwd.setImeOptions(EditorInfo.IME_ACTION_DONE);
        userPwd.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
    }

    View.OnClickListener btnListener = (view) -> {
        switch (view.getId()) {
            case R.id.joinBtn:
                join();
                break;
            case R.id.loginBtn: // 로그인 버튼 눌렀을 경우
                login();
                break;
            case R.id.logoutBtn:
                logout();
                break;

        }
    };

    private void join() {
        startActivity(new Intent(LoginActivity.this, JoinActivity.class));
        finish();
    }

    private void login() {
        String loginId = userId.getText().toString();
        String loginPwd = userPwd.getText().toString();
        try {
            String result = new LoginTask(activity).execute(loginId, loginPwd).get();
            JSONObject jObject = new JSONObject(result);
            String state = jObject.getString("result");
            if (state.equals("success")) {
                if (isAuto) {
                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.putString("autoId", loginId);
                    autoLogin.putString("autoPwd", loginPwd);
                    autoLogin.commit();
                }
                System.out.println(jObject.getString("userImage"));
                SharedPreferences userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                SharedPreferences.Editor infoEdit = userInfo.edit();
                infoEdit.putString("userId", jObject.getString("userId"));
                infoEdit.putString("userName", jObject.getString("userName"));
                infoEdit.putString("userEmail", jObject.getString("userEmail"));
                infoEdit.putString("userRight", jObject.getString("userRight"));
                infoEdit.putString("userImage", jObject.getString("userImage"));
                infoEdit.commit();
                Toast.makeText(LoginActivity.this, "로그인", Toast.LENGTH_SHORT).show();
                Intent preIntent = getIntent();
                Intent newIntent = new Intent(this, HomeActivity.class);
                if (preIntent.getExtras() != null) newIntent.putExtras(preIntent.getExtras());
                startActivity(newIntent);
                finish();
            } else if (state.equals("fail")) {
                Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 틀렸음", Toast.LENGTH_SHORT).show();
            } else if (state.equals("already login")) {
                Toast.makeText(LoginActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            finish();
        }
        else {
            backPressedTime = tempTime;
        }
    }
    private void logout() {
        try {
            new LogoutTask(activity).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

