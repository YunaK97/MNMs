package kr.hongik.mnms.firstscreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;

public class SignInActivity extends AppCompatActivity {
    Account signInMemberAccount = new Account();
    Member signInMember = new Member();

    //layouts
    ArrayAdapter bankTypeAdapter;
    Spinner email_type, bank_type;
    Button cameraBtn;

    //urls
    private String curIp = "221.138.13.68:8090";

    //TAGs
    String TAG_SUCCESS = "success", emailForm;
    final String TAG = getClass().getSimpleName();

    //Variables
    boolean idValid = false, ssnValid = false, emailValid = false, pwValid = false;
    final static int TAKE_PICTURE = 1;
    String checkID, checkEmail;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signComplete) {
            if (!getUserInfo() || !getAccountInfo()) {
                showToast("빈칸 ㄴㄴ해");
            } else {
                if (emailValid && idValid && ssnValid && pwValid) {
                    registerBegin();
                } else {
                    if (!emailValid) {
                        showToast("이메일 다시 확인");
                    } else if (!ssnValid) {
                        showToast("민증 다시 확인");
                    } else if (!idValid) {
                        showToast("아이디 다시 확인");
                    } else if (!pwValid) {
                        showToast("비밀번호 불일치");
                    }
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Intent intent = getIntent();
        //curIp = intent.getStringExtra("curIp");

        email_type = findViewById(R.id.email_type);
        final ArrayAdapter emailTypeAdapter = ArrayAdapter.createFromResource(this, R.array.email_type, R.layout.support_simple_spinner_dropdown_item);
        emailTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        email_type.setAdapter(emailTypeAdapter);
        email_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                emailForm = null;
                emailForm = "@" + email_type.getSelectedItem().toString();
                emailForm.replaceAll(" ", "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bank_type = findViewById(R.id.bank_type);
        bankTypeAdapter = ArrayAdapter.createFromResource(this, R.array.bank_type, R.layout.support_simple_spinner_dropdown_item);
        bankTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bank_type.setAdapter(bankTypeAdapter);
        bank_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    signInMemberAccount.setAccountBank("AAAA");
                } else if (position == 1) {
                    signInMemberAccount.setAccountBank("AAAB");
                } else if (position == 2) {
                    signInMemberAccount.setAccountBank("AAAC");
                } else if (position == 3) {
                    signInMemberAccount.setAccountBank("AAAD");
                } else if (position == 4) {
                    signInMemberAccount.setAccountBank("AAAE");
                } else if (position == 5) {
                    signInMemberAccount.setAccountBank("AAAF");
                } else if (position == 6) {
                    signInMemberAccount.setAccountBank("AAAG");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //id 중복확인
        Button overlap = findViewById(R.id.overlap);
        overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkID = ((TextView) findViewById(R.id.textID)).getText().toString();
                checkID.replaceAll(" ", "");

                if (checkID.length() < 4 || checkID.length() > 10) {
                    showToast("4~10 글자 입력");
                } else {
                    //id만 서버로 보내서 중복확인
                    checkOverlap("id");
                }
            }
        });

        //이메일 확인
        Button emailCheck = findViewById(R.id.emailCheck);
        emailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailID = ((TextView) findViewById(R.id.textEmail)).getText().toString();
                if (TextUtils.isEmpty(emailID)) return;
                checkEmail = emailID + emailForm;
                checkOverlap("email");

            }
        });

        //민증확인
        cameraBtn = findViewById(R.id.identify);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.identify) {// 카메라 앱을 여는 소스
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                }
                String tmpssn = "970822-10041004";
                showToast("민증확인! (구현중)");
                ssnValid = true;
                signInMember.setMemSsn(tmpssn);
            }
        });
        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("back", 321);
        setResult(Activity.RESULT_OK, intent);
        finish();
        //super.onBackPressed();
    }


    protected void checkOverlap(String TAG_TYPE) {
        if (TAG_TYPE.equals("id")) {
            String urlIdOverlap = "http://" + curIp + "/member/checkID";

            NetworkTask networkTask = new NetworkTask();
            networkTask.setURL(urlIdOverlap);
            networkTask.setTAG("idOverlap");

            Map<String, String> params = new HashMap<String, String>();
            params.put("memID", checkID);

            networkTask.execute(params);
        } else if (TAG_TYPE.equals("email")) {
            String urlEmailOverlap = "http://" + curIp + "/member/checkEmail";

            NetworkTask networkTask = new NetworkTask();
            networkTask.setURL(urlEmailOverlap);
            networkTask.setTAG("emailOverlap");

            Map<String, String> params = new HashMap<String, String>();
            params.put("memEmail", checkEmail);

            networkTask.execute(params);
        }
    }

    protected void registerBegin() {
        String urlRegister = "http://" + curIp + "/member/join";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlRegister);
        networkTask.setTAG("register");

        Map<String, String> params = new HashMap<>();
        params.put("memID", signInMember.getMemID());
        params.put("memPW", signInMember.getMemPW());
        params.put("memName", signInMember.getMemName());
        params.put("memEmail", signInMember.getMemEmail());
        params.put("memSSN", signInMember.getMemSsn());

        params.put("accountBank", signInMemberAccount.getAccountBank());
        params.put("accountBalance", signInMemberAccount.getAccountBalance() + "");
        params.put("accountNum", signInMemberAccount.getAccountNum());
        params.put("accountPassword", signInMemberAccount.getAccountPassword());

        networkTask.execute(params);
    }

    protected boolean getUserInfo() {
        String name = ((TextView) findViewById(R.id.textName)).getText().toString();
        name.replaceAll(" ", "");
        if (TextUtils.isEmpty(name)) return false;
        else signInMember.setMemName(name);
        String pw = ((TextView) findViewById(R.id.textPW)).getText().toString();
        pw.replaceAll(" ", "");
        if (TextUtils.isEmpty(pw)) return false;
        String checkPw = ((TextView) findViewById(R.id.textCheckPW)).getText().toString();
        checkPw.replaceAll(" ", "");
        if (TextUtils.isEmpty(checkPw)) return false;
        if (pw.equals(checkPw)) {
            pwValid = true;
            signInMember.setMemPW(pw);
        }
        return true;
    }

    protected boolean getAccountInfo() {
        String accountNum = ((TextView) findViewById(R.id.textAccountNum)).getText().toString();
        accountNum.replaceAll(" ", "");
        if (TextUtils.isEmpty(accountNum)) return false;
        else signInMemberAccount.setAccountNum(accountNum);

        String accountBalance = "1000000";
        if (TextUtils.isEmpty(accountBalance)) return false;
        else signInMemberAccount.setAccountBalance(Integer.parseInt(accountBalance));

        String accountPw = ((TextView) findViewById(R.id.textAccountPW)).getText().toString();
        accountPw.replaceAll(" ", "");

        if (TextUtils.isEmpty(accountPw)) return false;
        else if (accountPw.length() != 4) {
            showToast("계좌 비밀번호는 4자리 입니다.");
            return false;
        } else {
            signInMemberAccount.setAccountPassword(accountPw);
        }
        return true;
    }

    protected void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void idOverlapProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                showToast("사용가능한 아이디입니다.");

                signInMember.setMemID(checkID);
                idValid = true;
            } else {
                showToast("사용 불가능한 아이디입니다.");
                idValid = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void emailOverlapProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                showToast("사용가능한 이메일.");

                signInMember.setMemEmail(checkEmail);
                emailValid = true;
            } else {
                showToast("사용 불가능한 이메일.");
                emailValid = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerProcess(String response) {
        try {
            if (response.isEmpty()) {
                showToast("error!");
            }
            JSONObject jsonObject = new JSONObject(response);

            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            //회원가입 성공시
            if (success) {
                showToast("성공");

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.putExtra("result", true);
                intent.putExtra("back", 0);
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

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        private String url;
        private String TAG;

        void setURL(String url) {
            this.url = url;
        }

        void setTAG(String TAG) {
            this.TAG = TAG;
        }

        @Override
        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터

            // Http 요청 준비 작업
            HttpClient.Builder http = new HttpClient.Builder("POST", url);

            // Parameter 를 전송한다.
            http.addAllParameters(maps[0]);

            //Http 요청 전송
            HttpClient post = http.create();
            post.request();
            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();
            // 응답 본문 가져오기

            return post.getBody();
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("signInResult",response);
            if (TAG.equals("idOverlap")) {
                idOverlapProcess(response);
            } else if (TAG.equals("emailOverlap")) {
                emailOverlapProcess(response);
            } else if (TAG.equals("register")) {
                registerProcess(response);
            }

        }
    }
}
