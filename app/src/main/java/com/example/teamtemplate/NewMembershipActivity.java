package com.example.teamtemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NewMembershipActivity extends AppCompatActivity {
    MemberAdapter memberAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership);

        RecyclerView friend_list=findViewById(R.id.friend_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        friend_list.setLayoutManager(layoutManager);

        memberAdapter=new MemberAdapter();

        //친구 가져와서 출력
        Member member=new Member();
        member.setMemName("김씨");
        member.setMemID("kim");
        Member member2=new Member();
        member2.setMemName("이씨");
        member2.setMemID("lee");
        Member member3=new Member();
        member3.setMemName("박씨");
        member3.setMemID("park");
        memberAdapter.addItem(member);
        memberAdapter.addItem(member2);
        memberAdapter.addItem(member3);

        friend_list.setAdapter(memberAdapter);

        memberAdapter.setOnItemClickListener(new OnMemberItemClickListener() {
            @Override
            public void onItemClick(MemberAdapter.ViewHolder holder, View view, int position) {
                Member item=memberAdapter.getItem(position);
                showToast("아이템 선택됨 : "+ item.getMemName());
            }
        });
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
