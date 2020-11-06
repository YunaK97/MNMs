package kr.hongik.mnms.daily.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.hongik.mnms.R;

public class RecSendListAdapter extends RecyclerView.Adapter<RecSendListAdapter.ViewHolder> {
    private ArrayList<RecSend> items=new ArrayList<>();

    public void addItem(RecSend item){
        items.add(item);
    }

    public RecSend getItem(int position){
        return items.get(position);
    }

    public ArrayList<RecSend> getList(){return items;}

    public  void  setItems(ArrayList<RecSend> items){this.items=items;}

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDutchSendID,tvDutchReceiveID,tvDutchMoney;

        ViewHolder(View itemView){
            super(itemView);

            tvDutchSendID=itemView.findViewById(R.id.tvDutchSendID);
            tvDutchReceiveID=itemView.findViewById(R.id.tvDutchReceiveID);
            tvDutchMoney=itemView.findViewById(R.id.tvDutchMoney);

        }

        public void setItem(RecSend item){
            tvDutchSendID.setText(item.getDutchSendID());
            tvDutchReceiveID.setText(item.getDutchReceiveID());
            tvDutchMoney.setText(item.getDutchMoney()+"");
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_recsend_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //viewholder는 재사용된다! 계속 새로 만들순 없다.

        final RecSend item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
