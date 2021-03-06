package kr.hongik.mnms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private ArrayList<Member> items = new ArrayList<>();

    private OnMemberItemClickListener listener;

    public void addItem(Member item) {
        items.add(item);
    }

    public void setItems(ArrayList<Member> items) {
        this.items = items;
    }

    public Member getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Member item) {
        items.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName, tvFriendId;
        CheckBox cbFriendCheck;

        ViewHolder(View itemView, final OnMemberItemClickListener listener) {
            super(itemView);

            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            tvFriendId = itemView.findViewById(R.id.tvFriendId);
            cbFriendCheck = itemView.findViewById(R.id.cbFriendCheck);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(Member item) {
            tvFriendName.setText(item.getMemName());
            tvFriendId.setText(item.getMemID());
            cbFriendCheck.setChecked(false);
        }
    }

    public void setOnItemClickListener(OnMemberItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewholder 생성시점에 자동으로 실행 됨

        //inflater 참조방법
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_select_friend, parent, false);

        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //viewholder는 재사용된다! 계속 새로 만들순 없다.

        final Member item = items.get(position);
        holder.setItem(item);

        holder.cbFriendCheck.setChecked(item.isChecked());
        holder.cbFriendCheck.setOnCheckedChangeListener(null);
        holder.cbFriendCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
