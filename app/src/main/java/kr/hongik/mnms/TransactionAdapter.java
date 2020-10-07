package kr.hongik.mnms;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {
    private ArrayList<Transaction> mDataset = new ArrayList<>();
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is  just a string in this case
        public TextView TextView_transLeftMoney;
        public TextView TextView_transHistory;
        public TextView TextView_transMoney;
        public TextView TextView_since;

        public View rootView;

        public MyViewHolder(View v) {
            super(v);
            TextView_transHistory = v.findViewById(R.id.trans_history);
            TextView_transMoney = v.findViewById(R.id.trans_money);
            TextView_since = v.findViewById(R.id.trans_since);
            TextView_transLeftMoney = v.findViewById(R.id.trans_leftmoney);

            rootView = v;
        }
    }

    public void setItems(ArrayList<Transaction> mDataset) {
        this.mDataset = mDataset;
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_transaction, parent, false);
        TransactionAdapter.MyViewHolder vh = new TransactionAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TransactionAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Transaction tData = mDataset.get(position);

        if (tData.getTransactMoney() < 0) {
            holder.TextView_transMoney.setTextColor(Color.BLUE);
        } else {
            holder.TextView_transMoney.setTextColor(Color.RED);
        }

        holder.TextView_transHistory.setText(tData.getTransactHistroy());
        holder.TextView_transMoney.setText(tData.getTransactMoney()+"");
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
        notifyItemInserted(mDataset.size() - 1);
    }
}

