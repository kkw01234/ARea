package kr.co.area.hashtag.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import kr.co.area.hashtag.Main.MainActiviy;
import kr.co.area.hashtag.R;

public class Login extends Activity {
    EditText userId, userPwd;
    Button loginBtn, joinBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userId = (EditText) findViewById(R.id.userId);
        userPwd = (EditText) findViewById(R.id.userPwd);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);
        logoutBtn = (Button) findViewById(R.id.buttonlogout);
        loginBtn.setOnClickListener(btnListener);
        joinBtn.setOnClickListener(btnListener);
        logoutBtn.setOnClickListener(btnListener);
    }

    class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestHttpURLConnection conn = new RequestHttpURLConnection();
            String url = "http://118.220.3.71:13565/login";
            ArrayList<Parameter> params = new ArrayList<>();
            params.add(new Parameter("id", strings[0]));
            params.add(new Parameter("pwd", strings[1]));
            String result = conn.request(url, params, getApplicationContext());
            System.out.println(result);
            return result;
        }
    }

    class LogoutTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestHttpURLConnection conn = new RequestHttpURLConnection();
            String url = "http://118.220.3.71:13565/logout";
            String result = conn.request(url, null, getApplicationContext());
            System.out.println(result);
            return result;
        }
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
                    System.out.println(loginid);
                    try {
                        String result = new LoginTask().execute(loginid, loginpwd).get();
                        /*
                         *  제이슨 구조 해독해서, result 값 알기
                         * */
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        System.out.println(state);
                        if (state.equals("success")) {
                            Toast.makeText(Login.this, "로그인", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActiviy.class);
                            startActivity(intent);
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
                case R.id.buttonlogout:
                    String result = null;
                    try {
                        result = new LogoutTask().execute().get();
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        System.out.println(state);
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

