package com.example.teamtemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendListActivity extends AppCompatActivity {
    private FriendListAdapter friendListAdapter;
    private Button btn_add,btn_request;
    private Member loginMember;
    private String TAG_SUCCESS="success";

    //친구 추가알림 확인!


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");

        //tmpShowFriend();
        showFriend();

        btn_request=findViewById(R.id.btn_request);
        btn_add=findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),NewFriendActivity.class);
                intent.putExtra("loginMember",loginMember);
                startActivity(intent);
            }
        });

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 신청 목록 출력
                //다이얼로그로 하나씩 출력하자!
                //다이얼로그에 한명씩 출력 -> 수락,거절 선택 -> 모든 신청 활동 완료 -> 친구 목록 업데이트

            }
        });
    }

    public void showFriend(){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //JSONObject jsonObject=new JSONObject(response);
                    //JSONArray jsonArray=jsonObject.getJSONArray("response");
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        showToast("친구가 없습니다.");
                        return;
                    }

                    RecyclerView friend_list = findViewById(R.id.friend_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(FriendListActivity.this, LinearLayoutManager.VERTICAL, false);

                    friend_list.setLayoutManager(layoutManager);

                    friendListAdapter = new FriendListAdapter();

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String friendId = item.getString("memID");
                        String friendName = item.getString("memName");

                        Member member=new Member();
                        member.setMemName(friendName);
                        member.setMemID(friendId);
                        friendListAdapter.addItem(member);
                    }

                    friend_list.setAdapter(friendListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestShowFriend requestShowFriend=new RequestShowFriend(loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(FriendListActivity.this);
        queue.add(requestShowFriend);
    }

    public void tmpShowFriend(){
        RecyclerView friend_list = findViewById(R.id.friend_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(FriendListActivity.this, LinearLayoutManager.VERTICAL, false);

        friend_list.setLayoutManager(layoutManager);

        friendListAdapter = new FriendListAdapter();

        for (int i=0;i<10;i++) {
            String friendName = i+"님";
            String friendId = i+"_id";

            Member member=new Member();
            member.setMemName(friendName);
            member.setMemID(friendId);
            friendListAdapter.addItem(member);
        }

        friend_list.setAdapter(friendListAdapter);
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

}
