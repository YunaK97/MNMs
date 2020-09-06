package kr.hongik.mnms.mainscreen;

import android.view.View;

public interface OnGroupItemLongClickListener {
    void onItemLongClick(GroupAdapter.ViewHolder holder, View view, int position);
}
