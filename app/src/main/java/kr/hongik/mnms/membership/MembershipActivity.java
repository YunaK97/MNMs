package kr.hongik.mnms.membership;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;

import com.google.android.material.tabs.TabLayout;

public class MembershipActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        Intent intent = getIntent();
        MembershipGroup membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        Member member=(Member)intent.getSerializableExtra("loginMember");
        Account account=(Account)intent.getSerializableExtra("loginMemberAccount");
        String ip=intent.getStringExtra("ip");

        Bundle bundle = new Bundle();
        bundle.putSerializable("membershipGroup", membershipGroup);
        bundle.putSerializable("loginMember",member);
        bundle.putSerializable("loginMemberAccount",account);
        bundle.putString("ip",ip);

        viewPager = findViewById(R.id.viewpager);
        MembershipPagerAdapter adapter = new MembershipPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
}