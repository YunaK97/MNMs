package kr.hongik.mnms.mainscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kr.hongik.mnms.Group;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private ArrayList<Group> items= new ArrayList<>();

    OnGroupItemClickListener listener;
    OnGroupItemLongClickListener longListener;

    public void addItem(Group item){
        items.add(item);
    }

    public void setItems(ArrayList<Group> items){
        this.items=items;
    }

    public Group getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position,Group item){
        items.set(position,item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView group_name;

        ViewHolder(View itemView, final OnGroupItemClickListener listener, final OnGroupItemLongClickListener longListener){
            super(itemView);

            group_name=itemView.findViewById(R.id.group_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(longListener!=null){
                        longListener.onItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }

        public void setItem(Group item){
            group_name.setText(item.getGroupName());
        }
    }

    public void setOnItemClickListener(OnGroupItemClickListener listener){
        this.listener=listener;
    }

    public void setOnItemLongClickListener(OnGroupItemLongClickListener listener){
        this.longListener=listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.layout_group,parent,false);

        return new ViewHolder(itemView,listener,longListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //viewholder는 재사용된다! 계속 새로 만들순 없다.

        Group item=items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
