package kr.hongik.mnms.daily.ui.qr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyGroup;


public class QRFragment extends Fragment {
    private ImageView qr_code;
    private String text;
    private DailyGroup dailyGroup;
    private Member loginMember;
    private Account loginMemberAccount;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qr, container, false);

        Bundle bundle = getArguments();

        return v;
    }
}
