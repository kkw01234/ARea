package kr.co.area.hashtag.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import kr.co.area.hashtag.Main.MainActiviy;
import kr.co.area.hashtag.Main.HomeActivity;
import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.LoginTask;
import kr.co.area.hashtag.asyncTask.LogoutTask;

public class Login extends Activity {
    EditText userId, userPwd;
    Button loginBtn, joinBtn, logoutBtn;
    Switch autoLogin;
    boolean isAuto;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userId = (EditText) findViewById(R.id.userId);
        userPwd = (EditText) findViewById(R.id.userPwd);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        autoLogin = (Switch) findViewById(R.id.autoLogin);
        activity = this;
        autoLogin.setOnCheckedChangeListener((c, b) -> isAuto = b);
        loginBtn.setOnClickListener(btnListener);
        joinBtn.setOnClickListener(btnListener);
        logoutBtn.setOnClickListener(btnListener);
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.joinBtn:
                    joinFunction(view);
                    break;
                case R.id.loginBtn: // 로그인 버튼 눌렀을 경우
                    String loginid = userId.getText().toString();
                    String loginpwd = userPwd.getText().toString();
                    try {
                        String result = new LoginTask(activity).execute(loginid, loginpwd).get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        if (state.equals("success")) {
                            if (isAuto) {
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLogin = auto.edit();
                                autoLogin.putString("autoId", loginid);
                                autoLogin.putString("autoPwd", loginpwd);
                                autoLogin.commit();
                            }
                            SharedPreferences userInfo = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor infoEdit = userInfo.edit();
                            String userName = jObject.getString("userName");
                            String userEmail = jObject.getString("userEmail");
                            String userRight = jObject.getString("userRight");
                            infoEdit.putString("userName", userName);
                            infoEdit.putString("userEmail", userEmail);
                            infoEdit.putString("userRight", userRight);
                            infoEdit.commit();
                            Toast.makeText(Login.this, "로그인", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, HomeActivity.class));
                            finish();
                        } else if (state.equals("fail")) {
                            Toast.makeText(Login.this, "아이디 또는 비밀번호가 틀렸음", Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userPwd.setText("");
                        } else if (state.equals("already login")) {
                            Toast.makeText(Login.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userPwd.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.logoutBtn:
                    String result = null;
                    try {
                        result = new LogoutTask(activity).execute().get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    public void joinFunction(View v) {
        Intent joinintent = new Intent(getApplicationContext(), Join.class);
        startActivity(joinintent);
    }

}

