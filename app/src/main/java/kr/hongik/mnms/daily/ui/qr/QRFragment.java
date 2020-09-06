package kr.hongik.mnms.daily.ui.qr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;

import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyGroup;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;


public class QRFragment extends Fragment {
    private ImageView qr_code;
    private String text;
    private DailyGroup dailyGroup;
    private Member loginMember;
    private Account loginMemberAccount;
    String ip;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qr, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");
            loginMember=(Member) bundle.getSerializable("loginMember");
            loginMemberAccount=(Account) bundle.getSerializable("loginMemberAccount");
            ip=bundle.getString("ip");

            qr_code = v.findViewById(R.id.qr_code);
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            text = "OK";

            try{
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("myID",loginMember.getMemID());
                jsonObject.put("myAccount",loginMember.getAccountNum());
                jsonObject.put("myName",loginMember.getMemName());

                Log.d("qr_text",jsonObject.toString());

                BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qr_code.setImageBitmap(bitmap);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return v;
    }
}
