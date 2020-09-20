package kr.hongik.mnms.mainscreen.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.firstscreen.MainActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private RelativeLayout RL_settings_info;
    private LinearLayout LL_settings_first;
    private Button btn_memberOut, btn_newPW;

    //variables
    private boolean validPW=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        RL_settings_info = findViewById(R.id.RL_settings_info);
        LL_settings_first = findViewById(R.id.LL_settings_first);
        btn_memberOut = findViewById(R.id.btn_memberOut);
        btn_newPW = findViewById(R.id.btn_newPW);

        showInfo();
        String pwFirst = ((TextView) findViewById(R.id.tv_first_pw)).getText().toString();
        pwCheck(pwFirst);

        btn_newPW.setOnClickListener(this);
        btn_memberOut.setOnClickListener(this);
    }

    private void showInfo() {
        TextView tv_memName, tv_email;
        Button btn_newPW;
        tv_memName = findViewById(R.id.tv_memName);
        tv_email = findViewById(R.id.tv_email);
    }

    private void pwCheck(String pw) {
        String urlPWCheck = "http://" + loginMember.getIp() + "/member/login";

        if (8 <= pw.length() && pw.length() <= 20) {
            //pw확인하기 - 네트워크
            NetworkTask networkTask = new NetworkTask();
            networkTask.setURL(urlPWCheck);
            networkTask.setTAG("pwCheck");

            Map<String, String> params = new HashMap<>();
            params.put("memID", loginMember.getMemID());
            params.put("memPW", loginMember.getMemPW());

            networkTask.execute(params);
        }

        if(validPW){
            LL_settings_first.setVisibility(View.GONE);
            RL_settings_info.setVisibility(View.VISIBLE);
        }else{
           showToast("비밀번호가 틀렸습니다.");
        }
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_newPW:
                //멤버의 비밀번호 변경
                //memID와 새 memPW를 전송
                //비밀번호 바꾸고 성공여부 받아야함
                String urlPWchange = "http://" + loginMember.getIp() + "/member/login";
                String pw1 = ((TextView) findViewById(R.id.tv_pw)).getText().toString();
                String pw2 = ((TextView) findViewById(R.id.tv_pw_check)).getText().toString();
                validPW=false;
                if (pw1.equals(pw2)) {
                    NetworkTask networkTask = new NetworkTask();
                    networkTask.setURL(urlPWchange);
                    networkTask.setTAG("pwChange");

                    Map<String, String> params = new HashMap<>();
                    params.put("memID", loginMember.getMemID());
                    params.put("memPW", pw1);

                    networkTask.execute(params);
                } else {
                    showToast("비밀번호가 일치하지않습니다.");
                }
                break;
            case R.id.btn_memberOut:
                checkMemberOut();
                break;
        }
    }

    private void checkMemberOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

        builder.setTitle("Title");       // 제목 설정
        builder.setMessage("Message");   // 내용 설정

        // EditText 삽입하기
        final EditText editText = new EditText(SettingsActivity.this);
        builder.setView(editText);

        // 취소버튼
        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                // Event
            }
        });

        // 확인 - 탈퇴
        builder.setNeutralButton("탈퇴", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pw=editText.getText().toString();
                validPW=false;
                pwCheck(pw);
                if(validPW){
                    //멤버 삭제해야함
                    //멤버가 멤버십의 회장인 경우 탈퇴 불가! : 이거먼저 확인해야함!
                    //해당사항 없으면 멤버가 가입한 그룹 다 나가기
                    //멤버와 친구관계인것 다 없애기
                    //멤버의 정보를 다 null과 같이 무의미한 것으로 바꾸기
                    //멤버의 계좌정보도 그렇게하기!
                    //삭제가 가능하면 ㄱㄱ 안되면 위와같은 순서로 멤버접근을 비활성화시키기기
                    String urlMemberOut=""+loginMember.getIp()+"";
                    NetworkTask networkTask=new NetworkTask();
                    networkTask.setTAG("memberOUT");
                    networkTask.setURL(urlMemberOut);

                    Map<String,String> params=new HashMap<>();
                    params.put("memID",loginMember.getMemID());

                    networkTask.execute(params);
                }else{
                    showToast("비밀번호가 틀렸습니다.");
                }
                dialog.dismiss();     //닫기
                // Event
            }
        });
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url, TAG;

        void setURL(String url) {
            this.url = url;
        }

        void setTAG(String TAG) {
            this.TAG = TAG;
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
            if (TAG.equals("pwCheck")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        validPW=true;

                    } else {//비밀번호 확인 실패한 경우
                        showToast("잘못된 비밀번호입니다.");
                        validPW=false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("pwChange")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(TAG.equals("memberOUT")){
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                        setResult(MainActivity.TAG_MEMBEROUT,intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
