package kr.hongik.mnms.firstscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.NetworkTask;
import kr.hongik.mnms.ProgressDialog;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.MainMenuActivity;

public class MainActivity extends AppCompatActivity {

    //layouts
    private CheckBox cbAutoLogin;

    //URLs
    private String curIp = "211.186.21.254:8090";

    //variables
    public final static int TAG_SIGNIN = 221, TAG_LOGOUT = 322, TAG_MEMBEROUT = 1515;
    public static int TAG_BACK = 100;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String TAG_SUCCESS = "success";
    private String loginID, loginPW;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("mnms", MODE_PRIVATE);
        editor = preferences.edit();

        progressDialog=new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loginID = "";
        loginPW = "";

        loginID = preferences.getString("loginId", "1");
        loginPW = preferences.getString("loginPw", "1");
        if (!loginID.equals("1") && !loginPW.equals("1")) {
            Member member = new Member();
            member.setMemID(loginID);
            member.setMemPW(loginPW);

            loginProcess(member);
        }

        final Button btnLogin, btnSignin;

        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignin = findViewById(R.id.btnSignin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginID = ((TextView) findViewById(R.id.etID)).getText().toString();
                loginPW = ((TextView) findViewById(R.id.etPW)).getText().toString();
                if (TextUtils.isEmpty(loginID) || TextUtils.isEmpty(loginPW)) {
                    showToast("빈칸 안됩니다!");
                } else if (loginID.length() < 4 || loginID.length() > 20 || loginPW.length()<8|| loginPW.length()>20) {
                    showToast("올바르지 않은 ID이거나 PW입니다.");
                } else {
                    Member member = new Member();
                    member.setMemID(loginID);
                    member.setMemPW(loginPW);

                    loginProcess(member);
                }
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.putExtra("curIp", curIp);
                startActivityForResult(intent, TAG_SIGNIN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == TAG_MEMBEROUT) {
            showToast("멤버탈퇴! 처음으로 돌아갑니다.");
        }
        if (requestCode == TAG_LOGOUT) {
            if (resultCode == TAG_LOGOUT) {
                preferences = getSharedPreferences("mnms", MODE_PRIVATE);
                editor.clear();
                loginID = "";
                loginPW = "";

                editor.commit();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginProcess(final Member member) {
        //로그인을 위해 입력한 ID,PW를 전송
        //결과로 ID에 해당하는 멤버전체 정보와 계좌정보(계좌번호,잔액)를 받아옴
        String urlLogin = "http://" + curIp + "/member/login";
        progressDialog.show();

        final NetworkTask networkTask = new NetworkTask();
        Map<String, String> params = new HashMap<String, String>();
        params.put("memPW", member.getMemPW());
        params.put("memID", member.getMemID());

        networkTask.setURL(urlLogin);

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginCompleteProcess(networkTask.getResponse());
            }
        }, 500);
    }

    private void loginCompleteProcess(String response){
        progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                String name = jsonObject.getString("memName");
                String id = jsonObject.getString("memID");
                String email = jsonObject.getString("memEmail");
                String accNum = jsonObject.getString("accountNum");
                String phoneNumber = jsonObject.getString("phoneNumber");

                Member loginMem = new Member();
                loginMem.setMemName(name);
                loginMem.setMemID(id);
                loginMem.setMemEmail(email);
                loginMem.setAccountNum(accNum);
                loginMem.setIp(curIp);
                loginMem.setPhoneNumber(phoneNumber);

                Account memAcc = new Account();
                memAcc.setAccountNum(accNum);

                cbAutoLogin = findViewById(R.id.cbAutoLogin);

                if (cbAutoLogin.isChecked()) {
                    editor.putString("loginId", loginID);
                    editor.putString("loginPw", loginPW);
                    editor.commit();
                } else {
                    preferences = getSharedPreferences("mnms", MODE_PRIVATE);
                    editor.clear();
                    loginID = "";
                    loginPW = "";
                    editor.commit();
                }
                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                intent.putExtra("loginMember", loginMem);
                intent.putExtra("loginMemberAccount", memAcc);

                startActivityForResult(intent, TAG_LOGOUT);
            } else {//로그인에 실패한 경우
                preferences = getSharedPreferences("mnms", MODE_PRIVATE);
                editor.clear();
                loginID = "";
                loginPW = "";
                editor.commit();
                showToast("잘못된 ID이거나 PW입니다.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private long backKeyPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            showToast("뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.");
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            showToast("이용해주셔서 감사합니다.");
        }
    }

}
