package kr.hongik.mnms.daily;

import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kr.hongik.mnms.daily.ui.home.DailyFragment;
import kr.hongik.mnms.daily.ui.home.PreviewFragment;
import kr.hongik.mnms.daily.ui.mem.DailyMemFragment;

public class DailyPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items = new ArrayList<>();
    private ArrayList<String> tab = new ArrayList<>();

    DailyFragment dailyFragment = new DailyFragment();
    DailyMemFragment memberFragment = new DailyMemFragment();
    PreviewFragment previewFragment=new PreviewFragment();

    public DailyPagerAdapter(@NonNull FragmentManager fm, Bundle bundle) {
        super(fm);
        dailyFragment.setArguments(bundle);
        memberFragment.setArguments(bundle);
        previewFragment.setArguments(bundle);

        items.add(dailyFragment);
        items.add(memberFragment);
        items.add(previewFragment);

        tab.add("거래내역");
        tab.add("회원목록");
        tab.add("정산미리보기");
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

