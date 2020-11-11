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
    //휴대폰 변경 -> 인증받기 -> 나중에 구현
    //지난 비밀번호와는 동일하면 안됨 -> 나중에 구현

    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private RelativeLayout RLSettingsInfo;
    private LinearLayout LLSettingsFirst;
    private Button btnMemberOut, btnNewPW, btnSettingsPW;

    //variables
    private boolean validPW = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        RLSettingsInfo = findViewById(R.id.RLSettingsInfo);
        LLSettingsFirst = findViewById(R.id.LLSettingsFirst);
        btnMemberOut = findViewById(R.id.btnMemberOut);
        btnNewPW = findViewById(R.id.btnNewPW);
        btnSettingsPW = findViewById(R.id.btnSettingsPW);

        //회원정보 출력
        showInfo();

        btnNewPW.setOnClickListener(this);
        btnSettingsPW.setOnClickListener(this);
        btnMemberOut.setOnClickListener(this);
    }

    private void showInfo() {
        TextView tvMemName, tvEmail;
        tvMemName = findViewById(R.id.tvMemName);
        tvEmail = findViewById(R.id.tvEmail);
        tvMemName.setText(loginMember.getMemName());
        tvEmail.setText(loginMember.getMemEmail());
    }

    private void pwCheck(String pw) {
        String urlPWCheck = "http://" + loginMember.getIp() + "/member/checkPW";

        if (8 <= pw.length() && pw.length() <= 20) {
            //pw확인하기 - 네트워크
            NetworkTask networkTask = new NetworkTask();
            networkTask.setURL(urlPWCheck);
            networkTask.setTAG("pwCheck");

            Map<String, String> params = new HashMap<>();
            params.put("memID",loginMember.getMemID());
            params.put("memPW", pw);

            networkTask.execute(params);
        }
    }

    private void pwChange(String pw) {
        String urlPWchange = "http://" + loginMember.getIp() + "/member/changePW";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlPWchange);
        networkTask.setTAG("pwChange");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("memPW", pw);

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSettingsPW:
                String pw = ((TextView) findViewById(R.id.tvFirstPW)).getText().toString();
                pwCheck(pw);
                break;
            case R.id.btnNewPW:
                //멤버의 비밀번호 변경
                //memID와 새 memPW를 전송
                //비밀번호 바꾸고 성공여부 받아야함
                String etSettingsPW = ((TextView) findViewById(R.id.etSettingsPW)).getText().toString();
                String etPWCheck = ((TextView) findViewById(R.id.etPWCheck)).getText().toString();
                if(etSettingsPW.length()<8 || etSettingsPW.length()>20) {
                    showToast("비밀번호 : 8~20자");
                    return;
                }
                if (etSettingsPW.equals(etPWCheck)) {
                    pwChange(etSettingsPW);
                } else {
                    showToast("비밀번호가 일치하지않습니다.");
                }
                break;
            case R.id.btnMemberOut:
                //checkMemberOut();
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
                String pw = editText.getText().toString();
                validPW = false;
                pwCheck(pw);
                if (validPW) {
                    //멤버 삭제해야함
                    //멤버가 멤버십의 회장인 경우 탈퇴 불가! : 이거먼저 확인해야함!
                    //해당사항 없으면 멤버가 가입한 그룹 다 나가기
                    //멤버와 친구관계인것 다 없애기
                    //멤버의 정보를 다 null과 같이 무의미한 것으로 바꾸기
                    //멤버의 계좌정보도 그렇게하기!
                    //삭제가 가능하면 ㄱㄱ 안되면 위와같은 순서로 멤버접근을 비활성화시키기기
                    String urlMemberOut = "" + loginMember.getIp() + "";
                    NetworkTask networkTask = new NetworkTask();
                    networkTask.setTAG("memberOUT");
                    networkTask.setURL(urlMemberOut);

                    Map<String, String> params = new HashMap<>();
                    params.put("memID", loginMember.getMemID());


                    networkTask.execute(params);
                } else {
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
            Log.d(TAG, response);
            if (TAG.equals("pwCheck")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        LLSettingsFirst.setVisibility(View.GONE);
                        RLSettingsInfo.setVisibility(View.VISIBLE);

                    } else {//비밀번호 확인 실패한 경우
                        showToast("비밀번호가 틀렸습니다.");
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
            } else if (TAG.equals("memberOUT")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        setResult(MainActivity.TAG_MEMBEROUT, intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
