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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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

    String TAG_MEMBERSHIP="membership",TAG_DAILY="daily";

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
                GroupView(TAG_MEMBERSHIP);
            }
        });

        Button btn_short=findViewById(R.id.btn_short);
        btn_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupView(TAG_DAILY);
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
                                    Intent intent=new Intent(getApplicationContext(),NewFriendActivity.class);
                                    startActivity(intent);
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
    }

    public void sendListView(){
        showToast("송금 내역 클릭");
    }

    public void GroupView(final String tag){
        //그룹 가져와서 출력
        RecyclerView groupMembershiplList=findViewById(R.id.group_membership_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        groupMembershiplList.setLayoutManager(layoutManager);

        groupAdapter=new GroupAdapter();

        for(int i=0;i<10;i++){
            Group group = new Group();
            if(tag==TAG_MEMBERSHIP){
                group.setGroupName("membership : "+i);
            }else if(tag==TAG_DAILY){
                group.setGroupName("daily : "+i);
            }
            groupAdapter.addItem(group);
        }
        groupMembershiplList.setAdapter(groupAdapter);

        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
            @Override
            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                Group item=groupAdapter.getItem(position);
                showToast("아이템 선택됨 : "+ item.getGroupName());

                if(TAG_MEMBERSHIP==tag){
                    Intent intent = new Intent(MainMenuActivity.this,MembershipActivity.class);

                    intent.putExtra("loginMember",loginMember);
                    intent.putExtra("loginMemberAccount",loginMemberAccount);
                    intent.putExtra("selectedGroupName",item.getGroupName());

                    startActivity(intent);
                }else if(TAG_DAILY==tag){
                    showToast("daily 미구현");
                }

            }
        });

    }
    public void membershipView(){

//        Response.Listener<String> responseListener=new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    JSONArray jsonArray=jsonObject.getJSONArray("GroupArray");
//
//                    for (int i=0;i<jsonArray.length();i++){
//                        JSONObject item=jsonArray.getJSONObject(i);
//                        String groupname=item.getString("GroupName");
//
//                        Group group = new Group();
//                        group.setGroupName(groupname);
//                        groupAdapter.addItem(group);
//                    }
//
//                    groupMembershiplList.setAdapter(groupAdapter);
//
//                    groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
//                        @Override
//                        public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
//                            Group item=groupAdapter.getItem(position);
//                            showToast("아이템 선택됨 : "+ item.getGroupName());
//
//                            Intent intent = new Intent(MainMenuActivity.this,MembershipActivity.class);
//
//                            intent.putExtra("loginMember",loginMember);
//                            intent.putExtra("loginMemberAccount",loginMemberAccount);
//                            intent.putExtra("selectedGroupName",item.getGroupName());
//                            startActivity(intent);
//                        }
//                    });
//
//                    Boolean success=jsonObject.getBoolean("success");
//                    if(success){
//
//                    }
//                    else{//그룹 가져오기 실패
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        RequestGroup requestGroup=new RequestGroup(loginMember.getMemID(),responseListener);
//        RequestQueue queue=Volley.newRequestQueue(MainMenuActivity.this);
//        queue.add(requestGroup);


    }
    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
