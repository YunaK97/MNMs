package com.hongik.mnms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
//아이디 중복확인
//민증확인
//회원가입 완료
    String name,id,pw,email,accName,ssn,accBalance2;
    Integer accBalance;
    boolean idValid=false,ssnValid=false,emailValid=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button overlap=(Button)findViewById(R.id.overlap);
        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memID=((TextView)findViewById(R.id.textID)).getText().toString();
                Toast.makeText(getApplicationContext(),memID+" 사용가능!",Toast.LENGTH_LONG).show();
                idValid=true;
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
                String email2=((TextView)findViewById(R.id.textEmail)).getText().toString();
                String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(email2);

                if ( !m.matches()){
                    Toast.makeText(SignInActivity.this, "Email형식으로 입력하세요", Toast.LENGTH_SHORT).show();
                    emailValid=false;
                }else {
                    Toast.makeText(getApplicationContext(), "이메일 확인!", Toast.LENGTH_LONG).show();
                    email=email2;
                    emailValid = true;
                }
            }
        });

        //민증확인
        Button identify=(Button)findViewById(R.id.identify);
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssn="970822-10041004";
                Toast.makeText(getApplicationContext(),"민증확인!(미구현)",Toast.LENGTH_LONG).show();
                ssnValid=true;
            }
        });

        //회원가입 버튼!
        Button signComplete=(Button)findViewById(R.id.signComplete);
        signComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getUserInfo()){

                //아이디 중복확인 여부
                //주민번호 확인 여부
                //이메일 유효성 확인 여부
                //정보들 null인지 체크
                //TextUtils.isEmpty(text.getText()
                    accBalance=Integer.parseInt(accBalance2);
                    final Member member = new Member();
                    member.setMemName(name);
                    member.setMemPW(pw);
                    member.setMemID(id);
                    member.setMemEmail(email);
                    member.setMemSsn(ssn);

                    //member 서버에 전송 작업하기!
                    //
                    //로그인에서 객체로 전달 되는지 보고 객체로 전달하기
                    //
                    //
                    //
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject( response );
                                boolean success = jsonObject.getBoolean( "success" );

                                //회원가입 성공시
                                if(success) {
                                    //멤버 가져오기
                                    Toast.makeText( getApplicationContext(), "성공", Toast.LENGTH_SHORT ).show();
                                    successSignIn(member);

                                    //회원가입 실패시
                                } else {
                                    Toast.makeText( getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    //서버로 Volley를 이용해서 요청
                    RequestRegister requestRegister=new RequestRegister(member.getMemID(),member.getMemPW(),member.getMemName(),member.getMemEmail(),responseListener);
                    //RegisterRequest registerRequest = new RegisterRequest(member, responseListener);
                    RequestQueue queue = Volley.newRequestQueue( SignInActivity.this );
                    queue.add( requestRegister );

                }else{
                    Toast.makeText(getApplicationContext(),"빈칸 ㄴㄴ해",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void successSignIn(Member member){
        if (emailValid && ssnValid && idValid) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.putExtra("result", true);
            intent.putExtra("newMember",member);

            setResult(Activity.RESULT_OK, intent);

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "잘못된 입력입니다(이메일,주민번호,아이디 확인!)", Toast.LENGTH_LONG).show();
        }

    }

    protected boolean getUserInfo(){
        //
        //
        // 전체 빈칸으로 회원가입 클릭 시 오류발생
        //
        //
        name=((TextView)findViewById(R.id.textName)).getText().toString();
        //if(!TextUtils.isEmpty(name)) return false;
        id=((TextView)findViewById(R.id.textID)).getText().toString();
        //if(!TextUtils.isEmpty(id)) return false;
        pw=((TextView)findViewById(R.id.textPW)).getText().toString();
        //if(!TextUtils.isEmpty(pw)) return false;
        email=((TextView)findViewById(R.id.textEmail)).getText().toString();
        //if(!TextUtils.isEmpty(email)) return false;

        accName=((TextView)findViewById(R.id.textAccname)).getText().toString();
        //if(!TextUtils.isEmpty(accName)) return false;
        accBalance2=((TextView)findViewById(R.id.textAccount)).getText().toString();
        //if(!TextUtils.isEmpty(accBalance2)) return false;
        return true;
    }

}
