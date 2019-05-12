package kr.co.area.hashtag.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.area.hashtag.R;

public class Join extends Activity {
    EditText joinId, joinPwd, checkPwd, joinMail, joinname;
    Button checkBtn, joinButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        joinId = (EditText) findViewById(R.id.joinId);
        joinPwd = (EditText) findViewById(R.id.joinpw);
        checkPwd = (EditText) findViewById(R.id.checkjoinpw);
        joinMail = (EditText) findViewById(R.id.joinmail);
        joinname = (EditText) findViewById(R.id.nickname);
        checkBtn = (Button) findViewById(R.id.checkbtn);
        joinButton = (Button) findViewById(R.id.joinBtn);
        checkBtn.setOnClickListener(btnListener);
        joinButton.setOnClickListener(btnListener);
    }

    class JoinTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestHttpURLConnection conn = new RequestHttpURLConnection();
            String url = "http://118.220.3.71:13565/register_user";
            ArrayList<Parameter> params = new ArrayList<>();
            params.add(new Parameter("id", strings[0]));
            params.add(new Parameter("pwd", strings[1]));
            params.add(new Parameter("name", strings[2]));
            params.add(new Parameter("email", strings[3]));
            String result = conn.request(url, params, getApplicationContext());
            System.out.println(result);
            return result;
        }
    }
    class CheckTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            RequestHttpURLConnection conn = new RequestHttpURLConnection();
            String url = "http://118.220.3.71:13565/check_dup_id";
            ArrayList<Parameter> params = new ArrayList<>();
            params.add(new Parameter("id", strings[0]));
            String result = conn.request(url, params, getApplicationContext());
            System.out.println(result);
            return result;
        }
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkbtn: // 중복 버튼 눌렀을 경우
                    String checkId = joinId.getText().toString();
                    try {
                        String result = new  CheckTask().execute(checkId).get();
                        /*
                         *  제이슨 구조 해독해서, result 값 알기
                         * */
                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        System.out.println(state);
                        if (state.equals("avail")) {
                            Toast.makeText(Join.this, "사용가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        } else if (state.equals("dup")) {
                            Toast.makeText(Join.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.joinBtn: // 회원가입
                    String userId = joinId.getText().toString();
                    String userPwd = joinPwd.getText().toString();
                    String checkPw = checkPwd.getText().toString();
                    String username = joinname.getText().toString();
                    String userMail = joinMail.getText().toString();

                    try {
                        if (!userPwd.equals(checkPw)) {
                            Toast.makeText(Join.this, "비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String result = new JoinTask().execute(userId,userPwd,username,userMail).get();

                        JSONObject jObject = new JSONObject(result);
                        String state = jObject.getString("result");
                        System.out.println(state);

                        if (state.equals("success")) {
                            Toast.makeText(Join.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Join.this, Login.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };

}

