package com.example.teamtemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MyViewHolder> {
    private List<MembershipData> mDataset;
    private String myNickName;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is  just a string in this case
        public TextView TextView_nickname;
        public TextView TextView_msg;
        public View rootView;

        public MyViewHolder(View v) {
            super(v);
            TextView_nickname = v.findViewById(R.id.TextView_nickname);
            TextView_msg = v.findViewById(R.id.TextView_msg);
            rootView = v;

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MembershipAdapter(List<MembershipData> myDataset, Context context, String myNickName) {
        mDataset = myDataset;
        this.myNickName = myNickName;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MembershipAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_membership, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        MembershipData mData = mDataset.get(position);

        holder.TextView_nickname.setText(mData.getNickname());
        holder.TextView_msg.setText(mData.getMsg());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }
    public MembershipData getChat(int position) {
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addChat(MembershipData mData) {
        mDataset.add(mData);
        notifyItemInserted(mDataset.size()-1);
    }
}
