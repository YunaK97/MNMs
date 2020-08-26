package com.example.teamtemplate.daily;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.teamtemplate.Group;
import com.example.teamtemplate.R;
import com.google.android.material.tabs.TabLayout;

public class DailyActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        Intent intent = getIntent();
        Group group = (Group) intent.getSerializableExtra("dailyGroup");

        Bundle bundle = new Bundle();
        bundle.putSerializable("dailyGroup", group);

        viewPager = findViewById(R.id.viewpager);
        DailyPagerAdapter adapter = new DailyPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
}