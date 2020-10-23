package kr.hongik.mnms.membership.ui.manage;

import android.view.View;

public interface OnMemberListLongClickListener {
    void onItemLongClick(MemberListAdapter.ViewHolder holder, View view, int position);
}
