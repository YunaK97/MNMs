package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.Transaction;
import kr.hongik.mnms.daily.DailyActivity;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.membership.MembershipActivity;
import kr.hongik.mnms.membership.MembershipGroup;

public class NewTransactionActivity extends AppCompatActivity {
    private String plus_money, plus_history, plus_date, mainActivity;
    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private MembershipGroup membershipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount=(Account)intent.getSerializableExtra("loginMemberAccount");
        mainActivity=intent.getStringExtra("mainActivity");
        if(mainActivity.equals("daily")){
            dailyGroup=(DailyGroup)intent.getSerializableExtra("dailyGroup");
        }else if(mainActivity.equals("membership")){
            membershipGroup=(MembershipGroup)intent.getSerializableExtra("membershipGroup");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_trans_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_confirm) {
            plusTransaction();
            return true;
        }
        if (item.getItemId() == R.id.new_qr) {
            showToast("qr찍기 구현중!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void plusTransaction() {
        plus_date = ((EditText) findViewById(R.id.plus_date)).getText().toString();
        plus_history = ((EditText) findViewById(R.id.plus_history)).getText().toString();
        plus_money = ((EditText) findViewById(R.id.plus_money)).getText().toString();
        Transaction newTransaction=new Transaction();
        newTransaction.setSince(plus_date);
        newTransaction.setTransactHistroy(plus_history);
        newTransaction.setTransactMoney(plus_money);
        if (plus_money.isEmpty() || plus_history.isEmpty() || plus_date.isEmpty()) {
            showToast("빈칸 노노");
            return;
        } else {
            //실시간으로 팀원들이 돈 사용을 허락해야함?!?
            showToast("아직 미구현");
            if(mainActivity.equals("daily")){
                newTransaction.setAccountNum(loginMember.getAccountNum());
                newTransaction.setDID(dailyGroup.getDID());
                newDailyTransaction(newTransaction);
            }else if(mainActivity.equals("membership")){
                newTransaction.setAccountNum(membershipGroup.getAccountNum());
                newTransaction.setMID(dailyGroup.getGID());
                newMembershipTransaction(newTransaction);
            }
        }
    }

    private void newDailyTransaction(Transaction newTransaction){
        String urlNewDailyTransaction="http://" + loginMember.getIp() + "/newDailyTransaction";
        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("newDailyTransaction");
        networkTask.setURL(urlNewDailyTransaction);

        Map<String, String> params = new HashMap<>();
        params.put("accountNum",newTransaction.getAccountNum());
        params.put("DID",newTransaction.getDID());
        params.put("since",newTransaction.getSince());
        params.put("transactionHistory",newTransaction.getTransactHistroy());
        params.put("transactionMoney",newTransaction.getTransactMoney());

        networkTask.execute(params);
    }

    private void newMembershipTransaction(Transaction newTransaction){
        String urlNewMembershipTransaction="http://" + loginMember.getIp() + "/newMembershipTransaction";
        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("newMembershipTransaction");
        networkTask.setURL(urlNewMembershipTransaction);

        Map<String, String> params = new HashMap<>();
        params.put("accountNum",newTransaction.getAccountNum());
        params.put("MID",newTransaction.getMID());
        params.put("since",newTransaction.getSince());
        params.put("transactionHistory",newTransaction.getTransactHistroy());
        params.put("transactionMoney",newTransaction.getTransactMoney());

        networkTask.execute(params);
    }

    protected void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        String TAG;

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
            try{
                JSONObject jsonObject=new JSONObject(response);
                boolean success=jsonObject.getBoolean("success");
                if(success){
                    showToast("추가 완료");
                    if(mainActivity.equals("daily")){
                        Intent intent=new Intent(NewTransactionActivity.this, DailyActivity.class);
                        setResult(111,intent);
                        finish();
                    }else if(mainActivity.equals("membership")){
                        Intent intent=new Intent(NewTransactionActivity.this, MembershipActivity.class);
                        setResult(111,intent);
                        finish();
                    }
                }else {
                    showToast("추가 실패");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
