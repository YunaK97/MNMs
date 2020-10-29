package kr.hongik.mnms.daily.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyActivity;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.firstscreen.MainActivity;
import kr.hongik.mnms.newprocesses.NewDailyActivity;
import kr.hongik.mnms.newprocesses.NewFriendActivity;
import kr.hongik.mnms.newprocesses.NewMembershipActivity;
import kr.hongik.mnms.newprocesses.NewTransactionActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DailyQRActivity extends AppCompatActivity {

    //1.친구 사이에서의 QR 인식
    //2.결제 시 QR인식 - url로 결제 창 들어가야함!
    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private ArrayList<Member> memberArrayList;
    private ArrayList<DutchMember> dutchMemberArrayList;

    private IntentIntegrator qrScan;

    //Layouts
    private ImageView qr_code;
    private Button btn_qrScan,btn_qrMake;
    private RecyclerView RVdutchMember,RVeachMemMoney;
    private DutchListAdapter dutchListAdapter;
    private RecSendListAdapter recSendListAdapter;
    private TextView tvTotalMoney;

    private Spinner dutchType;
    private ArrayAdapter dutchTypeAdapter;

    //variables
    private String qrMessage;
    private int perMoney;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_q_r);

        tvTotalMoney=findViewById(R.id.tvTotalMoney);

        //종류 선택
        dutchType = findViewById(R.id.dutchType);
        dutchTypeAdapter = ArrayAdapter.createFromResource(this, R.array.dutch_money, R.layout.support_simple_spinner_dropdown_item);
        dutchTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        dutchType.setAdapter(dutchTypeAdapter);
        dutchType.setSelection(0);

        Intent intent=getIntent();
        if (intent != null) {
            dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");
            loginMember = (Member) intent.getSerializableExtra("loginMember");
            loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");
            memberArrayList=(ArrayList<Member>) intent.getSerializableExtra("memberArrayList");
            dutchMemberArrayList=new ArrayList<>();

            setTitle(dailyGroup.getGroupName());

            //돈 정산 목록 출력
            calculateTotal();
        }

        qrScan=new IntentIntegrator(this);
        btn_qrScan=findViewById(R.id.btn_qrScan);
        btn_qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.setPrompt("Scanning...");
                //qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });

        btn_qrMake=findViewById(R.id.btn_qrMake);
        btn_qrMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qr_code = findViewById(R.id.daily_qrImage);
                if(qr_code.getVisibility()==View.GONE) {
                    btn_qrMake.setText("QR없애기");
                    qr_code.setVisibility(View.VISIBLE);

                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("myID", loginMember.getMemID());
                        jsonObject.put("myAccount", loginMember.getAccountNum());
                        jsonObject.put("myName", loginMember.getMemName());
                        //내가 받을 돈

                        BitMatrix bitMatrix = multiFormatWriter.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        qr_code.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    btn_qrMake.setText("QR생성");
                    qr_code.setVisibility(View.GONE);
                }
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(DailyQRActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(DailyQRActivity.this, "스캔완료!"+result.getContents(), Toast.LENGTH_SHORT).show();

                floatDialog(result);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void calculateTotal(){
        String urlCalculateTotal = "http://" + loginMember.getIp() + "/daily/result";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlCalculateTotal);
        networkTask.setTAG("calculateTotal");

        Map<String, String> params = new HashMap<>();
        params.put("DID", dailyGroup.getDID()+"");

        networkTask.execute(params);
    }

    private void floatDialog(IntentResult result){
        Member friendMember=new Member();
        try {
            //data를 json으로 변환
            JSONObject jsonObject = new JSONObject(result.getContents());
            //이때 상대방의 ID,이름,계좌번호, 내가 보낼 돈 정보 가져오기

            friendMember.setMemID(jsonObject.getString("myID"));
            friendMember.setMemName(jsonObject.getString("myName"));
            friendMember.setAccountNum(jsonObject.getString("myAccount"));
            //내가 보낼 돈 찾기

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(DailyQRActivity.this);
        builder.setTitle("송금하시겠습니까?");
    }

    private void calculateTotalProcess(String response){
        Map<String,String> memberMap=new HashMap<>();

        RVdutchMember=findViewById(R.id.rvDutchMembers);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        RVdutchMember.setLayoutManager(layoutManager);
        dutchListAdapter=new DutchListAdapter();


        try {
            JSONObject jsonObject=new JSONObject(response);
            int memSize=Integer.parseInt(jsonObject.getString("memSize"));


            for(int i=0;i<memSize;i++){
                String memID=jsonObject.getString("memID"+i);
                String money=jsonObject.getString("money"+i);

                memberMap.put(memID,money);
            }

            String totalMoney=jsonObject.getString("total");
            tvTotalMoney.setText(totalMoney);
            perMoney=Integer.parseInt(totalMoney) / memberArrayList.size();

        }catch (Exception e){
            e.printStackTrace();
        }

        for(int i=0;i<memberArrayList.size();i++){
            Member member=memberArrayList.get(i);
            DutchMember dutchMember=new DutchMember();
            dutchMember.setMemID(member.getMemID());
            dutchMember.setMemName(member.getMemName());
            String money=memberMap.get(dutchMember.getMemID());
            if(money==null){
                dutchMember.setUsedMoney(0);
                dutchMember.setRsMoney(-perMoney);
            }else {
                dutchMember.setUsedMoney(Integer.parseInt(money));
                dutchMember.setRsMoney(Integer.parseInt(money)-perMoney);
            }

            dutchMemberArrayList.add(dutchMember);

            Comparator<DutchMember> noAsc = new Comparator<DutchMember>() {
                @Override
                public int compare(DutchMember item1, DutchMember item2) {
                   return item1.getRsMoney()>item2.getRsMoney() ? 1:-1;
                }
            };
            Collections.sort(dutchMemberArrayList, noAsc);

            dutchListAdapter.setItems(dutchMemberArrayList);
        }


        RVdutchMember.setAdapter(dutchListAdapter);

        calculateEachMember();

    }

    private void  calculateEachMember(){
        RVeachMemMoney=findViewById(R.id.rvEachMemMoney);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RVeachMemMoney.setLayoutManager(layoutManager);
        recSendListAdapter=new RecSendListAdapter();

        //누가 누구에게 얼마주는지 쓰기
        for(DutchMember mainMember:dutchMemberArrayList){
            //mainMember : 주는 사람(돈 적게 쓴 사람)
            // -> 돈 받을 사람한테 줘야함
            int totalSend=-mainMember.getRsMoney();
            if(totalSend<=0){continue;}

            for (int i=dutchMemberArrayList.size()-1;i>=0;i--) {
                //돈 받을 사람
                DutchMember receiveMem = dutchMemberArrayList.get(i);
                int recMoney = receiveMem.getRsMoney();
                if(recMoney-totalSend >0){
                    //아직 더 받아야함 - totalSend 끝
                    RecSend recSend=new RecSend();
                    recSend.setDutchMoney(totalSend);
                    recSend.setDutchSendID(mainMember.getMemID());
                    recSend.setDutchReceiveID(receiveMem.getMemID());
                    recSendListAdapter.addItem(recSend);
                    break;
                }else if(recMoney-totalSend <0){
                    //줄 돈 아직 여유 있음
                    totalSend-=recMoney;
                    RecSend recSend=new RecSend();
                    recSend.setDutchReceiveID(receiveMem.getMemID());
                    recSend.setDutchSendID(mainMember.getMemID());
                    recSend.setDutchMoney(recMoney);
                    recSendListAdapter.addItem(recSend);
                }else{
                    //주고 받기가 깔금
                    RecSend recSend=new RecSend();
                    recSend.setDutchReceiveID(receiveMem.getMemID());
                    recSend.setDutchSendID(mainMember.getMemID());
                    recSend.setDutchMoney(recMoney);
                    totalSend=0;
                    recSendListAdapter.addItem(recSend);
                    break;
                }
            }
        }

        RVeachMemMoney.setAdapter(recSendListAdapter);
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
            Log.d(TAG,response);
            if (TAG.equals("calculateTotal")) {
                calculateTotalProcess(response);
            }

        }
    }

    public class DutchMember extends Member{
        private int usedMoney,rsMoney;

        public int getUsedMoney() {
            return usedMoney;
        }

        public void setUsedMoney(int usedMoney) {
            this.usedMoney = usedMoney;
        }

        public int getRsMoney() {
            return rsMoney;
        }

        public void setRsMoney(int rsMoney) {
            this.rsMoney = rsMoney;
        }
    }
}

