package com.example.teamtemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewFriendActivity extends AppCompatActivity {
    MemberAdapter memberAdapter;
    Member selectedMember;
    String TAG_SUCCESS="success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        ImageButton id_search=findViewById(R.id.id_search);
        id_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                String friend_id= findViewById(R.id.friend_id).toString();
                //searchFriend(friend_id);

                //php 구현 전 임시 검색
                tmpSearchFriend();
            }
        });

        Button btn_addFriend=findViewById(R.id.btn_addFriend);
        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<memberAdapter.getItemCount();i++){
                    Member item=memberAdapter.getItem(i);
                    if(item.isChecked()){
                        selectedMember=item;
                    }
                }

                //선택된 친구 추가
                showToast(selectedMember.getMemName()+"선택!!!");
//                Response.Listener<String> responseListener=new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject=new JSONObject(response);
//                            boolean success=jsonObject.getBoolean(TAG_SUCCESS);
//                            if(success){
//                                showToast("친구추가 완료");
//                                finish();
//                            }
//                            else{
//                                showToast("친구 추가 실패ㅠ");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                RequestNewFriend requestNewFriendAdd=new RequestNewFriend("add",selectedMember.getMemID(),responseListener);
//                RequestQueue queue=Volley.newRequestQueue(NewFriendActivity.this);
//                queue.add(requestNewFriendAdd);
            }
        });
    }

    public void searchFriend(String friend_id){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success){
                        printFriendList(jsonObject);
                    }
                    else{
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestNewFriend requestNewFriend=new RequestNewFriend(friend_id,responseListener);
        RequestQueue queue= Volley.newRequestQueue(NewFriendActivity.this);
        queue.add(requestNewFriend);
    }

    public void printFriendList(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray=jsonObject.getJSONArray("FriendArray");

        RecyclerView friend_list = findViewById(R.id.new_friend_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NewFriendActivity.this, LinearLayoutManager.VERTICAL, false);

        friend_list.setLayoutManager(layoutManager);

        memberAdapter = new MemberAdapter();

        for (int i=0;i<jsonArray.length();i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String friendName = item.getString("FriendName");

            Member member=new Member();
            member.setMemName(friendName);
            memberAdapter.addItem(member);
        }

        friend_list.setAdapter(memberAdapter);
    }

    public void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public void tmpSearchFriend(){
        RecyclerView friend_list = findViewById(R.id.new_friend_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NewFriendActivity.this, LinearLayoutManager.VERTICAL, false);

        friend_list.setLayoutManager(layoutManager);

        memberAdapter = new MemberAdapter();

        for (int i=0;i<10;i++) {
            Member member=new Member();
            member.setMemName(i+"님");
            member.setMemID(i+" id");
            memberAdapter.addItem(member);
        }
        friend_list.setAdapter(memberAdapter);
    }

}