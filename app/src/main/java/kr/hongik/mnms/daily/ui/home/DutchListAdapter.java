package kr.hongik.mnms.daily.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.hongik.mnms.R;

public class DutchListAdapter extends RecyclerView.Adapter<DutchListAdapter.ViewHolder> {
    private ArrayList<DailyQRActivity.DutchMember> items=new ArrayList<>();

    public void addItem(DailyQRActivity.DutchMember item){
        items.add(item);
    }

    public DailyQRActivity.DutchMember getItem(int position){
        return items.get(position);
    }

    public ArrayList<DailyQRActivity.DutchMember> getList(){return items;}

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView dutchMemName,dutchMemID,dutchMemMoney;

        ViewHolder(View itemView){
            super(itemView);

            dutchMemID=itemView.findViewById(R.id.dutchMemID);
            dutchMemMoney=itemView.findViewById(R.id.dutchMemMoney);
            dutchMemName=itemView.findViewById(R.id.dutchMemName);

        }

        public void setItem(DailyQRActivity.DutchMember item){
            dutchMemName.setText(item.getMemName());
            dutchMemID.setText(item.getMemID());
            dutchMemMoney.setText(item.getMyMoney()+"");
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_dutch_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //viewholder는 재사용된다! 계속 새로 만들순 없다.

        final DailyQRActivity.DutchMember item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
