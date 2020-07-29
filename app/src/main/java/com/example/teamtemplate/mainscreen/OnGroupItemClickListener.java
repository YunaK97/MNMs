package com.example.teamtemplate.mainscreen;

import android.view.View;

import com.example.teamtemplate.mainscreen.GroupAdapter;

public interface OnGroupItemClickListener {
    public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position);
}