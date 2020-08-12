package com.example.teamtemplate.mainscreen;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teamtemplate.Group;
import com.example.teamtemplate.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private ArrayList<Group> items=new ArrayList<Group>();

    OnGroupItemClickListener listener;

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

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView group_name;

        public ViewHolder(View itemView,final OnGroupItemClickListener listener){
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
        }

        public void setItem(Group item){
            group_name.setText(item.getGroupName());
        }
    }

    public void setOnItemClickListener(OnGroupItemClickListener listener){
        this.listener=listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.layout_group,parent,false);

        return new ViewHolder(itemView,listener);
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
