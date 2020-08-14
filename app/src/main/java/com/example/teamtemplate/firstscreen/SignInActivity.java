package com.example.teamtemplate.firstscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.mainscreen.MainMenuActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SignInActivity extends AppCompatActivity {
//아이디 중복확인
//민증확인(카메라)
//회원가입 완료
    boolean idValid=false,ssnValid=false,emailValid=false,pwValid=false;
    Member signInMember = new Member();
    ArrayAdapter bankTypeAdapter;
    Account signInMemberAccount = new Account();
    Spinner email_type,bank_type;
    final String TAG = getClass().getSimpleName();
    Button cameraBtn;
    final static int TAKE_PICTURE = 1;
    String TAG_SUCCESS="success",emailForm;
    int TAG_EMAIL=123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email_type= findViewById(R.id.email_type);
        final ArrayAdapter emailTypeAdapter=ArrayAdapter.createFromResource(this,R.array.email_type,R.layout.support_simple_spinner_dropdown_item);
        emailTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        email_type.setAdapter(emailTypeAdapter);
        email_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emailForm=null;
                emailForm = "@" + email_type.getSelectedItem().toString();
                emailForm.replaceAll(" ","");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bank_type= findViewById(R.id.bank_type);
        bankTypeAdapter=ArrayAdapter.createFromResource(this,R.array.bank_type,R.layout.support_simple_spinner_dropdown_item);
        bankTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bank_type.setAdapter(bankTypeAdapter);
        bank_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    signInMemberAccount.setAccountBank("AAAA");
                } else if (position == 1) {
                    signInMemberAccount.setAccountBank("AAAB");
                } else if(position==2){
                    signInMemberAccount.setAccountBank("AAAC");
                }else if(position==3){
                    signInMemberAccount.setAccountBank("AAAD");
                }else if(position==4){
                    signInMemberAccount.setAccountBank("AAAE");
                }else if(position==5){
                    signInMemberAccount.setAccountBank("AAAF");
                }else if(position==6){
                    signInMemberAccount.setAccountBank("AAAG");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //id 중복확인
        Button overlap= findViewById(R.id.overlap);
        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memID=((TextView)findViewById(R.id.textID)).getText().toString();
                memID.replaceAll(" ","");

                if(memID.length()<4 || memID.length()>10) {
                    showToast("4~10 글자 입력");
                }else{
                    //id만 서버로 보내서 중복확인
                    checkOverlap("id",memID);
                }
            }
        });

        //이메일 확인
        Button emailCheck= findViewById(R.id.emailCheck);
        emailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailID=((TextView)findViewById(R.id.textEmail)).getText().toString();
                if (TextUtils.isEmpty(emailID)) return;

                checkOverlap("email",emailID+emailForm);

            }
        });

        //민증확인
        cameraBtn= findViewById(R.id.identify);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.identify) {// 카메라 앱을 여는 소스
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                }
                String tmpssn="970822-10041004";
                showToast("민증확인! (구현중)");
                ssnValid=true;
                signInMember.setMemSsn(tmpssn);
            }
        });
        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //회원가입 버튼!
        Button signComplete= findViewById(R.id.signComplete);
        signComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //아이디 중복확인 여부
                //주민번호 확인 여부
                //이메일 유효성 확인 여부

                //계좌정보 가져오기
                if(!getUserInfo() || !getAccountInfo()){
                    showToast("빈칸 ㄴㄴ해");
                }else{
                    if(emailValid&&idValid&&ssnValid&&pwValid){
                        registerProcess();
                    }else{
                        if(!emailValid){
                            showToast("이메일 다시 확인");
                        }else if(!ssnValid){
                            showToast("민증 다시 확인");
                        }else if(!idValid){
                            showToast("아이디 다시 확인");
                        }else if(!pwValid){
                            showToast("비밀번호 불일치");
                        }
                    }
                }
            }
        });
    }
 //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("back",321);
        setResult(Activity.RESULT_OK, intent);
        finish();
        //super.onBackPressed();
    }


    protected void checkOverlap(String TAG_TYPE, final String checkString){
        if(TAG_TYPE.equals("id")){
            final String url="http://jennyk97.dothome.co.kr/IdOverlap.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                        if(success){
                            showToast("사용가능한 아이디입니다.");

                            signInMember.setMemID(checkString);
                            idValid=true;
                        }
                        else{
                            showToast("사용 불가능한 아이디입니다.");
                            idValid=false;
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
                    params.put("memID", checkString);
                    return params;
                }
            };

            RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
            queue.add(stringRequest);
        }
        else if(TAG_TYPE.equals("email")){
            final String url="http://jennyk97.dothome.co.kr/EmailOverlap.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("emailCheck",response);
                        JSONObject jsonObject=new JSONObject(response);
                        boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                        if(success){
                            signInMember.setMemEmail(checkString);
                            Log.d("emailuse",signInMember.getMemEmail());
                            emailValid=true;
                            showToast("사용가능한 이메일");
                        }
                        else{
                            emailValid=false;
                            showToast("사용 불가능한 이메일");
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
                    params.put("memID", checkString);
                    return params;
                }
            };

            RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
            queue.add(stringRequest);
        }
    }

    protected void registerProcess(){
        final String url="http://jennyk97.dothome.co.kr/Register.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if(response.isEmpty()){
                        showToast("error!");
                    }
                    JSONObject jsonObject = new JSONObject( response );

                    boolean success = jsonObject.getBoolean( TAG_SUCCESS );
                    //회원가입 성공시
                    if(success) {
                        showToast("성공");

                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("result", true);
                        intent.putExtra("back",0);
                        setResult(221, intent);

                        finish();
                    } else {
                        //회원가입 실패
                        showToast("실패");
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
                params.put("memID", signInMember.getMemID());
                params.put("memPW", signInMember.getMemPW());
                params.put("memName", signInMember.getMemName());
                params.put("memEmail",signInMember.getMemEmail());

                params.put("accountBank",signInMemberAccount.getAccountBank());
                params.put("accountBalance",signInMemberAccount.getAccountBalance()+"");
                params.put("accountNum",signInMemberAccount.getAccountNum());
                params.put("accountPassword",signInMemberAccount.getAccountPassword());
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
        queue.add(stringRequest);
    }
    protected boolean getUserInfo(){
        String name=((TextView)findViewById(R.id.textName)).getText().toString();
        name.replaceAll(" ","");
        if(TextUtils.isEmpty(name)) return false;
        else signInMember.setMemName(name);
        String pw=((TextView)findViewById(R.id.textPW)).getText().toString();
        pw.replaceAll(" ","");
        if(TextUtils.isEmpty(pw)) return false;
        String checkPw=((TextView)findViewById(R.id.textCheckPW)).getText().toString();
        checkPw.replaceAll(" ","");
        if(TextUtils.isEmpty(checkPw)) return false;
        if(pw.equals(checkPw)){
            pwValid=true;
            signInMember.setMemPW(pw);
        }
        return true;
    }

    protected boolean getAccountInfo(){
        String accountNum=((TextView)findViewById(R.id.textAccountNum)).getText().toString();
        accountNum.replaceAll(" ","");
        if(TextUtils.isEmpty(accountNum)) return false;
        else signInMemberAccount.setAccountNum(accountNum);

        String accountBalance="1000000";
        if(TextUtils.isEmpty(accountBalance)) return false;
        else signInMemberAccount.setAccountBalance(Integer.parseInt(accountBalance));

        String accountPw=((TextView)findViewById(R.id.textAccountPW)).getText().toString();
        accountPw.replaceAll(" ","");

        if(TextUtils.isEmpty(accountPw)) return false;
        else if(accountPw.length()!=4){
            showToast("계좌 비밀번호는 4자리 입니다.");
            return false;
        }else {
            signInMemberAccount.setAccountPassword(accountPw);
        }
        return true;
    }

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

}
