package kr.hongik.mnms.daily;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;

import com.google.android.material.tabs.TabLayout;

public class DailyActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        Intent intent = getIntent();
        DailyGroup dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");
        Member member=(Member)intent.getSerializableExtra("loginMember");
        Account account=(Account)intent.getSerializableExtra("loginMemberAccount");
        String ip=intent.getStringExtra("ip");

        Bundle bundle = new Bundle();
        bundle.putSerializable("dailyGroup", dailyGroup);
        bundle.putSerializable("loginMember",member);
        bundle.putSerializable("loginMemberAccount",account);
        bundle.putString("ip",ip);

        viewPager = findViewById(R.id.viewpager);
        DailyPagerAdapter adapter = new DailyPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}