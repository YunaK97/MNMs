package com.example.teamtemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NewDailyActivity extends AppCompatActivity {
    MemberAdapter memberAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily);

        RecyclerView friend_list=findViewById(R.id.new_daily_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        friend_list.setLayoutManager(layoutManager);

        memberAdapter=new MemberAdapter();

        //친구 가져와서 출력
        for(int i=0;i<10;i++){
            Member member=new Member();
            member.setMemName(i+"님이 검색되었습니다.");
            memberAdapter.addItem(member);
        }

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
