package kr.hongik.mnms.membership.ui.manage;

import android.view.View;

public interface OnMemberListClickListener {
    void onItemClick(MemberListAdapter.ViewHolder holder, View view, int position);
}
