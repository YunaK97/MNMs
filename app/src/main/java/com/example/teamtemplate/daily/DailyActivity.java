package com.example.teamtemplate.daily;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.teamtemplate.Account;
import com.example.teamtemplate.Group;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
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

        Bundle bundle = new Bundle();
        bundle.putSerializable("dailyGroup", dailyGroup);
        bundle.putSerializable("loginMember",member);
        bundle.putSerializable("loginMemberAccount",account);

        viewPager = findViewById(R.id.viewpager);
        DailyPagerAdapter adapter = new DailyPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}