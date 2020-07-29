package com.example.teamtemplate.newgroupfriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.MemberAdapter;
import com.example.teamtemplate.R;
import com.example.teamtemplate.RequestShowFriend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewMembershipActivity extends AppCompatActivity {
    private MemberAdapter memberAdapter;
    private ArrayList<Member> selectedMember;
    private Member loginMember;
    private String TAG_SUCCESS="success";
    private String membership_name,membership_money;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership);

        Intent intent=getIntent();
        loginMember=(Member)intent.getSerializableExtra("loginMember");

        /*
            친구 가져와서 출력
            그룹이름,회비,친구목록 php로 전송
            생성 결과 받기
         */

        membership_name=((TextView)findViewById(R.id.membership_name)).getText().toString();
        membership_money=((TextView)findViewById(R.id.membership_money)).getText().toString();

        //친구 가져와서 출력
        showFriend();

        //membership 생성 버튼 클릭
        Button btn_new_membership=findViewById(R.id.btn_new_membership);
        btn_new_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("멤버십 생성!");
                finish();
                //NewMembership();
            }
        });
    }

    public void NewMembership(){
        if(membership_money==null || membership_name==null){
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        }else {
            for (int i = 0; i < memberAdapter.getItemCount(); i++) {
                if (memberAdapter.getItem(i).isChecked()) {
                    Member member = new Member();
                    member.setMemID((memberAdapter.getItem(i)).getMemID());
                    member.setMemName((memberAdapter.getItem(i)).getMemName());
                    selectedMember.add(member);
                }
            }
            //new_membership 생성 성공 여부
            Response.Listener<String> responseListener=new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                        if(success){
                            showToast("생성 성공!");
                            finish();
                        }
                        else{
                            showToast("membership생성 실패!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestNewMembership requestNewMembership =new RequestNewMembership(loginMember,selectedMember,membership_money,membership_name,responseListener);
            RequestQueue queue= Volley.newRequestQueue(NewMembershipActivity.this);
            queue.add(requestNewMembership);

        }
    }

    public void showFriend(){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        showToast("친구가 없습니다.");
                        return;
                    }

                    RecyclerView friend_list = findViewById(R.id.membership_select_friend);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(NewMembershipActivity.this, LinearLayoutManager.VERTICAL, false);

                    friend_list.setLayoutManager(layoutManager);

                    memberAdapter = new MemberAdapter();

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String friendId = item.getString("memID");
                        String friendName = item.getString("memName");

                        Member member=new Member();
                        member.setMemName(friendName);
                        member.setMemID(friendId);
                        memberAdapter.addItem(member);
                    }

                    friend_list.setAdapter(memberAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestShowFriend requestShowFriend=new RequestShowFriend(loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(NewMembershipActivity.this);
        queue.add(requestShowFriend);
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
