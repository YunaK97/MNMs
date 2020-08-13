package com.example.teamtemplate.mainscreen;

import android.view.View;

public interface OnFriendItemClickListener {
    void onItemClick(FriendListAdapter.ViewHolder holder, View view, int position);
}
