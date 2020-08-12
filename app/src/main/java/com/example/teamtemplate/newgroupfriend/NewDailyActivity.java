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

public class NewDailyActivity extends AppCompatActivity {
    private MemberAdapter memberAdapter;
    private ArrayList<Member> arrayList;
    private Member loginMember;
    private ArrayList<Member> selectedMember;
    private String TAG_SUCCESS="success";
    private String daily_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily);

        Intent intent=getIntent();
        loginMember=(Member)intent.getSerializableExtra("loginMember");

        /*
            친구 가져와서 출력
            그룹이름,회비,친구목록 php로 전송
            생성 결과 받기
         */
        daily_name=((TextView)findViewById(R.id.daily_name)).getText().toString();

        //친구 가져와서 출력
        showFriend();

        //membership 생성 버튼 클릭

        Button btn_new_daily=findViewById(R.id.btn_new_daily);
        btn_new_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("daily 멤버십 생성!");
                //NewMembership();
            }
        });
    }

    protected void NewMembership(){
        if(daily_name==null){
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
                            showToast("daily생성 실패!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestNewGroup requestNewGroup =new RequestNewGroup(loginMember,selectedMember,daily_name,responseListener);
            RequestQueue queue= Volley.newRequestQueue(NewDailyActivity.this);
            queue.add(requestNewGroup);

        }
    }
    protected void showFriend(){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        showToast("친구가 없습니다.");
                        return;
                    }

                    RecyclerView friend_list = findViewById(R.id.daily_selected_friend);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(NewDailyActivity.this, LinearLayoutManager.VERTICAL, false);

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
        RequestQueue queue= Volley.newRequestQueue(NewDailyActivity.this);
        queue.add(requestShowFriend);
    }

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
