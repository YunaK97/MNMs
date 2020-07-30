package com.example.teamtemplate.newgroupfriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.MemberAdapter;
import com.example.teamtemplate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewFriendActivity extends AppCompatActivity {
    Member loginMember;
    String TAG_SUCCESS="success";
    String friend_id;
    TextView friend_name_text,friend_id_text;
    ImageButton id_search;
    Button btn_addFriend,request_accept,request_reject;
    LinearLayout linearLayout,request_friend_layout;
    RecyclerView requestedRecyclerView;
    MemberAdapter memberAdapter;
    ArrayList selectedFriend;

    private String REQUESTED="123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        //intent 받아오기
        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");

        //findViewById
        id_search=findViewById(R.id.id_search);
        btn_addFriend=findViewById(R.id.btn_addFriend);
        linearLayout=findViewById(R.id.friend_layout);
        friend_name_text=findViewById(R.id.friend_name);
        friend_id_text=findViewById(R.id.friend_id);
        requestedRecyclerView=findViewById(R.id.request_friend);
        request_accept=findViewById(R.id.request_accept);
        request_reject=findViewById(R.id.request_reject);
        request_friend_layout=findViewById(R.id.request_friend_layout);

        showRequest();
        id_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                friend_id=((TextView)findViewById(R.id.friend_search)).getText().toString();
                if(friend_id.equals(loginMember.getMemID()) || friend_id.length()<4) {
                    showToast("불가능한 id 입니다.");
                }else{
                    request_friend_layout.setVisibility(View.GONE);
                    searchFriend(friend_id);
                }
            }
        });

        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean dup=jsonObject.getBoolean("dup");
                            if(dup){
                                showToast("이미 친구사이입니다.");
                            }else {
                                boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                                if (success) {
                                    showToast("친구추가 완료");
                                    finish();
                                }
                                else {
                                    showToast("친구 추가 실패ㅠ");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RequestNewFriend requestNewFriendAdd = new RequestNewFriend("add", loginMember.getMemID(),friend_id, responseListener);
                RequestQueue queue = Volley.newRequestQueue(NewFriendActivity.this);
                queue.add(requestNewFriendAdd);
            }
        });
    }

    public void showRequest(){
        linearLayout.setVisibility(View.GONE);

        requestedRecyclerView=(RecyclerView)findViewById(R.id.request_friend);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        requestedRecyclerView.setLayoutManager(layoutManager);
        memberAdapter=new MemberAdapter();

        for (int i=0;i<10;i++){
            Member member=new Member();
            member.setMemName(i+"님");
            member.setMemID("id : "+i);
            memberAdapter.addItem(member);
        }

        requestedRecyclerView.setAdapter(memberAdapter);

        request_friend_layout.setVisibility(View.VISIBLE);

        //받은 요청 출력
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONArray jsonArray = new JSONArray(response);
//
//                    for(int i=0;i<jsonArray.length();i++){
//                        JSONObject jsonObject=jsonArray.getJSONObject(i);
//
//                        String friendName=jsonObject.getString("memName");
//                        String friendID=jsonObject.getString("memID");
//
//                        Member member=new Member();
//                        member.setMemName(friendName);
//                        member.setMemID(friendID);
//                        memberAdapter.addItem(member);
//                    }
//
//                    requestedRecyclerView.setAdapter(memberAdapter);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        RequestNewFriend requestNewFriendAdd = new RequestNewFriend(REQUESTED,loginMember.getMemID(),responseListener);
//        RequestQueue queue = Volley.newRequestQueue(NewFriendActivity.this);
//        queue.add(requestNewFriendAdd);

        //체크박스 -> 수락버튼 -> accept
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("친구 수락!");
                request_friend_layout.setVisibility(View.GONE);
               // requestFriend("friend");
            }
        });


        //체크박스 -> 거절버튼 -> reject
        request_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("친구 거절!");
                request_friend_layout.setVisibility(View.GONE);
                //requestFriend("reject");
            }
        });
    }

    public void requestFriend(String TAG_RESULT){
        for(int i=0;i<memberAdapter.getItemCount();i++){
            if(memberAdapter.getItem(i).isChecked()) {
                selectedFriend.add(memberAdapter.getItem(i));
            }
        }
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    Boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success){
                        showToast("친구 신청 처리 완료!");
                        request_friend_layout.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestNewFriend requestNewFriendAdd = new RequestNewFriend(loginMember,selectedFriend, TAG_RESULT,responseListener);
        RequestQueue queue = Volley.newRequestQueue(NewFriendActivity.this);
        queue.add(requestNewFriendAdd);
    }

    public void searchFriend(final String friend_id){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success){
                        String friend_name=jsonObject.getString("memName");

                        friend_name_text.setText(friend_name);
                        friend_id_text.setText(friend_id);

                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        showToast("ID 검색 실패");
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

    public void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

}