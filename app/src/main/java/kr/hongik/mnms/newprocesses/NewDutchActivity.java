package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.firstscreen.MainActivity;

public class NewDutchActivity extends AppCompatActivity {

    private Member loginMember,friendMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private int sendMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dutch);

        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount= (Account) intent.getSerializableExtra("loginMemberAccount");
        dailyGroup= (DailyGroup) intent.getSerializableExtra("dailyGroup");
        sendMoney=intent.getIntExtra("sendMoney",0);
        String friendID=intent.getStringExtra("friendID");

        friendMember=new Member();
        friendMember.setMemID(friendID);
        getFriendInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String accountPW="1";
        if (item.getItemId() == R.id.new_send){
            accountPW = ((EditText) findViewById(R.id.etAccountPW)).getText().toString();
            if (accountPW.length()!=4){
                showToast("비밀번호는 4자리입니다");
            }else{
                checkAccountPW(accountPW);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void getFriendInfo(){
    String urlFriendInfo = "http://" + loginMember.getIp() + "/membership/notQR";

        NetworkTask networkTask = new NetworkTask();
        Map<String, String> params = new HashMap<>();
        params.put("memID", friendMember.getMemID());

        networkTask.setURL(urlFriendInfo);
        networkTask.setTAG("friendInfo");

        networkTask.execute(params);
    }

    private void setInfo(){
        TextView tvDailyName=findViewById(R.id.tvDailyName);
        tvDailyName.setText(dailyGroup.getGroupName());

        TextView tvReceiveName=findViewById(R.id.tvReceiveName);
        tvReceiveName.setText(friendMember.getMemName());

        TextView tvReceiveAccount=findViewById(R.id.tvReceiveAccount);
        tvReceiveAccount.setText(friendMember.getAccountNum());

        TextView tvReceiveMoney=findViewById(R.id.tvReceiveMoney);
        tvReceiveMoney.setText(sendMoney+"");
    }

    private void checkAccountPW(String accountPW) {
        String urlCheckPW = "http://" + loginMember.getIp();
        Map<String, String> params = new HashMap<>();

        urlCheckPW+="/daily/checkPW";
        params.put("accountPassword", accountPW);
        params.put("accountNum", loginMemberAccount.getAccountNum());

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("checkAccountPW");
        networkTask.setURL(urlCheckPW);

        networkTask.execute(params);
    }

    private void dutchPay() {
        String urlSendMoney = "http://" + loginMember.getIp() + "/daily/transact";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlSendMoney);
        networkTask.setTAG("dutchPay");

        Map<String, String> params = new HashMap<>();

        params.put("money", sendMoney + "");
        params.put("accountNum", friendMember.getAccountNum());
        params.put("myAccountNum", loginMember.getAccountNum());
        params.put("nick", friendMember.getMemName());
        params.put("friendID",friendMember.getMemID());
        params.put("Mynick", loginMember.getMemName());
        params.put("memID",loginMember.getMemID());
        params.put("DID",dailyGroup.getDID()+"");

        networkTask.execute(params);
    }

    private void dutchPayProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String success=jsonObject.getString("success");
            if(success.equals("true")){
                showToast("송금 성공");
                finish();
            } else if (success.equals("false")) {
                showToast("이미 보냈읍니다.");
                finish();
            }else if(success.equals("notfriend")){
                showToast("친구가 아니라 불가합니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAccountPWProcess(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            boolean success=jsonObject.getBoolean("success");
            if(success){
                dutchPay();
            }else{
                showToast("비번이 ㅌㄹ려유");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getFriendInfoProcess(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            String friendName=jsonObject.getString("memName");
            String friendAccount=jsonObject.getString("accountNum");
            friendMember.setMemName(friendName);
            friendMember.setAccountNum(friendAccount);

            setInfo();
        }catch (Exception e){
            e.printStackTrace();
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
            Log.d(TAG, response);
            if (TAG.equals("dutchPay")) {
                dutchPayProcess(response);
            }else if(TAG.equals("checkAccountPW")){
                checkAccountPWProcess(response);
            }else if(TAG.equals("friendInfo")){
                getFriendInfoProcess(response);
            }

        }
    }
}