package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
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
    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private MembershipGroup membershipGroup;

    private String plus_history, plus_date, mainActivity;
    private int plus_money;
    private boolean QRSend;
    private IntentIntegrator qrScan;

    //variables
    private int TAG_SUCCESS=111;

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
            //intializing scan object
            qrScan = new IntentIntegrator(this);
            qrScan.setPrompt("Scanning...");
            //qrScan.setOrientationLocked(false);
            qrScan.initiateScan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void plusTransaction() {
        plus_date = ((EditText) findViewById(R.id.plus_date)).getText().toString();
        plus_history = ((EditText) findViewById(R.id.plus_history)).getText().toString();
        plus_money=0;
        plus_money = Integer.parseInt(((EditText) findViewById(R.id.plus_money)).getText().toString());
        Transaction newTransaction=new Transaction();
        newTransaction.setSince(plus_date);
        newTransaction.setTransactHistroy(plus_history);
        newTransaction.setTransactMoney(plus_money);
        if (plus_money==0 || plus_history.isEmpty() || plus_date.isEmpty()) {
            showToast("빈칸 노노");
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
        //데일리그룹과 관련된 돈사용 - 멤버계좌에서 돈 사용됨!
        //멤버정보와 transaction 정보 보냄 - 멤버의 계좌 잔액도 변동됨!
        //트랜잭션 생성 후 성공유무 받아야함
        String urlNewDailyTransaction="http://" + loginMember.getIp() + "/newDailyTransaction";
        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("newDailyTransaction");
        networkTask.setURL(urlNewDailyTransaction);

        Map<String, String> params = new HashMap<>();
        params.put("accountNum",newTransaction.getAccountNum());
        params.put("DID",newTransaction.getDID()+"");
        params.put("since",newTransaction.getSince());
        params.put("transactHistory",newTransaction.getTransactHistroy());
        params.put("transactMoney",newTransaction.getTransactMoney()+"");

        networkTask.execute(params);
    }

    private void newMembershipTransaction(Transaction newTransaction){
        //멤버십과 관련된 돈사용 - 멤버십계좌에서 돈 사용됨!
        //멤버십정보와 transaction 정보 보냄 - 멤버십계좌의 잔액이 변동됨
        //트랜잭션 생성 후 성공유무 받아야함
        String urlNewMembershipTransaction="http://" + loginMember.getIp() + "/newMembershipTransaction";
        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("newMembershipTransaction");
        networkTask.setURL(urlNewMembershipTransaction);

        Map<String, String> params = new HashMap<>();
        params.put("accountNum",newTransaction.getAccountNum());
        params.put("MID",newTransaction.getMID()+"");
        params.put("since",newTransaction.getSince());
        params.put("transactHistory",newTransaction.getTransactHistroy());
        params.put("transactMoney",newTransaction.getTransactMoney()+"");

        networkTask.execute(params);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(NewTransactionActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(NewTransactionActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    //data를 json으로 변환
                    JSONObject jsonObject = new JSONObject(result.getContents());
                    //textViewName.setText(obj.getString("name"));
                    //textViewAddress.setText(obj.getString("address"));
                    //돈 사용한 곳 관련 정보 가져오기
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, result.getContents(), Toast.LENGTH_LONG).show();
                    //textViewResult.setText(result.getContents());
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                    if(mainActivity.equals("newDailyTransaction")){
                        Intent intent=new Intent(NewTransactionActivity.this, DailyActivity.class);
                        setResult(TAG_SUCCESS,intent);
                        finish();
                    }else if(mainActivity.equals("newMembershipTransaction")){
                        Intent intent=new Intent(NewTransactionActivity.this, MembershipActivity.class);
                        setResult(TAG_SUCCESS,intent);
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
