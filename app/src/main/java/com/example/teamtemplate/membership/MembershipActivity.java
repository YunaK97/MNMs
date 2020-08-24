package com.example.teamtemplate.membership;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.teamtemplate.Group;
import com.example.teamtemplate.R;
import com.example.teamtemplate.membership.ui.home.MembershipFragment;
import com.example.teamtemplate.membership.ui.manage.ManageFeeFragment;
import com.google.android.material.tabs.TabLayout;

public class MembershipActivity extends AppCompatActivity {

        private Context mContext;
        private TabLayout mTabLayout;
        private ViewPager mViewPager;
        private CustomPagerAdapter mCustomPagerAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_membership);
            mContext = getApplicationContext();

            Intent intent = getIntent();
            Group group = (Group)intent.getSerializableExtra("membershipGroup");

            // 추가 시켜 줄 fragment 를 생성
            MembershipFragment membershipFragment=new MembershipFragment();
            ManageFeeFragment manageFeeFragment = new ManageFeeFragment();

            Bundle bundle=new Bundle();
            bundle.putSerializable("membershipGroup", group);

            membershipFragment.setArguments(bundle);
            manageFeeFragment.setArguments(bundle);

            mTabLayout = (TabLayout) findViewById(R.id.layout_tab);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("거래내역")));
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("회비관리")));
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("회원목록")));

            mViewPager = (ViewPager) findViewById(R.id.pager_content);
            mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());

            mCustomPagerAdapter.addItem(membershipFragment);
            mCustomPagerAdapter.addItem(manageFeeFragment);

            mViewPager.setAdapter(mCustomPagerAdapter);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {


                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        private View createTabView(String tabName) {
            View tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            TextView txt_name = (TextView) tabView.findViewById(R.id.txt_name);
            txt_name.setText(tabName);
            return tabView;

        }

    }