package com.example.teamtemplate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.daily.DailyActivity;
import com.example.teamtemplate.membership.MembershipActivity;
import com.example.teamtemplate.newgroup.NewDailyActivity;
import com.example.teamtemplate.newgroup.NewMembershipActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class MainMenuActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Member loginMember;
    private Account loginMemberAccount;
    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private TextView textName,accName,textBalance;
    final private int[] addType={0};

    private String TAG_MEMBERSHIP="membership",TAG_DAILY="daily",TAG_SUCCESS="success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_mainmenu);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_USE_LOGO);

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

        Button btn_transaction = findViewById(R.id.btn_transaction);
        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactionListView();
            }
        });

        Button btn_membership=findViewById(R.id.btn_membership);
        btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpGroupView(TAG_MEMBERSHIP);
                //GroupView(TAG_MEMBERSHIP);
            }
        });

        Button btn_daily=findViewById(R.id.btn_daily);
        btn_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpGroupView(TAG_DAILY);
                //GroupView(TAG_DAILY);
            }
        });

        Button btn_addList=findViewById(R.id.btn_friendList);
        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //친구 목록 출력
                Intent intent=new Intent(getApplicationContext(),FriendListActivity.class);
                intent.putExtra("loginMember",loginMember);
                startActivity(intent);
            }
        });
    }

    public void transactionListView(){
        showToast("송금 내역 클릭");
    }

    public void tmpGroupView(final String tag){
        groupMembershiplList=findViewById(R.id.group_membership_list);
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
                    Intent intent = new Intent(MainMenuActivity.this, MembershipActivity.class);

                    intent.putExtra("loginMember",loginMember);
                    intent.putExtra("loginMemberAccount",loginMemberAccount);
                    intent.putExtra("gname",item.getGroupName());
                    intent.putExtra("gid",item.getGid());

                    startActivity(intent);
                }else if(TAG_DAILY==tag){
                    Intent intent = new Intent(MainMenuActivity.this, DailyActivity.class);

                    intent.putExtra("loginMember",loginMember);
                    intent.putExtra("loginMemberAccount",loginMemberAccount);
                    intent.putExtra("gname",item.getGroupName());
                    intent.putExtra("gid",item.getGid());

                    startActivity(intent);
                }

            }
        });

    }
    public void GroupView(final String tag){
        //TAG 별로 그룹 정보 가져오기 실행
        //그룹 가져와서 출력
        groupMembershiplList=findViewById(R.id.group_membership_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        groupMembershiplList.setLayoutManager(layoutManager);

        groupAdapter=new GroupAdapter();

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()==0){
                        showToast("그룹이 없습니다.");

                    }else{
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject item=jsonArray.getJSONObject(i);
                            String groupname=item.getString("groupName");
                            String gid=item.getString("groupID");

                            showToast(groupname);

                            Group group = new Group();
                            group.setGroupName(groupname);
                            group.setGid(gid);
                            groupAdapter.addItem(group);
                        }

                        groupMembershiplList.setAdapter(groupAdapter);

                        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
                            @Override
                            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                                Group item=groupAdapter.getItem(position);
                                showToast("아이템 선택됨 : "+ item.getGroupName());

                                Intent intent = new Intent(MainMenuActivity.this,MembershipActivity.class);

                                intent.putExtra("loginMember",loginMember);
                                intent.putExtra("loginMemberAccount",loginMemberAccount);
                                intent.putExtra("gname",item.getGroupName());
                                intent.putExtra("gid",item.getGid());
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("오류 : "+e.toString());
                }
            }
        };
        RequestGroup requestGroup=new RequestGroup(loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(MainMenuActivity.this);
        queue.add(requestGroup);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        Intent intent;

        switch (curId){
            case R.id.add_membership:
                intent = new Intent(getApplicationContext(), NewMembershipActivity.class);
                intent.putExtra("loginMember",loginMember);
                startActivity(intent);

                break;
            case R.id.add_daily:
                intent = new Intent(getApplicationContext(), NewDailyActivity.class);
                intent.putExtra("loginMember",loginMember);
                startActivity(intent);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public void plusAction(){
        final String[] select=new String[] {"회비모임","꿀잼모임"};

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
                            Intent intent = new Intent(getApplicationContext(), NewMembershipActivity.class);
                            intent.putExtra("loginMember",loginMember);
                            startActivity(intent);
                        }else if(addType[0]==1){
                            Intent intent = new Intent(getApplicationContext(), NewDailyActivity.class);
                            intent.putExtra("loginMember",loginMember);
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
}
