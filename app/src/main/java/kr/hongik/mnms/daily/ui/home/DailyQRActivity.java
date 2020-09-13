package kr.hongik.mnms.daily.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyGroup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.util.ArrayList;

public class DailyQRActivity extends AppCompatActivity {

    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private ImageView qr_code;

    //variables
    private String qrMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_q_r);

        Intent intent=getIntent();
        if (intent != null) {
            dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");
            loginMember = (Member) intent.getSerializableExtra("loginMember");
            loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");
            memberArrayList=(ArrayList<Member>) intent.getSerializableExtra("memberArrayList");

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
}
