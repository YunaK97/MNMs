package kr.hongik.mnms.daily;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kr.hongik.mnms.daily.ui.mem.MemberFragment;

import kr.hongik.mnms.daily.ui.dutch.DutchPayFragment;
import kr.hongik.mnms.daily.ui.home.DailyFragment;
import kr.hongik.mnms.daily.ui.qr.QRFragment;

import java.util.ArrayList;

public class DailyPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items = new ArrayList<>();
    private ArrayList<String> tab = new ArrayList<>();

    DailyFragment dailyFragment = new DailyFragment();
    DutchPayFragment dutchPayFragment = new DutchPayFragment();
    MemberFragment memberFragment = new MemberFragment();
    QRFragment qrFragment=new QRFragment();

    public DailyPagerAdapter(@NonNull FragmentManager fm, Bundle bundle) {
        super(fm);
        dailyFragment.setArguments(bundle);
        dutchPayFragment.setArguments(bundle);
        memberFragment.setArguments(bundle);
        qrFragment.setArguments(bundle);

        items.add(dailyFragment);
        items.add(dutchPayFragment);
        items.add(memberFragment);
        items.add(qrFragment);

        tab.add("거래내역");
        tab.add("더치페이");
        tab.add("회원목록");
        tab.add("임시QR");
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

