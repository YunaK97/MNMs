package kr.hongik.mnms;

import android.view.View;

public interface OnMemberItemClickListener {
    void onItemClick(MemberAdapter.ViewHolder holder, View view, int position);
}
