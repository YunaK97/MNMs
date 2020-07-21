package com.example.teamtemplate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainMenuActivity extends AppCompatActivity {
    private Member loginMember;
    private Account loginMemberAccount;
    RecyclerView group_membership_list;
    GroupAdapter groupAdapter;
    TextView textName,accName,textBalance;
    final int[] addType={0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount=(Account)intent.getSerializableExtra("loginMemberAccount");

        textName=findViewById(R.id.textName);
        String text="이름 : "+loginMember.getMemName();
        textName.setText(text);
        accName=findViewById(R.id.accName);
        text="계좌번호 : "+loginMemberAccount.getAccountNum();
        accName.setText(text);
        textBalance=findViewById(R.id.textBalance);
        text="잔액 : "+loginMemberAccount.getAccountBalance();
        textBalance.setText(text);

        Button btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendListView();
            }
        });

        Button btn_membership=findViewById(R.id.btn_membership);
        btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showToast("회비 입력");
                membershipView();
            }
        });

        Button btn_short=findViewById(R.id.btn_short);
        btn_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shortListView();
            }
        });

        Button btn_addList=findViewById(R.id.btn_addList);
        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] select=new String[] {"회비모임","꿀잼모임","친구추가"};

                AlertDialog.Builder dialog=new AlertDialog.Builder(MainMenuActivity.this);
                dialog.setTitle("추가!")
                        .setSingleChoiceItems(select, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addType[0]=which;
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(addType[0]==0){
                                    Intent intent = new Intent(getApplicationContext(),NewMembershipActivity.class);
                                    startActivity(intent);
                                }else if(addType[0]==1){
                                    Intent intent = new Intent(getApplicationContext(),NewDailyActivity.class);
                                    startActivity(intent);
                                }else if(addType[0]==2){
                                    //아이디 검색 -> 친구추가
                                }
                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });

        group_membership_list = findViewById(R.id.group_membership_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainMenuActivity.this, LinearLayoutManager.VERTICAL, false);

        group_membership_list.setLayoutManager(layoutManager);

        groupAdapter = new GroupAdapter();
    }

    public void sendListView(){
        showToast("송금 내역 클릭");
    }

    public void membershipView(){

        //그룹 가져와서 출력
        RecyclerView groupMembershiplList=findViewById(R.id.group_membership_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        groupMembershiplList.setLayoutManager(layoutManager);

        groupAdapter=new GroupAdapter();

        //친구 가져와서 출력
        Group group=new Group();
        group.setGroupName("아우디");

        Group group2=new Group();
        group2.setGroupName("계모임");
        groupAdapter.addItem(group);
        groupAdapter.addItem(group2);

        groupMembershiplList.setAdapter(groupAdapter);

        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
            @Override
            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                Group item=groupAdapter.getItem(position);
                showToast("아이템 선택됨 : "+ item.getGroupName());

                Intent intent = new Intent(MainMenuActivity.this,MembershipActivity.class);

                intent.putExtra("loginMember",loginMember);
                intent.putExtra("loginMemberAccount",loginMemberAccount);
                intent.putExtra("selectedGroupName",item.getGroupName());
                startActivity(intent);
            }
        });
    }

    public void shortListView(){

        //그룹 가져와서 출력
        RecyclerView groupMembershiplList=findViewById(R.id.group_membership_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        groupMembershiplList.setLayoutManager(layoutManager);

        groupAdapter=new GroupAdapter();

        //친구 가져와서 출력
        Group group=new Group();
        group.setGroupName("번개만남1");

        Group group2=new Group();
        group2.setGroupName("번개만남2");
        groupAdapter.addItem(group);
        groupAdapter.addItem(group2);

        groupMembershiplList.setAdapter(groupAdapter);

        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
            @Override
            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                Group item=groupAdapter.getItem(position);
                showToast("아이템 선택됨 : "+ item.getGroupName());
                Intent intent = new Intent(MainMenuActivity.this,MembershipActivity.class);

                intent.putExtra("loginMember",loginMember);
                intent.putExtra("loginMemberAccount",loginMemberAccount);
                intent.putExtra("selectedGroupName",item.getGroupName());
                startActivity(intent);
            }
        });
    }
    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
