package com.example.teamtemplate.newgroupfriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.MemberAdapter;
import com.example.teamtemplate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewMembershipActivity extends AppCompatActivity {
    private MemberAdapter memberAdapter;
    private ArrayList<Member> selectedMember;
    private Member loginMember;
    private String TAG_SUCCESS="success";
    private ArrayList<String> groupName;
    private RecyclerView friend_list;
    private String membership_name, membership_money,membership_notsubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership);

        Intent intent=getIntent();
        loginMember=(Member)intent.getSerializableExtra("loginMember");

        //친구 가져와서 출력
        showFriend();

        groupNameList();

        //membership 생성 버튼 클릭
        Button btn_new_membership=findViewById(R.id.btn_new_membership);
        btn_new_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMembership();
            }
        });
    }

    protected void NewMembership(){
        selectedMember=new ArrayList<>();
        membership_name=((TextView)findViewById(R.id.membership_name)).getText().toString();
        membership_money=((TextView)findViewById(R.id.membership_money)).getText().toString();
        membership_notsubmit=((TextView)findViewById(R.id.membership_notsubmit)).getText().toString();
        if(membership_money==null || membership_name==null || membership_notsubmit==null){
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        }else
        {
            boolean overlap=true;
            for(String s:groupName){
                if(s.equals(membership_name)){
                    overlap=false;
                }
            }
            if(!overlap){
                showToast("이미 존재하는 그룹이름입니다.");
            }else if(overlap) {
                for (int i = 0; i < memberAdapter.getItemCount(); i++) {
                    if (memberAdapter.getItem(i).isChecked()) {
                        Member member = new Member();
                        member.setMemID((memberAdapter.getItem(i)).getMemID());
                        selectedMember.add(member);
                    }
                }
                String url = "http://jennyk97.dothome.co.kr/NewMembership.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                            if (success) {
                                showToast("생성 성공!");
                                finish();
                            } else {
                                showToast("membership 실패!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("memID", loginMember.getMemID());
                        params.put("memName", loginMember.getMemName());
                        params.put("membershipName", membership_name);
                        params.put("membershipMoney",membership_money);
                        params.put("membershipNotSubmit", membership_notsubmit);
                        try {
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < selectedMember.size(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("memID", selectedMember.get(i).getMemID());
                                jsonArray.put(jsonObject);
                            }
                            params.put("friend", jsonArray.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return params;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(stringRequest);
            }
        }
    }


    private void groupNameList(){
        groupName=new ArrayList<>();

        final String url="http://jennyk97.dothome.co.kr/MembergroupInfo.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                            groupName.add(groupname);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("오류 : "+e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("memID",loginMember.getMemID());
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    protected void showFriend(){
        final String url="http://jennyk97.dothome.co.kr//ShowFriend.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        showToast("친구가 없습니다.");
                        return;
                    }

                    friend_list = findViewById(R.id.membership_select_friend);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("memID",loginMember.getMemID());
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
