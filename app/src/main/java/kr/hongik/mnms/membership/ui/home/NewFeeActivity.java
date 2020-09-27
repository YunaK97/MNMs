package kr.hongik.mnms.membership.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.membership.MembershipGroup;

public class NewFeeActivity extends AppCompatActivity {
    //회비 이미 낸사람은 체크하기 안된다고

    private Member loginMember;
    private Account loginMemberaccount;
    private MembershipGroup membershipGroup;

    //layouts
    private TextView TV_newFee_groupName,TV_newFee_fee;
    private EditText TV_newFee_accPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fee);

        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberaccount = (Account) intent.getSerializableExtra("loginMemberAccount");
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");

        showInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_confirm) {
            String accPW=((TextView)findViewById(R.id.TV_newFee_accPW)).getText().toString();
            if(accPW.length()!=4){
                showToast("비밀번호는 4자리 입니다.");
            }else{
                int accoutnPW=Integer.parseInt(accPW);
                submitFee(accoutnPW);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void showInfo(){
        TV_newFee_groupName=findViewById(R.id.TV_newFee_groupName);
        TV_newFee_fee=findViewById(R.id.TV_newFee_fee);

        TV_newFee_fee.setText(membershipGroup.getFee());
        TV_newFee_groupName.setText(membershipGroup.getGroupName());
    }

    private void submitFee(int accountPW){
        String urlSubmitFee="http://"+loginMember.getIp()+"/membership/submit";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("submitFee");
        networkTask.setURL(urlSubmitFee);

        Map<String,String> params=new HashMap<>();
        params.put("memID",loginMember.getMemID());
        params.put("accountNum",loginMember.getAccountNum());
        params.put("fee",membershipGroup.getFee()+"");
        params.put("accountPW",accountPW+"");
        params.put("MID",membershipGroup.getMID()+"");
        params.put("membershipAccountNum",membershipGroup.getAccountNum());

        networkTask.execute(params);
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
            try {
                JSONObject jsonObject=new JSONObject(response);
                boolean success=jsonObject.getBoolean("success");
                if(success){
                    showToast("성공적으로 냄");
                    finish();
                }else{
                    showToast("잉 이미 낸거 아니오?");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}