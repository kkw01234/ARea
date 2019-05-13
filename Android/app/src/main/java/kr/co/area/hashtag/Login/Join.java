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
import kr.co.area.hashtag.asyncTask.CheckIdTask;
import kr.co.area.hashtag.asyncTask.CheckNameTask;
import kr.co.area.hashtag.asyncTask.JoinTask;

public class Join extends Activity {
    EditText joinId, joinPwd, checkPwd, joinMail, joinname;
    Button checkBtn, joinButton, checkName;
    boolean idCheck = false, nameCheck = false;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        activity = this;
        joinId = findViewById(R.id.joinId);
        joinPwd = findViewById(R.id.joinpw);
        checkPwd = findViewById(R.id.checkjoinpw);
        joinMail = findViewById(R.id.joinmail);
        joinname = findViewById(R.id.nickname);
        checkBtn = findViewById(R.id.checkbtn);
        joinButton = findViewById(R.id.joinBtn);
        checkName = findViewById(R.id.checkbtn2);
        joinId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                idCheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });
        joinname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameCheck = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });
        checkBtn.setOnClickListener(btnListener);
        joinButton.setOnClickListener(btnListener);
        checkName.setOnClickListener(btnListener);
    }

    View.OnClickListener btnListener = (view) -> {
        switch (view.getId()) {
            case R.id.checkbtn: // 중복 버튼 눌렀을 경우
                String checkId = joinId.getText().toString();
                if (checkId.equals("")) {
                    Toast.makeText(Join.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String result = new CheckIdTask(activity).execute(checkId).get();
                    /*
                     *  제이슨 구조 해독해서, result 값 알기
                     * */
                    JSONObject jObject = new JSONObject(result);
                    String state = jObject.getString("result");
                    System.out.println(state);
                    if (state.equals("avail")) {
                        Toast.makeText(Join.this, "사용가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        idCheck = true;
                    } else if (state.equals("dup")) {
                        Toast.makeText(Join.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.checkbtn2: // 중복 버튼 눌렀을 경우
                String checkName = joinname.getText().toString();
                if (checkName.equals("")) {
                    Toast.makeText(Join.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String result = new CheckNameTask(activity).execute(checkName).get();
                    /*
                     *  제이슨 구조 해독해서, result 값 알기
                     * */
                    JSONObject jObject = new JSONObject(result);
                    String state = jObject.getString("result");
                    System.out.println(state);
                    if (state.equals("avail")) {
                        Toast.makeText(Join.this, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        nameCheck = true;
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
                    if (!idCheck) {
                        Toast.makeText(Join.this, "확인되지 않은 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!nameCheck) {
                        Toast.makeText(Join.this, "확인되지 않은 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (username.equals("")) {
                        Toast.makeText(Join.this, "닉네임을 적어주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (userPwd.equals("")) {
                        Toast.makeText(Join.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkPw.equals("")) {
                        Toast.makeText(Join.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (userMail.equals("")) {
                        Toast.makeText(Join.this, "이메일을 적어주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String result = new JoinTask(activity).execute(userId, userPwd, username, userMail).get();

                    JSONObject jObject = new JSONObject(result);
                    String state = jObject.getString("result");
                    System.out.println(state);

                    if (state.equals("success")) {
                        Toast.makeText(Join.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Join.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else if (state.equals("email fail")) {
                        Toast.makeText(Join.this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    };

}