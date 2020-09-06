package kr.hongik.mnms.mainscreen.ui.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kr.hongik.mnms.Member;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.R;


public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private ArrayList<Member> items= new ArrayList<>();

    private OnFriendItemClickListener listener;
    private OnFriendItemLongClickListener longlistener;

    public void addItem(Member item){
        items.add(item);
    }

    public void setItems(ArrayList<Member> items){
        this.items=items;
    }

    public Member getItem(int position) {
        return items.get(position);
    }

    public ArrayList<Member> getList(){return items;};

    public void setItem(int position,Member item){
        items.set(position,item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView friendlist_name,friendlist_id;

        ViewHolder(View itemView, final OnFriendItemClickListener listener, final OnFriendItemLongClickListener longlistener){
            super(itemView);

            friendlist_name=itemView.findViewById(R.id.friendlist_name);
            friendlist_id=itemView.findViewById(R.id.friendlistlist_id);

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
                public boolean onLongClick(View v)
                {
                    int position = getAdapterPosition();
                    if(longlistener!=null){
                        longlistener.onItemLongClick(ViewHolder.this, v, position);
                    }
                    return true;
                }
            });
        }

        public void setItem(Member item){
            friendlist_name.setText(item.getMemName());
            friendlist_id.setText(item.getMemID());
        }
    }

    public void setOnItemClickListener(OnFriendItemClickListener listener){
        this.listener=listener;
    }

    public void setOnItemLongClickListener(OnFriendItemLongClickListener listener) {
        this.longlistener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.layout_friend_list,parent,false);

        return new ViewHolder(itemView,listener,longlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //viewholder는 재사용된다! 계속 새로 만들순 없다.

        final Member item=items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
