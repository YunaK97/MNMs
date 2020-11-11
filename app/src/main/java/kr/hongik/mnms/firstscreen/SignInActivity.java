package kr.hongik.mnms.firstscreen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private Account signInMemberAccount = new Account();
    private Member signInMember = new Member();

    //layouts
    private ArrayAdapter bankTypeAdapter, emailTypeAdapter;
    private Spinner spinnerEmailType, spinnerBankType;
    private Button cameraBtn, btnSignIn, btnEmailAuth, btnEmailOverlap;

    //urls
    private String curIp = "211.186.21.254:8090";

    //TAGs
    public String TAG_SUCCESS = "success", emailForm;
    public final String TAG = getClass().getSimpleName();

    //Variables
    private boolean idValid = false, ssnValid = false, emailValid = false, pwValid = false;
    private final static int TAKE_PICTURE = 1;
    private String checkID, checkEmail, authEmail;
    private String emailAuthNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setAccountNum();

        //회원가입 버튼
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);

        //이메일 종류 선택
        spinnerEmailType = findViewById(R.id.spinnerEmailType);
        emailTypeAdapter = ArrayAdapter.createFromResource(this, R.array.email_type, R.layout.support_simple_spinner_dropdown_item);
        emailTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerEmailType.setAdapter(emailTypeAdapter);
        spinnerEmailType.setSelection(0);

        //은행 종류 선택
        spinnerBankType = findViewById(R.id.spinnerBankType);
        bankTypeAdapter = ArrayAdapter.createFromResource(this, R.array.bank_type, R.layout.support_simple_spinner_dropdown_item);
        bankTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerBankType.setAdapter(bankTypeAdapter);
        spinnerBankType.setSelection(0);

        //id 중복확인
        Button overlap = findViewById(R.id.btnIdOverlap);
        overlap.setOnClickListener(this);

        //이메일 확인
        btnEmailOverlap = findViewById(R.id.btnEmailOverlap);
        btnEmailOverlap.setOnClickListener(this);

        //이메일 인증
        btnEmailAuth = findViewById(R.id.btnEmailAuth);
        btnEmailAuth.setOnClickListener(this);

        //민증확인
        cameraBtn = findViewById(R.id.btnIdentify);
        cameraBtn.setOnClickListener(this);
        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.d(TAG, "권한 설정 완료");
            } else {
                //Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        setResult(MainActivity.TAG_BACK, intent);
        finish();
        //super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                beforeSignInCheck();
                break;
            case R.id.btnIdOverlap:
                checkID = ((TextView) findViewById(R.id.etID)).getText().toString();
                checkID = checkID.trim();

                if (checkID.length() < 4 || checkID.length() > 20) {
                    showToast("4~20 글자 입력");
                } else {
                    //id만 서버로 보내서 중복확인
                    checkOverlap("id");
                }
                break;
            case R.id.btnEmailOverlap:
                String emailID = ((TextView) findViewById(R.id.etEmail)).getText().toString();
                if (TextUtils.isEmpty(emailID)) {
                    showToast("빈칸 노노");
                    return;
                }
                emailForm = spinnerEmailType.getSelectedItem().toString();
                if (emailForm.equals("이메일")) {
                    showToast("이메일을 확인하세요");
                    return;
                }
                emailForm = "@" + emailForm;
                checkEmail = emailID + emailForm;
                checkOverlap("email");
                break;
            case R.id.btnEmailAuth:
                String etCheckEmail=((EditText)findViewById(R.id.etCheckEmail)).getText().toString();
                showToast(emailAuthNumber+" : "+etCheckEmail);

                if(etCheckEmail.equals(emailAuthNumber)){
                    showToast("인증 완료");
                    btnEmailAuth.setClickable(false);
                    btnEmailOverlap.setClickable(false);
                }else{
                    showToast("인증번호가 일치하지 않습니다");
                }

                break;
            case R.id.btnIdentify:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PICTURE);
                String tmpssn = "970822-10041004";
                showToast("민증확인! (구현중)");
                ssnValid = true;
                signInMember.setMemSsn(tmpssn);
                break;

        }
    }

    private void setAccountNum() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int div1 = random.nextInt(100) + 1000;
        int div2 = random.nextInt(100) + 10000;
        int div3 = random.nextInt(100) + 100;
        String accountNum = (int) (Math.random() * div1) + "-" + (int) (Math.random() * div2) + "-" + (int) (Math.random() * div3);

        signInMemberAccount.setAccountNum(accountNum);

        TextView tvAccountNum = findViewById(R.id.tvAccountNum);
        tvAccountNum.setText(accountNum);
    }

    protected void checkOverlap(String TAG_TYPE) {
        //중복체크하는 부분임
        //id 중복체크 : 작성한 id전송 - 테이블에 이미 있는 id일 경우 false를 받음
        //email 중복체크 : 작성한 email전송 - 테이블에 이미 있는 email일 경우 false를 받음
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

    private void beforeSignInCheck() {
        //빈칸 체크
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
    }

    private boolean getUserInfo() {
        String name = ((TextView) findViewById(R.id.etName)).getText().toString();
        name = name.trim();
        if (TextUtils.isEmpty(name)) return false;
        else signInMember.setMemName(name);

        String pw = ((TextView) findViewById(R.id.etPW)).getText().toString();
        pw = pw.trim();
        if (TextUtils.isEmpty(pw)) return false;
        if (pw.length() < 8 || pw.length() > 20) {
            showToast("비밀번호 : 8~20자");
            return false;
        }
        String checkPw = ((TextView) findViewById(R.id.etCheckPW)).getText().toString();
        checkPw = checkPw.trim();
        if (TextUtils.isEmpty(checkPw)) return false;
        if (pw.equals(checkPw)) {
            pwValid = true;
            signInMember.setMemPW(pw);
        } else {
            showToast("비밀번호가 일치하지 않습니다.");
            pwValid = false;
            return false;
        }

        String phone2, phone3, phoneNumber;
        phone2 = ((TextView) findViewById(R.id.tvSignInPhone2)).getText().toString();
        phone3 = ((TextView) findViewById(R.id.tvSignInPhone3)).getText().toString();
        if (TextUtils.isEmpty(phone2) || TextUtils.isEmpty(phone3))
            return false;
        if (phone2.length() != 4 || phone3.length() != 4) {
            showToast("올바르지 않은 번호입니다.");
            return false;
        }
        phoneNumber ="010" + phone2 + phone3;
        signInMember.setPhoneNumber(phoneNumber);

        return true;
    }

    private boolean getAccountInfo() {
        if (spinnerBankType.getSelectedItemPosition() == 0) {
            signInMemberAccount.setAccountBank("국민");
        } else if (spinnerBankType.getSelectedItemPosition() == 1) {
            signInMemberAccount.setAccountBank("우리");
        } else if (spinnerBankType.getSelectedItemPosition() == 2) {
            signInMemberAccount.setAccountBank("신한");
        } else if (spinnerBankType.getSelectedItemPosition() == 3) {
            signInMemberAccount.setAccountBank("하나");
        } else if (spinnerBankType.getSelectedItemPosition() == 4) {
            signInMemberAccount.setAccountBank("카카오뱅크");
        } else if (spinnerBankType.getSelectedItemPosition() == 5) {
            signInMemberAccount.setAccountBank("농협");
        } else if (spinnerBankType.getSelectedItemPosition() == 6) {
            signInMemberAccount.setAccountBank("IBK");
        }

        String accountBalance = "10000000";
        if (TextUtils.isEmpty(accountBalance)) return false;
        else signInMemberAccount.setAccountBalance(Integer.parseInt(accountBalance));

        String accountPw = ((TextView) findViewById(R.id.tvAccountPW)).getText().toString();
        accountPw = accountPw.trim();

        String accountPWCheck = ((TextView) findViewById(R.id.tvAccountPWCheck)).getText().toString();
        accountPWCheck = accountPWCheck.trim();
        if (TextUtils.isEmpty(accountPw)) return false;
        else if (accountPw.length() != 4) {
            showToast("계좌 비밀번호는 4자리 입니다.");
            return false;
        }

        if (accountPw.equals(accountPWCheck)) {
            signInMemberAccount.setAccountPassword(accountPw);
        } else {
            showToast("계좌 비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    }

    protected void registerBegin() {
        //회원가입을 위함
        //가입하기 위해 작성한 member의 전체 정보,account의 전체정보 전달
        //결과로 성공/실패 여부 전송받아야함
        String urlRegister = "http://" + curIp + "/member/join";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlRegister);
        networkTask.setTAG("registerBegin");

        Map<String, String> params = new HashMap<>();
        params.put("memID", signInMember.getMemID());
        params.put("memPW", signInMember.getMemPW());
        params.put("memName", signInMember.getMemName());
        params.put("memEmail", signInMember.getMemEmail());
        params.put("memSsn", signInMember.getMemSsn());
        params.put("phoneNumber", signInMember.getPhoneNumber());

        params.put("accountBank", signInMemberAccount.getAccountBank());
        params.put("accountBalance", signInMemberAccount.getAccountBalance() + "");
        params.put("accountNum", signInMemberAccount.getAccountNum());
        params.put("accountPassword", signInMemberAccount.getAccountPassword());

        networkTask.execute(params);
    }

    private void showToast(String data) {
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
                signInMember.setMemEmail(checkEmail);
                LinearLayout LLSignInEmailCheck = findViewById(R.id.LLSignInEmailCheck);
                LLSignInEmailCheck.setVisibility(View.VISIBLE);

                showToast("이메일로 인증번호가 발송되었습니다.");

                String urlEmailAuth = "http://" + curIp + "/member/mail";

                NetworkTask networkTask = new NetworkTask();
                networkTask.setURL(urlEmailAuth);
                networkTask.setTAG("emailAuth");

                Map<String, String> params = new HashMap<String, String>();
                params.put("memEmail", checkEmail);

                networkTask.execute(params);

            } else {
                showToast("사용 불가능한 이메일.");
                emailValid = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void emailAuthProcess(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        emailAuthNumber = jsonObject.getString("number");

    }

    private void registerBeginProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            //회원가입 성공시
            if (success) {
                showToast("성공");

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                setResult(MainActivity.TAG_SIGNIN, intent);
                finish();
            } else {
                //회원가입 실패
                showToast("실패");
            }

        } catch (JSONException e) {
            //e.printStackTrace();
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
            Log.d(TAG, response);

            if (TAG.equals("idOverlap")) {
                idOverlapProcess(response);
            } else if (TAG.equals("emailOverlap")) {
                emailOverlapProcess(response);
            } else if (TAG.equals("registerBegin")) {
                registerBeginProcess(response);
            } else if (TAG.equals("emailAuth")) {
                try {
                    emailAuthProcess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
