package kr.co.area.hashtag.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import kr.co.area.hashtag.R;
import kr.co.area.hashtag.asyncTask.CheckNameTask;

public class ChangeMypage extends AppCompatActivity {
    TextView idView, emailView;
    EditText joinPwd, checkPwd, joinname;
    Button changeButton, checkNamebtn;
    boolean nameCheck = false;
    Activity activity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemypage);

        activity = this;
        idView = (TextView) findViewById(R.id.idtextView);
        emailView = (TextView) findViewById(R.id.emailtextView);
        joinPwd = (EditText) findViewById(R.id.joinpw);
        checkPwd = (EditText) findViewById(R.id.checkjoinpw);
        joinname = (EditText) findViewById(R.id.nickname);
        changeButton = (Button) findViewById(R.id.changeBtn);
        checkNamebtn = (Button) findViewById(R.id.checkbtn2);

        SharedPreferences auto = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        String userId = auto.getString("userId",null);
        String userName = auto.getString("userName",null);
        String userEmail = auto.getString("userEmail", null);

        idView.setText(userId);
        emailView.setText(userEmail);
        joinname.setText(userName);

        checkNamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkName = joinname.getText().toString();
                if (checkName.equals("")) {
                    Toast.makeText(ChangeMypage.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    joinname.setFocusableInTouchMode(true);
                    joinname.requestFocus();
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
                        Toast.makeText(ChangeMypage.this, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
                        nameCheck = true;
                    } else if (state.equals("dup")) {
                        Toast.makeText(ChangeMypage.this, "중복입니다.", Toast.LENGTH_SHORT).show();
                        joinname.setFocusableInTouchMode(true);
                        joinname.requestFocus();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            String userPwd = joinPwd.getText().toString();
            String checkPw = checkPwd.getText().toString();
            @Override
            public void onClick(View v) {
                if (!userPwd.equals(checkPw)) {
                    Toast.makeText(ChangeMypage.this, "비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
                    joinPwd.setFocusableInTouchMode(true);
                    joinPwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinPwd,0);
                    return;
                }
                if (userPwd.equals("")) {
                    Toast.makeText(ChangeMypage.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                    joinPwd.setFocusableInTouchMode(true);
                    joinPwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinPwd,0);
                    return;
                }
                if (checkPw.equals("")) {
                    Toast.makeText(ChangeMypage.this, "비밀번호를 적어주세요.", Toast.LENGTH_SHORT).show();
                    checkPwd.setFocusableInTouchMode(true);
                    checkPwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(joinPwd,0);
                    return;
                }
                /*닉네임 수정 서버*/
//                String checkName = joinname.getText().toString();
//                if (checkName.equals("")) {
//                    Toast.makeText(ChangeMypage.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                try {
//                    String result = new ChangeNameTask(activity).execute(checkName).get();
//                    /*
//                     *  제이슨 구조 해독해서, result 값 알기
//                     * */
//                    JSONObject jObject = new JSONObject(result);
//                    String state = jObject.getString("result");
//                    System.out.println(state);
//                    if (state.equals("avail")) {
//                        Toast.makeText(ChangeMypage.this, "사용가능한 닉네임 입니다.", Toast.LENGTH_SHORT).show();
//                        nameCheck = true;
//                    } else if (state.equals("dup")) {
//                        Toast.makeText(ChangeMypage.this, "중복입니다.", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });


    }
    //뒤로가기
    public void onBackPressed() {
        startActivity(new Intent(ChangeMypage.this, HomeActivity.class));
        finish();
    }

}
