package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    /*
     * 사용한 돈, 사용한 곳, 그룹들 불러오기
     * 멤버십 사용 - 회장만 가능 - newTransaction
     * 회비 내기 - 회장,부원 - newFee
     * 데일리 사용 - 외부 사용 적어두기 (외부 : 임시 QR 발급(임시 계좌번호로 송금하는 형태)) - newTransaction
     * 데일리 - 더치페이 -> 상대방 이름 선택 -newMoneyTOFriend
     *
     *
     *
     *
     *
     *
     *
     * */

    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private MembershipGroup membershipGroup;

    private String etPlusHistory, mainActivity;
    private int etPlusMoney;
    private boolean QRSend;
    private IntentIntegrator qrScan;

    //layouts
    private TextView tvNewTransDailyName;
    private LinearLayout LLNewTransDaily, LLNewTransMembership;

    //variables
    public int TAG_TRANS_SUCCESS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");
        mainActivity = intent.getStringExtra("mainActivity");

        LLNewTransDaily = findViewById(R.id.LLNewTransDaily);
        LLNewTransMembership = findViewById(R.id.LLNewTransMembership);
        if (mainActivity.equals("daily")) {
            dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");

            tvNewTransDailyName=findViewById(R.id.tvNewTransDailyName);
            tvNewTransDailyName.setText(dailyGroup.getGroupName());
            LLNewTransDaily.setVisibility(View.VISIBLE);
            LLNewTransMembership.setVisibility(View.GONE);

        } else if (mainActivity.equals("membership")) {
            membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");

            LLNewTransMembership.setVisibility(View.VISIBLE);
            LLNewTransDaily.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String accountPW="1";
        if (item.getItemId() == R.id.new_confirm) {
            if(mainActivity.equals("daily")){
                accountPW = ((EditText) findViewById(R.id.etNewTransDailyPW)).getText().toString();
            }else {
                accountPW = ((EditText) findViewById(R.id.etNewTransMembershipPW)).getText().toString();
            }
            checkAccountPW(accountPW);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAccountPW(String accountPW) {
        String urlCheckPW = "http://" + loginMember.getIp();
        Map<String, String> params = new HashMap<>();

        if(mainActivity.equals("daily")){
            urlCheckPW+="/daily/checkPW";
            params.put("accountPassword", accountPW);
            params.put("accountNum", loginMemberAccount.getAccountNum());
        }else{
            urlCheckPW+="/membership/checkPW";
            params.put("accountPassword", accountPW);
            params.put("accountNum", membershipGroup.getAccountNum());
        }

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("checkAccountPW");
        networkTask.setURL(urlCheckPW);

        networkTask.execute(params);
    }

    private void newDailyTransaction(Transaction newTransaction) {
        //데일리그룹과 관련된 돈사용 - 멤버계좌에서 돈 사용됨!
        //멤버정보와 transaction 정보 보냄 - 멤버의 계좌 잔액도 변동됨!
        //트랜잭션 생성 후 성공유무 받아야함
        String urlNewDailyTransaction = "http://" + loginMember.getIp() + "/daily/pay";
        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("newDailyTransaction");
        networkTask.setURL(urlNewDailyTransaction);

        Map<String, String> params = new HashMap<>();
        params.put("accountNum", newTransaction.getAccountNum());
        params.put("DID", newTransaction.getDID() + "");
        params.put("history", newTransaction.getTransactHistroy());
        params.put("money", newTransaction.getTransactMoney() + "");

        networkTask.execute(params);
    }

    private void newMembershipTransaction(Transaction newTransaction) {
        //멤버십과 관련된 돈사용 - 멤버십계좌에서 돈 사용됨!
        //멤버십정보와 transaction 정보 보냄 - 멤버십계좌의 잔액이 변동됨
        //트랜잭션 생성 후 성공유무 받아야함
        String urlNewMembershipTransaction = "http://" + loginMember.getIp() + "/membership/transaction";
        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("newMembershipTransaction");
        networkTask.setURL(urlNewMembershipTransaction);

        Map<String, String> params = new HashMap<>();
        params.put("accountNum", newTransaction.getAccountNum());
        params.put("MID", newTransaction.getMID() + "");
        params.put("history", newTransaction.getTransactHistroy());
        params.put("money", newTransaction.getTransactMoney() + "");

        networkTask.execute(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void newTransaction(){
        Transaction newTransaction = new Transaction();

        etPlusHistory = ((EditText) findViewById(R.id.etPlusHistory)).getText().toString();
        etPlusMoney = Integer.parseInt(((EditText) findViewById(R.id.etPlusMoney)).getText().toString());

        newTransaction.setTransactHistroy(etPlusHistory);
        newTransaction.setTransactMoney(etPlusMoney);

        if (etPlusMoney == 0 || etPlusHistory.isEmpty()) {
            showToast("빈칸 노노");
        } else {
            if (mainActivity.equals("daily")) {
                newTransaction.setAccountNum(loginMember.getAccountNum());
                newTransaction.setDID(dailyGroup.getDID());
                newDailyTransaction(newTransaction);
            } else if (mainActivity.equals("membership")) {
                newTransaction.setAccountNum(membershipGroup.getAccountNum());
                newTransaction.setMID(membershipGroup.getMID());
                newMembershipTransaction(newTransaction);
            }
        }
    }

    private void newTransactionProcess(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                if (mainActivity.equals("daily")) {
                    Intent intent = new Intent(NewTransactionActivity.this, DailyActivity.class);
                    setResult(TAG_TRANS_SUCCESS, intent);
                    finish();
                } else if (mainActivity.equals("membership")) {
                    Intent intent = new Intent(NewTransactionActivity.this, MembershipActivity.class);
                    setResult(TAG_TRANS_SUCCESS, intent);
                    finish();
                }
            } else {
                showToast("추가 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.d(TAG,response);
            if (TAG.equals("checkAccountPW")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        newTransaction();
                    } else {
                        showToast("계좌비밀번호 오류");
                    }
                } catch (Exception e) {

                }
            } else if (TAG.equals("newDailyTransaction") || TAG.equals("newMembershipTransaction")) {
                newTransactionProcess(response);
            }
        }
    }
}
