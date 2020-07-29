package com.example.teamtemplate.mainscreen;

import android.view.View;

import com.example.teamtemplate.mainscreen.FriendListAdapter;

public interface OnFriendItemClickListener {
    public void onItemClick(FriendListAdapter.ViewHolder holder, View view, int position);
}
