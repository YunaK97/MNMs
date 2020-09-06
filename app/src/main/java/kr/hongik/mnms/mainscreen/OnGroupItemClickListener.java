package kr.hongik.mnms.mainscreen;

import android.view.View;

public interface OnGroupItemClickListener {
    void onItemClick(GroupAdapter.ViewHolder holder, View view, int position);
}
