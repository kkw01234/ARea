package kr.co.area.hashtag.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    boolean idcheck = false;

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
        joinId.setOnClickListener(idListener);
        joinId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                idcheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });
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
    View.OnClickListener idListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            idcheck = true;
        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkbtn: // 중복 버튼 눌렀을 경우
                    String checkId = joinId.getText().toString();
                    if (checkId.equals("")) {
                        Toast.makeText(Join.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                            idcheck = true;
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
                        if (!idcheck){
                            Toast.makeText(Join.this, "확인되지 않은 아이디 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (username.equals("")){
                            Toast.makeText(Join.this, "닉네임을 적어주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (userPwd.equals("")){
                            Toast.makeText(Join.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (checkPw.equals("")){
                            Toast.makeText(Join.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (userMail.equals("")){
                            Toast.makeText(Join.this, "이메일을 적어주세요.", Toast.LENGTH_SHORT).show();
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
                        else if (state.equals("email fail")) {
                            Toast.makeText(Join.this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

}

