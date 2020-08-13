package com.example.teamtemplate.firstscreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.mainscreen.MainMenuActivity;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final static int SIGNIN=221,BACK=321;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String TAG_SUCCESS="success";
    private  CheckBox autoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button login,signin;

        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();

        setContentView(R.layout.activity_main);
        login= findViewById(R.id.login);
        signin= findViewById(R.id.signin);
        autoLogin=findViewById(R.id.autoLogin);

        String tmpId=preferences.getString("loginId","");
        String tmpPw=preferences.getString("loginPw","");
        if(!tmpId.equals("") && !tmpPw.equals("")) {
            Member member=new Member();
            member.setMemID(tmpId);
            member.setMemPW(tmpPw);
            loginProcess(member);
        }

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

    protected void loginProcess(final Member member){
        final String url="http://jennyk97.dothome.co.kr/Login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

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

                        if(autoLogin.isChecked()){
                            editor.putString("loginId",loginMem.getMemID());
                            editor.putString("loginPw",loginMem.getMemID());
                            editor.commit();
                        }
                        else{
                            editor.clear();
                            editor.commit();
                        }
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("memPW", member.getMemPW());
                params.put("memID",member.getMemID());
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }


    protected void showToast(String data){
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
