package com.example.teamtemplate.firstscreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.mainscreen.MainMenuActivity;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static int SIGNIN=221,BACK=321;
    String TAG_SUCCESS="success";
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
                if(TextUtils.isEmpty(id) || TextUtils.isEmpty(pw)){
                    showToast("빈칸 노노!");
                }else {
                    Member member = new Member();
                    member.setMemID(id);
                    member.setMemPW(pw);

                    loginProcess(member);

                }
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
                    //Gson gson=new Gson();
                    //Member loginMem= (Member) gson.fromJson(response,Member.class);

                    //if(loginMem.getMemID() != null){ //로그인에 성공한 경우
                        //String userID=jsonObject.getString("memID");
                        //String userPass=jsonObject.getString("memPW");
                        //Member loginMem= (Member) jsonObject.get("member");

                    Boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success){
                        String name=jsonObject.getString("memName");
                        String id=jsonObject.getString("memID");
                        String pw=jsonObject.getString("memPW");
                        String email=jsonObject.getString("memEmail");
                        String accNum=jsonObject.getString("accountNum");
                        String accBalance=jsonObject.getString("accountBalance");

                        System.out.println("잔액 "+accBalance);
                        Member loginMem=new Member();
                        loginMem.setMemName(name);
                        loginMem.setMemID(id);
                        loginMem.setMemPW(pw);
                        loginMem.setMemEmail(email);

                        Account memAcc=new Account();
                        memAcc.setAccountNum(accNum);
                        int balance=Integer.parseInt(accBalance);
                        memAcc.setAccountBalance(balance);

                        showToast("로그인 성공하였습니다. "+loginMem.getMemName()+"님");
                        Intent intent=new Intent(MainActivity.this, MainMenuActivity.class);
                        intent.putExtra("loginMember",loginMem);
                        intent.putExtra("loginMemberAccount",memAcc);
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
        RequestWork requestWork =new RequestWork(memID,memPW,responseListener);
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(requestWork);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == SIGNIN){
            boolean result=data.getBooleanExtra("result",false);
            int back=data.getIntExtra("back",0);
            if(back==0){
                if(result) {
                    showToast("회원가입 성공! -> 로그인합시다");
                }else{
                    showToast("쏴리,,회원가입 실패ㅠ");
                }
            }
        }
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private long backKeyPressedTime=0;

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()>backKeyPressedTime+2500){
            backKeyPressedTime=System.currentTimeMillis();
            showToast("뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.");
            return;
        }
        if (System.currentTimeMillis()<=backKeyPressedTime+2500){
            finish();
            showToast("이용해주셔서 감사합니다.");
        }
    }
}
