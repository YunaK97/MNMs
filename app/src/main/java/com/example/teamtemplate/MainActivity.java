package com.example.teamtemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public final static int SIGNIN=221;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button login,signin;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login= findViewById(R.id.login);
        signin= findViewById(R.id.signin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=((TextView)findViewById(R.id.id)).getText().toString();
                String pw=((TextView)findViewById(R.id.pw)).getText().toString();

                Member member=new Member();
                member.setMemID(id);
                member.setMemPW(pw);

                //임시!
                loginProcess(member);

                //로그인 정보 가져오기
                //없으면 false
                //있으면 true, 멤버정보 가져옴
                //  메인화면으로 전환

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignInActivity.class);
                startActivityForResult(intent,SIGNIN);
            }
        });
    }

    protected void loginProcess(Member member){

        final String memID=member.getMemID();
        final String memPW=member.getMemPW();

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Gson gson=new Gson();
                    Member loginMem= (Member) gson.fromJson(response,Member.class);

                    if(loginMem.getMemID() != null){ //로그인에 성공한 경우
                        //String userID=jsonObject.getString("memID");
                        //String userPass=jsonObject.getString("memPW");
                        //Member loginMem= (Member) jsonObject.get("member");


                        showToast("로그인 성공하였습니다. "+loginMem.getMemName()+"님");
                        Intent intent=new Intent(MainActivity.this,MainMenuActivity.class);
                        intent.putExtra("loginMember",loginMem);
                        //멤버 나머지 속성 받기

                        startActivity(intent);
                    }
                    else{//로그인에 실패한 경우
                        showToast("로그인에 실패했습니다.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestLogin requestLogin =new RequestLogin(memID,memPW,responseListener);
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(requestLogin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == SIGNIN){
            boolean result=data.getBooleanExtra("result",false);
            Member newMember=(Member) data.getSerializableExtra("newMember");

            if(result) {
                showToast("회원가입 성공! -> 로그인합시다");
            }else{
                showToast("쏴리,,회원가입 실패ㅠ");
            }
        }
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}
