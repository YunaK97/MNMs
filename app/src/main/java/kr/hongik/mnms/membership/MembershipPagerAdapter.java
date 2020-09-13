package kr.hongik.mnms.membership;

import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kr.hongik.mnms.membership.ui.home.MembershipFragment;
import kr.hongik.mnms.membership.ui.manage.ManageFeeFragment;
import kr.hongik.mnms.membership.ui.manage.MembershipMemFragment;

public class MembershipPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items = new ArrayList<>();
    private ArrayList<String> tab = new ArrayList<>();

    MembershipFragment membershipFragment = new MembershipFragment();
    ManageFeeFragment manageFeeFragment = new ManageFeeFragment();
    MembershipMemFragment manageMemFragment = new MembershipMemFragment();

    public MembershipPagerAdapter(@NonNull FragmentManager fm, Bundle bundle) {
        super(fm);
        membershipFragment.setArguments(bundle);
        manageFeeFragment.setArguments(bundle);
        manageMemFragment.setArguments(bundle);

        items.add(membershipFragment);
        items.add(manageFeeFragment);
        items.add(manageMemFragment);

        tab.add("거래내역");
        tab.add("회비관리");
        tab.add("회원목록");

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tab.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

}
