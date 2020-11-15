package kr.hongik.mnms.newprocesses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.NetworkTask;
import kr.hongik.mnms.ProgressDialog;
import kr.hongik.mnms.R;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendMoneyActivity extends AppCompatActivity {
    /*
     * 1. 친구 정보 가져옴
     * 2. 친구 정보 출력
     * 3. 송금 버튼 클릭시
     *   4. 비밀번호, 송금 금액 확인
     *   5. 비밀번호 일치여부 확인
     * 6. 돈 송금
     * */
    private Member loginMember, friendMember;
    private Account loginMemberAccount;
    private ProgressDialog progressDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_send) {
            String etSendMoney, etSendPW;
            etSendMoney = ((EditText) findViewById(R.id.etSendMoney)).getText().toString();
            etSendPW = ((EditText) findViewById(R.id.etSendPW)).getText().toString();

            if (etSendMoney.length() <= 0) {
                showToast("얼마 송금?");
            } else {
                if (etSendPW.length() != 4) {
                    showToast("비밀번호는 4자리입니다");
                } else {
                    progressDialog.show();
                    checkAccountPW(etSendPW);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        progressDialog=new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();

        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");
        friendMember = new Member();
        String friendId, friendName;
        friendId = intent.getStringExtra("friendID");
        friendName = intent.getStringExtra("friendName");
        friendMember.setMemID(friendId);
        friendMember.setMemName(friendName);

        getfriendAccount();
    }

    private void getfriendAccount() {
        String urlGetfriendAccount = "http://" + loginMember.getIp() + "/membership/notQR";
        Map<String, String> params = new HashMap<>();

        params.put("memID", friendMember.getMemID());

        final NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("getfriendAccount");
        networkTask.setURL(urlGetfriendAccount);

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getfriendAccountProcess(networkTask.getResponse());
            }
        }, 1500);

    }

    private void checkAccountPW(String accountPW) {
        String urlCheckPW = "http://" + loginMember.getIp() + "/daily/checkPW";
        Map<String, String> params = new HashMap<>();

        params.put("accountPassword", accountPW);
        params.put("accountNum", loginMemberAccount.getAccountNum());

        final NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("checkAccountPW");
        networkTask.setURL(urlCheckPW);

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAccountPWProcess(networkTask.getResponse());
            }
        }, 1500);
    }

    private void checkAccountPWProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                sendmoneyToFriend();
            } else {
                showToast("비번이 ㅌㄹ려유");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendmoneyToFriend() {
        String urlSendmoneyTofriend = "http://" + loginMember.getIp() + "/member/pay";

        final NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlSendmoneyTofriend);
        networkTask.setTAG("sendmoneyTofriend");

        Map<String, String> params = new HashMap<>();

        String etSendMoney;
        etSendMoney = ((EditText) findViewById(R.id.etSendMoney)).getText().toString();
        String history1 = ((EditText) findViewById(R.id.etSendHistory1)).getText().toString();
        String history2 = ((EditText) findViewById(R.id.etSendHistory2)).getText().toString();
        if (history1.length() <= 0) {
            history1 = friendMember.getMemName();
        }

        if (history2.length() <= 0) {
            history2 = loginMember.getMemName();
        }

        params.put("money", etSendMoney);
        params.put("friendAccount", friendMember.getAccountNum());
        params.put("accountNum", loginMember.getAccountNum());
        params.put("history1", history1);
        params.put("history2", history2);

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                String response=networkTask.getResponse();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean("success");
                    if(success){
                        showToast("송금 성공!");
                        finish();
                    }else{
                        showToast("앗 실수! 에러났당");
                    }
                }catch (Exception e){

                }
            }
        }, 1500);
    }

    private void getfriendAccountProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                String friendAcc = jsonObject.getString("accountNum");
                friendMember.setAccountNum(friendAcc);

                showInfo();
            } else {
                showToast("앗 실패,,다시 시도!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInfo() {
        TextView tvSendName, tvSendAccount;
        tvSendName = findViewById(R.id.tvSendName);
        tvSendAccount = findViewById(R.id.tvSendAccount);

        tvSendName.setText(friendMember.getMemName());
        tvSendAccount.setText(friendMember.getAccountNum());
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }


//    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
//        protected String url, TAG;
//
//        void setURL(String url) {
//            this.url = url;
//        }
//
//        void setTAG(String TAG) {
//            this.TAG = TAG;
//        }
//
//        @Override
//        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터
//
//            // Http 요청 준비 작업
//            HttpClient.Builder http = new HttpClient.Builder("POST", url);
//
//            // Parameter 를 전송한다.
//            http.addAllParameters(maps[0]);
//
//            //Http 요청 전송
//            HttpClient post = http.create();
//            post.request();
//            // 응답 상태코드 가져오기
//            int statusCode = post.getHttpStatusCode();
//            // 응답 본문 가져오기
//
//            return post.getBody();
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            Log.d(TAG, response);
//            if (TAG.equals("checkAccountPW")) {
//                checkAccountPWProcess(response);
//            } else if (TAG.equals("getfriendAccount")) {
//                getfriendAccountProcess(response);
//            }else if(TAG.equals("sendmoneyTofriend")){
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    boolean success=jsonObject.getBoolean("success");
//                    if(success){
//                        showToast("송금 성공!");
//                        finish();
//                    }else{
//                        showToast("앗 실수! 에러났당");
//                    }
//                }catch (Exception e){
//
//                }
//            }
//        }
//    }
}