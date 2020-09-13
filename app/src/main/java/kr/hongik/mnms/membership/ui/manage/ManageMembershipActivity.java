package kr.hongik.mnms.membership.ui.manage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.R;

public class ManageMembershipActivity extends AppCompatActivity {
    //president가 membership 수정, 관리하는 곳
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_membership);
    }
}
