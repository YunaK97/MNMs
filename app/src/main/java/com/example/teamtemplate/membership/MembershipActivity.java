package com.example.teamtemplate.membership;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.teamtemplate.Group;
import com.example.teamtemplate.R;
import com.google.android.material.tabs.TabLayout;

public class MembershipActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        Intent intent = getIntent();
        Group group = (Group) intent.getSerializableExtra("membershipGroup");

        Bundle bundle = new Bundle();
        bundle.putSerializable("membershipGroup", group);

        viewPager = findViewById(R.id.viewpager);
        MembershipPagerAdapter adapter = new MembershipPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
}