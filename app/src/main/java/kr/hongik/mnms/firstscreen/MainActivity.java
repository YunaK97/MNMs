package kr.hongik.mnms.firstscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.MainMenuActivity;

public class MainActivity extends AppCompatActivity {
    //layouts
    private CheckBox autoLogin;

    //URLs
    private String curIp = "211.186.21.254:8090";

    //variables
    public final static int TAG_SIGNIN = 221,TAG_LOGOUT=322,TAG_MEMBEROUT=1515;
    public static int TAG_BACK=100;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    private String TAG_SUCCESS = "success";
    private String id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button login, signin;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        String tmpId = preferences.getString("loginId", "");
        String tmpPw = preferences.getString("loginPw", "");
        if (!tmpId.equals("") && !tmpPw.equals("")) {
            Member member = new Member();
            member.setMemID(tmpId);
            member.setMemPW(tmpPw);
            loginProcess(member);
        }

        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        signin = findViewById(R.id.signin);
        autoLogin = findViewById(R.id.autoLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = ((TextView) findViewById(R.id.id)).getText().toString();
                pw = ((TextView) findViewById(R.id.pw)).getText().toString();
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pw)) {
                    showToast("빈칸 노노!");
                } else {
                    Member member = new Member();
                    member.setMemID(id);
                    member.setMemPW(pw);
                    loginProcess(member);
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
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
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==TAG_MEMBEROUT){
            showToast("멤버탈퇴! 처음으로 돌아갑니다.");
        }
        if (requestCode == TAG_LOGOUT) {
            if(resultCode==TAG_LOGOUT) {
                editor.clear();
                editor.commit();
            }
        }
    }

    private void loginProcess(final Member member) {
        //로그인을 위해 입력한 ID,PW를 전송
        //결과로 ID에 해당하는 멤버전체 정보와 계좌정보(계좌번호,잔액)를 받아옴
        String urlLogin = "http://" + curIp + "/member/login";

        NetworkTask networkTask = new NetworkTask();
        Map<String, String> params = new HashMap<String, String>();
        params.put("memPW", member.getMemPW());
        params.put("memID", member.getMemID());

        networkTask.setURL(urlLogin);

        networkTask.execute(params);
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

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;

        void setURL(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터

            // Http 요청 준비 작업
            HttpClient.Builder http = new HttpClient.Builder("POST", url);

            // Parameter 를 전송한다.
            http.addAllParameters(maps[0]);

            //Http 요청 전송
            HttpClient post = http.create();
            post.request();
            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();
            // 응답 본문 가져오기

            return post.getBody();
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                if (success) {
                    String name = jsonObject.getString("memName");
                    String id = jsonObject.getString("memID");
                    String pw = jsonObject.getString("memPW");
                    String email = jsonObject.getString("memEmail");
                    String accNum = jsonObject.getString("accountNum");
                    String accBalance = jsonObject.getString("accountBalance");
                    String ssn=jsonObject.getString("memSsn");
                    String phoneNumber=jsonObject.getString("phoneNumber");

                    Member loginMem = new Member();
                    loginMem.setMemName(name);
                    loginMem.setMemID(id);
                    loginMem.setMemPW(pw);
                    loginMem.setMemEmail(email);
                    loginMem.setAccountNum(accNum);
                    loginMem.setIp(curIp);
                    loginMem.setMemSsn(ssn);
                    loginMem.setPhoneNumber(phoneNumber);

                    Account memAcc = new Account();
                    memAcc.setAccountNum(accNum);
                    int balance = Integer.parseInt(accBalance);
                    memAcc.setAccountBalance(balance);

                    if (autoLogin.isChecked()) {
                        editor.putString("loginId", loginMem.getMemID());
                        editor.putString("loginPw", loginMem.getMemID());
                        editor.commit();
                    } else {
                        editor.clear();
                        editor.commit();
                    }
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    intent.putExtra("loginMember", loginMem);
                    intent.putExtra("loginMemberAccount", memAcc);

                    startActivityForResult(intent, TAG_LOGOUT);
                } else {//로그인에 실패한 경우
                    showToast("로그인에 실패했습니다.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
