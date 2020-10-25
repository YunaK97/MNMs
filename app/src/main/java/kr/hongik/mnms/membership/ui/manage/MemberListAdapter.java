package kr.hongik.mnms.membership.ui.manage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;


public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
    private ArrayList<MembershipMemFragment.MembershipMember> items = new ArrayList<>();

    private OnMemberListClickListener listener;
    private OnMemberListLongClickListener longlistener;

    public void addItem(MembershipMemFragment.MembershipMember item) {
        items.add(item);
    }

    public void setItems(ArrayList<MembershipMemFragment.MembershipMember> items) {
        this.items = items;
    }

    public MembershipMemFragment.MembershipMember getItem(int position) {
        return items.get(position);
    }

    public ArrayList<MembershipMemFragment.MembershipMember> getList() {
        return items;
    }


    public void setItem(int position, MembershipMemFragment.MembershipMember item) {
        items.set(position, item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView membership_name, membership_id,membership_cnt;

        ViewHolder(View itemView,final OnMemberListClickListener listener,final OnMemberListLongClickListener longlistener) {
            super(itemView);

            membership_name = itemView.findViewById(R.id.membership_name);
            membership_id = itemView.findViewById(R.id.membership_id);
            membership_cnt=itemView.findViewById(R.id.membership_cnt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (longlistener != null) {
                        longlistener.onItemLongClick(ViewHolder.this, v, position);
                    }
                    return true;
                }
            });
        }

        public void setItem(MembershipMemFragment.MembershipMember item) {
            membership_name.setText(item.getMemName());
            membership_id.setText(item.getMemID());
            membership_cnt.setText(item.getNotSubmit()+"");
        }
    }

    public void setOnClickListener(OnMemberListClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(OnMemberListLongClickListener listener) {
        this.longlistener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_member_list, parent, false);

        return new ViewHolder(itemView, listener, longlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //viewholder는 재사용된다! 계속 새로 만들순 없다.

        final MembershipMemFragment.MembershipMember item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
