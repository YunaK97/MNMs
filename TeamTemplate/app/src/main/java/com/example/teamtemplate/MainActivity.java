package com.hongik.mnms;

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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public final static int SIGNIN=221;
    String tmpId="nh822",tmpPw="qwer01";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button login,signin;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login=(Button)findViewById(R.id.login);
        signin=(Button)findViewById(R.id.signin);

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

        String memID=member.getMemID();
        String memPW=member.getMemPW();

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean("success");
                    if(success){ //로그인에 성공한 경우
                        String userID=jsonObject.getString("userID");
                        String userPass=jsonObject.getString("userPassword");

                        Toast.makeText(getApplicationContext(),"로그인 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,MainMenuActivity.class);
                        intent.putExtra("userID",userID);
                        intent.putExtra("userPass",userPass);
                        //멤버 나머지 속성 받기

                        startActivity(intent);
                    }
                    else{//로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(),"로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestLogin requestLogin =new RequestLogin(memID,memPW,responseListener);
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(requestLogin);
//
//        if(member!=null) {
//            if (member.getMemID().equals(tmpId) && member.getMemPW().equals(tmpPw)) {
//                Toast.makeText(getApplicationContext(), member.getMemID() + " 님 환영합니다!", Toast.LENGTH_LONG).show();
//
//                member.setMemSsn("970822-10041004");
//                member.setMemEmail("nh822@naver.com");
//                member.setMemAccountNum("250502-04-371024");
//                member.setMemBalance(987000);
//                member.setMemName("김나현");
//                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
//                intent.putExtra("loginMember", member);
//                startActivity(intent);
//            }else{
//                Toast.makeText(getApplicationContext(),"ID or PW 확인!",Toast.LENGTH_LONG).show();
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == SIGNIN){
            boolean result=data.getBooleanExtra("result",false);
            Member newMember=(Member) data.getSerializableExtra("newMember");

            if(result){
                Toast.makeText(getApplicationContext(),"로그인하시면 되옴"+newMember.getMemID(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
