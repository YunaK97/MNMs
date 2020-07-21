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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewMembershipActivity extends AppCompatActivity {
    MemberAdapter memberAdapter;
    ArrayList<Member> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership);

        RecyclerView friend_list=findViewById(R.id.new_membership_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        //GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        friend_list.setLayoutManager(layoutManager);

        memberAdapter=new MemberAdapter();

        //친구 가져와서 출력
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

        Button new_membership=findViewById(R.id.btn_new_membership);
        new_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
