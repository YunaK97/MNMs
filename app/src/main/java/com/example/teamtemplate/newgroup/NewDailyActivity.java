package com.example.teamtemplate.newgroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamtemplate.Member;
import com.example.teamtemplate.MemberAdapter;
import com.example.teamtemplate.R;

import java.util.ArrayList;

public class NewDailyActivity extends AppCompatActivity {
    private MemberAdapter memberAdapter;
    private ArrayList<Member> arrayList;
    private Member loginMember;

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

        TextView membership_name=findViewById(R.id.membership_name);
        TextView membership_money=findViewById(R.id.membership_money);

        //친구 가져와서 출력
        showFriendList();

        //membership 생성 버튼 클릭

        Button btn_new_daily=findViewById(R.id.btn_new_daily);
        btn_new_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("daily 멤버십 생성!");
                //new_membership 생성 성공 여부
//                Response.Listener<String> responseListener=new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject=new JSONObject(response);
//                            boolean success=jsonObject.getBoolean(TAG_SUCCESS);
//                            if(success){
//                                showToast("사용가능한 아이디입니다.");
//                                signInMember.setMemID(memID);
//                                idValid=true;
//                            }
//                            else{
//                                showToast("사용 불가능한 아이디입니다.");
//                                idValid=false;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//
//                RequestNewMembership requestNewMembership=new RequestNewMembership();
//                RequestIdOverlap requestIdOverlap =new RequestIdOverlap(memID,responseListener);
//
//                RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
//                queue.add(requestIdOverlap);
            }
        });
    }

    public void showFriendList(){
        //로그인 멤버의 아이디 전송
//        Response.Listener<String> responseListener=new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    boolean success=jsonObject.getBoolean(TAG_SUCCESS);
//                    if(success){
//
//                    }
//                    else{
//                        showToast("로딩실패ㅠ");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        RequestShowFriend requestShowFriend=new RequestShowFriend(loginMember.getMemID(),responseListener);
//        RequestQueue queue= Volley.newRequestQueue(NewMembershipActivity.this);
//        queue.add(requestShowFriend);

        //받은 데이터 출력
        RecyclerView friend_list=findViewById(R.id.new_daily_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        friend_list.setLayoutManager(layoutManager);

        memberAdapter=new MemberAdapter();

        for(int i=0;i<10;i++){
            Member member=new Member();
            member.setMemName(i+"님");
            memberAdapter.addItem(member);
        }
        friend_list.setAdapter(memberAdapter);

        for (int i=0;i<memberAdapter.getItemCount();i++){
            if(memberAdapter.getItem(i).isChecked()){
                arrayList.add(memberAdapter.getItem(i));
            }
        }
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
