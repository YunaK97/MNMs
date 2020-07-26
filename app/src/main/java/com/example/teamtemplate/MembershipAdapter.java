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
    private List<Transaction> mDataset;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is  just a string in this case
        public TextView TextView_accountNum;
        public TextView TextView_transID;
        public TextView TextView_transHistory;
        public TextView TextView_transMoney;
        public TextView TextView_transVersion;
        public TextView TextView_since;

        public View rootView;

        public MyViewHolder(View v) {
            super(v);
            TextView_accountNum = v.findViewById(R.id.TextView_accountNum);
            TextView_transID = v.findViewById(R.id.TextView_transId);
            TextView_transHistory = v.findViewById(R.id.TextView_transHistory);
            TextView_transMoney = v.findViewById(R.id.TextView_transMoney);
            TextView_transVersion = v.findViewById(R.id.TextView_transVersion);
            TextView_since = v.findViewById(R.id.TextView_since);
            rootView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MembershipAdapter(List<Transaction> myDataset, Context context) {
        mDataset = myDataset;
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
        Transaction tData = mDataset.get(position);

        holder.TextView_accountNum.setText(tData.getAccountNum());
        holder.TextView_transID.setText(tData.getTransactID());
        holder.TextView_transHistory.setText(tData.getTransactHistroy());
        holder.TextView_transMoney.setText(tData.getTransactMoney());
        holder.TextView_transVersion.setText(tData.getTransactVersion());
        holder.TextView_since.setText(tData.getSince());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }
    public Transaction getChat(int position) {
        return mDataset != null ? mDataset.get(position) : null;
    }

    public void addItem(Transaction tData) {
        mDataset.add(tData);
        notifyItemInserted(mDataset.size()-1);
    }
}
