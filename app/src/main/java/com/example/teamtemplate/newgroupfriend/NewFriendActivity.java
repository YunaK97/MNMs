package com.example.teamtemplate.newgroupfriend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class NewFriendActivity extends AppCompatActivity {
    Member loginMember;
    String TAG_SUCCESS="success";
    String friend_id;
    TextView friend_name_text,friend_id_text;
    CheckBox friend_check;
    ImageButton id_search;
    Button btn_addFriend;
    LinearLayout linearLayout;


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
        friend_check=findViewById(R.id.friend_check);
        linearLayout=findViewById(R.id.friend_layout);
        friend_name_text=findViewById(R.id.friend_name);
        friend_id_text=findViewById(R.id.friend_id);

        id_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                friend_id=((TextView)findViewById(R.id.friend_search)).getText().toString();
                if(friend_id!=loginMember.getMemID()) {
                    searchFriend(friend_id);
                }else{
                    showToast("불가능한 id 입니다.");
                }
            }
        });

        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(friend_check.isChecked()) {
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
            }
        });
    }

    public void searchFriend(final String friend_id){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success){
                        showToast("성공");
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