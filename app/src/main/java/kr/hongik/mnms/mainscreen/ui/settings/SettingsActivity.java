package kr.hongik.mnms.mainscreen.ui.settings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.firstscreen.MainActivity;
import kr.hongik.mnms.mainscreen.MainMenuActivity;

public class SettingsActivity extends AppCompatActivity {

    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private RelativeLayout RL_settings_info;
    private LinearLayout LL_settings_first;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent=getIntent();
        loginMember=(Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount=(Account)intent.getSerializableExtra("loginMemberAccount");

        RL_settings_info=findViewById(R.id.RL_settings_info);
        LL_settings_first=findViewById(R.id.LL_settings_first);

        showInfo();
        pwCheck();
    }

    private void showInfo(){
        TextView tv_memName,tv_email;
        Button btn_newPW;
        tv_memName=findViewById(R.id.tv_memName);
        tv_email=findViewById(R.id.tv_email);
        btn_newPW=findViewById(R.id.btn_newPW);
        btn_newPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlPWchange="http://" + loginMember.getIp() + "/member/login";
                String pw1=((TextView)findViewById(R.id.tv_pw)).getText().toString();
                String pw2=((TextView)findViewById(R.id.tv_pw_check)).getText().toString();
                if(pw1.equals(pw2)){
                    NetworkTask networkTask=new NetworkTask();
                    networkTask.setURL(urlPWchange);
                    networkTask.setTAG("pwChange");

                    Map<String,String> params=new HashMap<>();
                    params.put("memID",loginMember.getMemID());
                    params.put("memPW",pw1);

                    networkTask.execute(params);
                }else{
                    showToast("비밀번호가 일치하지않습니다.");
                }
            }
        });
    }

    private void pwCheck(){
        String urlPWCheck = "http://" + loginMember.getIp() + "/member/login";
        String pwFirst=((TextView)findViewById(R.id.tv_first_pw)).getText().toString();

        if(8<=pwFirst.length() && pwFirst.length()<=20){
            //pw확인하기 - 네트워크
            NetworkTask networkTask=new NetworkTask();
            networkTask.setURL(urlPWCheck);
            networkTask.setTAG("pwCheck");

            Map<String,String> params=new HashMap<>();
            params.put("memID",loginMember.getMemID());
            params.put("memPW",loginMember.getMemPW());

            networkTask.execute(params);
        }
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
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
            if(TAG.equals("pwCheck")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        LL_settings_first.setVisibility(View.GONE);
                        RL_settings_info.setVisibility(View.VISIBLE);

                    } else {//비밀번호 확인 실패한 경우
                        showToast("잘못된 비밀번호입니다.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(TAG.equals("pwChange")){
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
