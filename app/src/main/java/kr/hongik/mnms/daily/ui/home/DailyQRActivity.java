package kr.hongik.mnms.daily.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.firstscreen.MainActivity;
import kr.hongik.mnms.newprocesses.NewDailyActivity;
import kr.hongik.mnms.newprocesses.NewFriendActivity;
import kr.hongik.mnms.newprocesses.NewMembershipActivity;
import kr.hongik.mnms.newprocesses.NewTransactionActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class DailyQRActivity extends AppCompatActivity {

    //1.친구 사이에서의 QR 인식
    //2.결제 시 QR인식 - url로 결제 창 들어가야함!
    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private ArrayList<Member> memberArrayList;
    private IntentIntegrator qrScan;
    private String urlTransaction;

    //Layouts
    private ImageView qr_code;
    private Button btn_qrScan;

    //variables
    private String qrMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_q_r);

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
        Intent intent=getIntent();
        if (intent != null) {
            dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");
            loginMember = (Member) intent.getSerializableExtra("loginMember");
            loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");
            memberArrayList=(ArrayList<Member>) intent.getSerializableExtra("memberArrayList");

            setTitle(dailyGroup.getGroupName());

            qr_code = findViewById(R.id.daily_qrImage);

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("myID", loginMember.getMemID());
                jsonObject.put("myAccount", loginMember.getAccountNum());
                jsonObject.put("myName", loginMember.getMemName());

                BitMatrix bitMatrix = multiFormatWriter.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qr_code.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private void floatDialog(IntentResult result){
        Member friendMember=new Member();
        try {
            //data를 json으로 변환
            JSONObject jsonObject = new JSONObject(result.getContents());
            //이때 상대방의 ID,이름,계좌번호, 내가 보낼 돈 정보 가져오기

            friendMember.setMemID(jsonObject.getString("myID"));
            friendMember.setMemName(jsonObject.getString("myName"));
            friendMember.setAccountNum(jsonObject.getString("myAccount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(DailyQRActivity.this);
        builder.setTitle("");
    }
}
