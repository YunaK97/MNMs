package com.example.teamtemplate.newgroupfriend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Group;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.MemberAdapter;
import com.example.teamtemplate.R;
import com.example.teamtemplate.mainscreen.GroupAdapter;
import com.example.teamtemplate.mainscreen.OnGroupItemClickListener;
import com.example.teamtemplate.mainscreen.OnGroupItemLongClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewDailyActivity extends AppCompatActivity {
    private MemberAdapter memberAdapter;
    private ArrayList<Member> arrayList;
    private Member loginMember;
    private RecyclerView friend_list;
    private ArrayList<Member> selectedMember;
    private ArrayList<String> groupName;
    private String TAG_SUCCESS="success";
    private String daily_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily);

        Intent intent=getIntent();
        loginMember=(Member)intent.getSerializableExtra("loginMember");

        //친구 가져와서 출력
        showFriend();

        //기존 그룹 이름 가져오기
        groupNameList();

        //membership 생성 버튼 클릭

        Button btn_new_daily=findViewById(R.id.btn_new_daily);
        btn_new_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMembership();
            }
        });
    }

    protected void NewMembership(){
        selectedMember=new ArrayList<>();
        daily_name=((TextView)findViewById(R.id.daily_name)).getText().toString();
        if(daily_name==null){
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        } else
        {
            boolean overlap=true;
            for(String s:groupName){
                if(s.equals(daily_name)){
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
                String url = "http://jennyk97.dothome.co.kr/NewDaily.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                            if (success) {
                                finish();
                            } else {
                                showToast("daily생성 실패!");
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
                        params.put("dailyName", daily_name);
                        try {
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < selectedMember.size(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("memID", selectedMember.get(i).getMemID());
                                jsonArray.put(jsonObject);
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("memID", loginMember.getMemID());
                            jsonArray.put(jsonObject);
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

        final String url="http://jennyk97.dothome.co.kr/DailygroupInfo.php";

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
        final String url="http://jennyk97.dothome.co.kr/ShowFriend.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        showToast("친구가 없습니다.");
                        return;
                    }

                    friend_list = findViewById(R.id.daily_selected_friend);
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
