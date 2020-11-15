package kr.hongik.mnms.daily.ui.home;

import android.view.View;

import kr.hongik.mnms.daily.ui.dutch.RecSendListAdapter;

public interface OnDailyMemLongClickListener {
    void onItemLongClick(RecSendListAdapter.ViewHolder holder, View view, int position);
}
