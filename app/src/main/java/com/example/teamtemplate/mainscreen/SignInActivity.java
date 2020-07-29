package com.example.teamtemplate.mainscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SignInActivity extends AppCompatActivity {
//아이디 중복확인
//민증확인(카메라)
//회원가입 완료
    boolean idValid=false,ssnValid=false,emailValid=false;
    Member signInMember = new Member();
    Account memberAccount = new Account();
    Spinner email_type,bank_type;
    final String TAG = getClass().getSimpleName();
    Button cameraBtn;
    final static int TAKE_PICTURE = 1;
    String TAG_SUCCESS="success";
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
                String emailID=((TextView)findViewById(R.id.textEmail)).getText().toString();
                if(!TextUtils.isEmpty(emailID)) {
                    String email = emailID + "@" + email_type.getSelectedItem().toString();
                    signInMember.setMemEmail(email);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bank_type= findViewById(R.id.bank_type);
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
        Button overlap= findViewById(R.id.overlap);
        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String memID=((TextView)findViewById(R.id.textID)).getText().toString();
                if(memID.length()<4 || memID.length()>10) {
                    showToast("4~10 글자 입력");
                }else{
                    //id만 서버로 보내서 중복확인
                    Response.Listener<String> responseListener=new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                                if(success){
                                    showToast("사용가능한 아이디입니다.");
                                    signInMember.setMemID(memID);
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
                    };
                    RequestWork requestWork =new RequestWork(memID,responseListener);
                    RequestQueue queue= Volley.newRequestQueue(SignInActivity.this);
                    queue.add(requestWork);
                }
            }
        });

        //이메일 확인
        Button emailCheck= findViewById(R.id.emailCheck);
        emailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signInMember.getMemEmail()!=null){
                    showToast("이메일 확인!"+signInMember.getMemEmail());
                    emailValid=true;
                }else{
                    showToast("빈칸 놉!");
                }
            }
        });

        //민증확인
        cameraBtn= findViewById(R.id.identify);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.identify:
                        // 카메라 앱을 여는 소스
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_PICTURE);
                        break;
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
                    if(emailValid&&idValid&&ssnValid){
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
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
                                        setResult(Activity.RESULT_OK, intent);

                                        finish();
                                    } else {
                                        //회원가입 실패
                                        showToast("실패");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                    };

                    //서버로 Volley를 이용해서 요청
                    //RequestRegister registerRequest = new RequestRegister(signInMember,memberAccount, responseListener);
                    RequestWork requestWork =new RequestWork(signInMember,memberAccount,responseListener);
                    RequestQueue queue = Volley.newRequestQueue( SignInActivity.this );
                    queue.add(requestWork);
                    //queue.add( registerRequest );
                    }else{
                        showToast("잘못된 입력!(이메일,주민번호,아이디 확인~)");
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
    protected boolean getUserInfo(){
        String name=((TextView)findViewById(R.id.textName)).getText().toString();
        if(TextUtils.isEmpty(name)) return false;
        else signInMember.setMemName(name);
        String pw=((TextView)findViewById(R.id.textPW)).getText().toString();
        if(TextUtils.isEmpty(pw)) return false;
        signInMember.setMemPW(pw);
        return true;
    }

    protected boolean getAccountInfo(){
        String accountNum=((TextView)findViewById(R.id.textAccountNum)).getText().toString();
        if(TextUtils.isEmpty(accountNum)) return false;
        else memberAccount.setAccountNum(accountNum);

        String accountBalance=((TextView)findViewById(R.id.textAccountBalance)).getText().toString();

        if(TextUtils.isEmpty(accountBalance)) return false;
        else memberAccount.setAccountBalance(Integer.parseInt(accountBalance));

        String accountPw=((TextView)findViewById(R.id.textAccountPW)).getText().toString();
        if(TextUtils.isEmpty(accountPw)) return false;
        else if(accountPw.length()!=4){
            showToast("계좌 비밀번호는 4자리 입니다.");
            return false;
        }else {
            memberAccount.setAccountPassword(accountPw);
        }

        return true;
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

}
