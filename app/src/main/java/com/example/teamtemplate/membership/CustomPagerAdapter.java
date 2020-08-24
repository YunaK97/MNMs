package com.example.teamtemplate.membership;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.teamtemplate.membership.ui.home.MembershipFragment;
import com.example.teamtemplate.membership.ui.manage.ManageFeeFragment;
import com.example.teamtemplate.membership.ui.manage.ManageMemFragment;

import java.util.ArrayList;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;
    ArrayList<Fragment> items = new ArrayList<Fragment>();

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
        //this.mPageCount = pageCount;
    }

//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                MembershipFragment membershipFragment = new MembershipFragment();
//                return membershipFragment;
//
//            case 1:
//                ManageFeeFragment manageFeeFragment = new ManageFeeFragment();
//                return manageFeeFragment;
//
//            case 2:
//                ManageMemFragment manageMemFragment = new ManageMemFragment();
//                return manageMemFragment;
//
//            default:
//                return null;
//        }
//    }

    @Override
    public Fragment getItem(int position) {
         return items.get(position);
    }

    @Override
    public int getCount() {
        //return mPageCount;
        return items.size();
    }

    public void addItem(Fragment item) {
        items.add(item);
    }
}
