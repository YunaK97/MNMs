package com.example.teamtemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
//아이디 중복확인
//민증확인
//회원가입 완료
    boolean idValid=false,ssnValid=false,emailValid=false;
    Member signInMember = new Member();
    Account memberAccount = new Account();
    Spinner email_type,bank_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email_type=(Spinner) findViewById(R.id.email_type);
        final ArrayAdapter emailTypeAdapter=ArrayAdapter.createFromResource(this,R.array.email_type,R.layout.support_simple_spinner_dropdown_item);
        emailTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        email_type.setAdapter(emailTypeAdapter);
        email_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String emailID=((TextView)findViewById(R.id.textEmail)).getText().toString();
                String email=emailID+"@"+email_type.getSelectedItem().toString();
                signInMember.setMemEmail(email);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bank_type=(Spinner) findViewById(R.id.bank_type);
        final ArrayAdapter bankTypeAdapter=ArrayAdapter.createFromResource(this,R.array.bank_type,R.layout.support_simple_spinner_dropdown_item);
        bankTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bank_type.setAdapter(bankTypeAdapter);
        bank_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    memberAccount.setAccountBank("AAAA");
                } else if (position == 1) {
                    memberAccount.setAccountBank("AAAB");
                } else if(position==2){
                    memberAccount.setAccountBank("AAAC");
                }else if(position==3){
                    memberAccount.setAccountBank("AAAD");
                }else if(position==4){
                    memberAccount.setAccountBank("AAAE");
                }else if(position==5){
                    memberAccount.setAccountBank("AAAF");
                }else if(position==6){
                    memberAccount.setAccountBank("AAAG");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //중복확인
        Button overlap=(Button)findViewById(R.id.overlap);
        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memID=((TextView)findViewById(R.id.textID)).getText().toString();
                Toast.makeText(getApplicationContext(),memID+" 사용가능!",Toast.LENGTH_LONG).show();
                idValid=true;
                signInMember.setMemID(memID);
                //id만 서버로 보내서 중복확인
//                Response.Listener<String> responseListener=new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject=new JSONObject(response);
//                            boolean success=jsonObject.getBoolean("success");
//                            if(success){ //로그인에 성공한 경우
//                                String userID=jsonObject.getString("userID");
//
//                                Toast.makeText(getApplicationContext(),"사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
//                                idValid=true;
//
//                            }
//                            else{//로그인에 실패한 경우
//                                Toast.makeText(getApplicationContext(),"사용 불가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
//                                idValid=false;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                RequestIdOverlap requestIdOverlap =new RequestIdOverlap(memID,responseListener);
//                RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
//                queue.add(requestIdOverlap);
            }
        });

        //이메일 확인
        Button emailCheck=(Button)findViewById(R.id.emailCheck);
        emailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "이메일 확인!"+signInMember.getMemEmail(), Toast.LENGTH_LONG).show();
                emailValid=true;
            }
        });

        //민증확인
        Button identify=(Button)findViewById(R.id.identify);
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmpssn="970822-10041004";
                Toast.makeText(getApplicationContext(),"민증확인!(미구현)",Toast.LENGTH_LONG).show();
                ssnValid=true;
                signInMember.setMemSsn(tmpssn);
            }
        });

        //회원가입 버튼!
        Button signComplete=(Button)findViewById(R.id.signComplete);
        signComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //아이디 중복확인 여부
                //주민번호 확인 여부
                //이메일 유효성 확인 여부

                //계좌정보 가져오기
                getAccountInfo();

                if(!emailValid&&!idValid&&!ssnValid){
                    Toast.makeText(getApplicationContext(), "잘못된 입력입니다(이메일,주민번호,아이디 확인!)", Toast.LENGTH_LONG).show();
                }else{
                    if(getUserInfo()){
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    if(response.isEmpty()){
                                        showToast("error!");
                                    }
                                    JSONObject jsonObject = new JSONObject( response );

                                    boolean success = jsonObject.getBoolean( "success" );
                                    //회원가입 성공시
                                    if(success) {
                                        //멤버 가져오기
                                        showToast("성공");

                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        intent.putExtra("result", true);
                                        //intent.putExtra("newMember",newMember);


                                        setResult(Activity.RESULT_OK, intent);

                                        finish();

                                        //회원가입 실패시
                                    } else {
                                        showToast("실패");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }
                    };
                    //서버로 Volley를 이용해서 요청
                    RequestRegister requestRegister=new RequestRegister(signInMember.getMemID(),signInMember.getMemPW(),signInMember.getMemName(),signInMember.getMemEmail(),responseListener);
                    //RegisterRequest registerRequest = new RegisterRequest(member, responseListener);
                    RequestQueue queue = Volley.newRequestQueue( SignInActivity.this );
                    queue.add( requestRegister );
                    }else{
                        showToast("빈칸 ㄴㄴ해");
                    }
                }
            }
        });
    }

    protected boolean getUserInfo(){
        //정보들 null인지 체크
        //
        //
        // 전체 빈칸으로 회원가입 클릭 시 오류발생
        //
        //
        String name=((TextView)findViewById(R.id.textName)).getText().toString();
        //if(!TextUtils.isEmpty(name)) return false;
        signInMember.setMemName(name);
        String pw=((TextView)findViewById(R.id.textPW)).getText().toString();
        //if(!TextUtils.isEmpty(pw)) return false;
        signInMember.setMemPW(pw);
        return true;
    }

    protected void getAccountInfo(){
        String accountNum=((TextView)findViewById(R.id.textAccountNum)).getText().toString();
        //if(!TextUtils.isEmpty(accName)) return false;
        memberAccount.setAccountNum(accountNum);
        String accountBalance=((TextView)findViewById(R.id.textAccountBalance)).getText().toString();
        memberAccount.setBalance(accountBalance);
        //if(!TextUtils.isEmpty(accBalance2)) return false;
        String accountPw=findViewById(R.id.textAccountPW).toString();
        memberAccount.setPassword(accountPw);
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

}
