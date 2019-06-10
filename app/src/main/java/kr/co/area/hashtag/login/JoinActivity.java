package kr.co.area.hashtag.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.CheckEmailTask;
import kr.co.area.hashtag.asyncTask.CheckIdTask;
import kr.co.area.hashtag.asyncTask.CheckNameTask;
import kr.co.area.hashtag.asyncTask.JoinTask;

public class JoinActivity extends Activity {
    EditText joinId, joinPwd, checkPwd, joinMail, joinname;
    Button checkBtn, joinButton, checkName, checkMail;
    boolean idCheck = false, nameCheck = false, mailCheck = false;
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
        checkBtn = findViewById(R.id.idcheckbtn);
        joinButton = findViewById(R.id.joinBtn);
        checkName = findViewById(R.id.nickcheckbtn);
        checkMail = findViewById(R.id.mailcheckbtn);
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
        checkMail.setOnClickListener(btnListener);
        joinMail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        joinMail.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                join();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(JoinActivity.this, LoginActivity.class));
        finish();
    }

    View.OnClickListener btnListener = (view) -> {
        switch (view.getId()) {
            case R.id.idcheckbtn: // 아이디중복 버튼 눌렀을 경우
                String checkId = joinId.getText().toString();
                if (checkId.equals("")) {
                    Toast.makeText(JoinActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    joinId.setFocusableInTouchMode(true);
                    joinId.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinId,0);
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
                        Toast.makeText(JoinActivity.this, "사용가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        idCheck = true;
                        joinPwd.setFocusableInTouchMode(true);
                        joinPwd.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(joinPwd,0);
                    } else if (state.equals("dup")) {
                        Toast.makeText(JoinActivity.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                        joinId.setFocusableInTouchMode(true);
                        joinId.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(joinId,0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nickcheckbtn: // 닉네임중복 버튼 눌렀을 경우
                String checkName = joinname.getText().toString();
                if (checkName.equals("")) {
                    Toast.makeText(JoinActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    joinname.setFocusableInTouchMode(true);
                    joinname.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinname,0);
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
                        Toast.makeText(JoinActivity.this, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        nameCheck = true;
                        joinMail.setFocusableInTouchMode(true);
                        joinMail.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(joinMail,0);
                    } else if (state.equals("dup")) {
                        Toast.makeText(JoinActivity.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                        joinname.setFocusableInTouchMode(true);
                        joinname.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(joinname,0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.mailcheckbtn: //메일중복 버튼을 눌렀을때
                String checkMail = joinMail.getText().toString();
                if (checkMail.equals("")) {
                    Toast.makeText(JoinActivity.this, "메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    joinMail.setFocusableInTouchMode(true);
                    joinMail.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinMail,0);
                    return;
                }
                try {
                    String result = new CheckEmailTask(activity).execute(checkMail).get();
                    /*
                     *  제이슨 구조 해독해서, result 값 알기
                     * */
                    JSONObject jObject = new JSONObject(result);
                    String state = jObject.getString("result");
                    System.out.println(state);
                    if (state.equals("avail")) {
                        mailCheck = true;
                        Toast.makeText(JoinActivity.this, "사용가능한 메일 입니다.", Toast.LENGTH_SHORT).show();
                    } else if (state.equals("dup")) {
                        Toast.makeText(JoinActivity.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                        joinMail.setFocusableInTouchMode(true);
                        joinMail.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(joinMail,0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.joinBtn: // 회원가입
                join();
                break;
        }
    };
    private void join(){
        String userId = joinId.getText().toString();
        String userPwd = joinPwd.getText().toString();
        String checkPw = checkPwd.getText().toString();
        String username = joinname.getText().toString();
        String userMail = joinMail.getText().toString();

        try {
            if (!userPwd.equals(checkPw)) {
                Toast.makeText(JoinActivity.this, "비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
                joinPwd.setFocusableInTouchMode(true);
                joinPwd.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinPwd,0);
                return;
            }
            if (!idCheck) {
                Toast.makeText(JoinActivity.this, "확인되지 않은 아이디 입니다.", Toast.LENGTH_SHORT).show();
                joinId.setFocusableInTouchMode(true);
                joinId.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinId,0);
                return;
            }
            if (!nameCheck) {
                Toast.makeText(JoinActivity.this, "확인되지 않은 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                joinname.setFocusableInTouchMode(true);
                joinname.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinname,0);
                return;
            }
            if (!mailCheck) {
                Toast.makeText(JoinActivity.this, "확인되지 않은 메일 입니다.", Toast.LENGTH_SHORT).show();
                joinMail.setFocusableInTouchMode(true);
                joinMail.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinMail,0);
                return;
            }
            if (username.equals("")) {
                Toast.makeText(JoinActivity.this, "닉네임을 적어주세요.", Toast.LENGTH_SHORT).show();
                joinname.setFocusableInTouchMode(true);
                joinname.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinname,0);
                return;
            }
            if (userPwd.equals("")) {
                Toast.makeText(JoinActivity.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                joinPwd.setFocusableInTouchMode(true);
                joinPwd.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinPwd,0);
                return;
            }
            if (checkPw.equals("")) {
                Toast.makeText(JoinActivity.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                checkPwd.setFocusableInTouchMode(true);
                checkPwd.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinPwd,0);
                return;
            }
            if (userMail.equals("")) {
                Toast.makeText(JoinActivity.this, "이메일을 적어주세요.", Toast.LENGTH_SHORT).show();
                joinMail.setFocusableInTouchMode(true);
                joinMail.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinMail,0);
                return;
            }
            String result = new JoinTask(activity).execute(userId, userPwd, username, userMail).get();

            JSONObject jObject = new JSONObject(result);
            String state = jObject.getString("result");
            System.out.println(state);

            if (state.equals("success")) {
                Toast.makeText(JoinActivity.this, "회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else if (state.equals("email fail")) {
                Toast.makeText(JoinActivity.this, "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                joinMail.setFocusableInTouchMode(true);
                joinMail.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(joinMail,0);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}