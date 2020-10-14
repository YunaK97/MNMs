package kr.hongik.mnms.membership.ui.manage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.PasswordAuthentication;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.membership.CustomDialogDuration;
import kr.hongik.mnms.membership.MembershipActivity;
import kr.hongik.mnms.membership.MembershipGroup;

public class ManageMembershipActivity extends AppCompatActivity {
    //president가 membership 수정, 관리하는 곳
    //fee, 회장위임, fee duratio
    // 4.회장이 멤버 탈퇴 가능
    // 회장만이 멤버십 카운트 조정 가능
    //이달의 fee마감 버튼 - 마감날부터 나타나게 하기!

    private Member loginMember;
    private MembershipGroup membershipGroup;

    private Date today = new Date();
    private SimpleDateFormat todayFormat;
    private int dayNum;//오늘의 요일
    private Calendar calendar;

    //Layouts
    private EditText etNewNotSubmit, etNewName, etNewFee;
    private TextView new_membership_payType, new_membership_payTypeNum;
    private Button btn_feeSubmitComplete;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_membership_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.membership_manage) {
            changeDate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_membership);

        final Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");

        etNewFee = findViewById(R.id.new_membership_fee);
        etNewName = findViewById(R.id.new_membership_name);
        etNewNotSubmit = findViewById(R.id.new_membership_notsubmit);
        btn_feeSubmitComplete = findViewById(R.id.btn_feeSubmitComplete);

        etNewFee.setText(membershipGroup.getFee() / 10000 + "");
        etNewNotSubmit.setText(membershipGroup.getNotSubmit() + "");
        etNewName.setText(membershipGroup.getGroupName());

        new_membership_payType = findViewById(R.id.new_membership_payType);
        new_membership_payTypeNum = findViewById(R.id.new_membership_payTypeNum);

        if (membershipGroup.getPayDuration().equals("week")) {
            new_membership_payType.setText("매주");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date nDate = dateFormat.parse(membershipGroup.getPayDay());

                Calendar cal = Calendar.getInstance();
                cal.setTime(nDate);

                int dayNum = cal.get(Calendar.DAY_OF_WEEK);

                switch (dayNum) {
                    case 1:
                        new_membership_payTypeNum.setText("일");
                        break;
                    case 2:
                        new_membership_payTypeNum.setText("월");
                        break;
                    case 3:
                        new_membership_payTypeNum.setText("화");
                        break;
                    case 4:
                        new_membership_payTypeNum.setText("수");
                        break;
                    case 5:
                        new_membership_payTypeNum.setText("목");
                        break;
                    case 6:
                        new_membership_payTypeNum.setText("금");
                        break;
                    case 7:
                        new_membership_payTypeNum.setText("토");
                        break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (membershipGroup.getPayDuration().equals("month")) {
            new_membership_payType.setText("매월");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date nDate = dateFormat.parse(membershipGroup.getPayDay());

                Calendar cal = Calendar.getInstance();
                cal.setTime(nDate);

                int monNum = cal.get(Calendar.MONTH) + 1;
                new_membership_payTypeNum.setText(monNum + "");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (membershipGroup.getPayDuration().equals("year")) {
            new_membership_payType.setText("매년");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date nDate = dateFormat.parse(membershipGroup.getPayDay());

                Calendar cal = Calendar.getInstance();
                cal.setTime(nDate);

                int monNum = cal.get(Calendar.MONTH) + 1;
                int dayNum = cal.get(Calendar.DATE);
                new_membership_payTypeNum.setText(monNum + "-" + dayNum);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        new_membership_payType.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                CustomDialogDuration customDialogDuration = new CustomDialogDuration(ManageMembershipActivity.this);
                customDialogDuration.callFunction(new_membership_payType, new_membership_payTypeNum);
            }
        });
        new_membership_payTypeNum.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                CustomDialogDuration customDialogDuration = new CustomDialogDuration(ManageMembershipActivity.this);
                customDialogDuration.callFunction(new_membership_payType, new_membership_payTypeNum);
            }
        });

        checkEndDay();

        btn_feeSubmitComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageMembershipActivity.this, R.style.CustomDialog);

                builder.setTitle("회비를 마감하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        completeFee();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });

    }

    private void checkEndDay() {
        //마감날 체크
        //마감날 이후 - 버튼 항상 보임,클릭 가능
        //마감날 이전 - 버튼 가려둠, 클릭 불가능

        todayFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar curDate = Calendar.getInstance();
        curDate.setTime(today);

        Calendar payDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date nDate = dateFormat.parse(membershipGroup.getPayDay());

            payDate = Calendar.getInstance();
            payDate.setTime(nDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(curDate.compareTo(payDate) >= 0){
            btn_feeSubmitComplete.setVisibility(View.VISIBLE);
            btn_feeSubmitComplete.setClickable(true);
        }else{
            btn_feeSubmitComplete.setVisibility(View.INVISIBLE);
            btn_feeSubmitComplete.setClickable(false);
        }
    }

    private void completeFee() {
        String urlCompleteFee = "http://" + loginMember.getIp() + "/membership/unpay";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("completeFee");
        networkTask.setURL(urlCompleteFee);

        Map<String, String> params = new HashMap<>();
        params.put("MID", membershipGroup.getMID() + "");
        params.put("NotSubmit", membershipGroup.getNotSubmit() + "");
        params.put("GID", membershipGroup.getGID() + "");
        params.put("Duration", membershipGroup.getPayDuration());

        networkTask.execute(params);
    }

    private void changeNotSubmit() {
        String urlChangeNotSubmit = "http://" + loginMember.getIp() + "/membership/updateNotSubmit";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("changeNotSubmit");
        networkTask.setURL(urlChangeNotSubmit);
        etNewNotSubmit = findViewById(R.id.new_membership_notsubmit);
        String newNotSubmit = (etNewNotSubmit).getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("NotSubmit", newNotSubmit);
        params.put("MID", membershipGroup.getMID() + "");

        networkTask.execute(params);

    }

    private void changeMembershipName() {
        String urlChangeMembershipName = "http://" + loginMember.getIp() + "/membership/updatename";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("changeMembershipName");
        networkTask.setURL(urlChangeMembershipName);

        etNewName = findViewById(R.id.new_membership_name);
        String newName = (etNewName).getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("name", newName);
        params.put("GID", membershipGroup.getGID() + "");

        networkTask.execute(params);
    }

    private void changeDate() {
        String urlChangeDate = "http://" + loginMember.getIp() + "/membership/date";

        String payType = ((TextView) findViewById(R.id.new_membership_payType)).getText().toString();

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("changeDate");
        networkTask.setURL(urlChangeDate);

        findStartDay();

        Map<String, String> params = new HashMap<>();
        params.put("MID", membershipGroup.getMID() + "");
        params.put("date", todayFormat.format(calendar.getTime()));
        if (payType.equals("매월")) {
            params.put("Duration", "MONTH");
        } else if (payType.equals("매주")) {
            params.put("Duration", "WEEK");
        } else if (payType.equals("매년")) {
            params.put("Duration", "YEAR");
        }

        networkTask.execute(params);
    }

    private void changeUpdatePay() {
        String urlChangeUpdatePay = "http://" + loginMember.getIp() + "/membership/updatepay";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("changeUpdatePay");
        networkTask.setURL(urlChangeUpdatePay);

        etNewFee = findViewById(R.id.new_membership_fee);
        String newFee = (etNewFee).getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("pay", newFee);
        params.put("MID", membershipGroup.getMID() + "");

        networkTask.execute(params);
    }

    private void findStartDay() {
        todayFormat = new SimpleDateFormat("yyyy-MM-dd");

        calendar = Calendar.getInstance();
        calendar.setTime(today);
        dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        String payType = ((TextView) findViewById(R.id.new_membership_payType)).getText().toString();
        String payTypeNum = ((TextView) findViewById(R.id.new_membership_payTypeNum)).getText().toString();

        if (payType.equals("매주")) {
            int payNum = dayNum;//내가 정한 요일
            if (payTypeNum.equals("일")) {
                payNum = 1;
            } else if (payTypeNum.equals("월")) {
                payNum = 2;
            } else if (payTypeNum.equals("화")) {
                payNum = 3;
            } else if (payTypeNum.equals("수")) {
                payNum = 4;
            } else if (payTypeNum.equals("목")) {
                payNum = 5;
            } else if (payTypeNum.equals("금")) {
                payNum = 6;
            } else if (payTypeNum.equals("토")) {
                payNum = 7;
            }


            if (payNum - dayNum < 0) {
                calendar.add(Calendar.DATE, (7 - (dayNum - payNum)));
            } else {
                calendar.add(Calendar.DATE, payNum - dayNum);
            }
        } else if (payType.equals("매월")) {
            int day = calendar.get(Calendar.DATE);
            if (day == Integer.parseInt(payTypeNum)) {
                //같을땐 오늘 날짜로
            } else if (day > Integer.parseInt(payTypeNum)) {
                //내려는 날짜가 이미 지난 경우 다음달로 넘겨야함
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DATE, Integer.parseInt(payTypeNum));
            } else if (day < Integer.parseInt(payTypeNum)) {
                calendar.set(Calendar.DATE, Integer.parseInt(payTypeNum));
            }
        } else if (payType.equals("매년")) {
            String month = payTypeNum.substring(0, 2);
            String day = payTypeNum.substring(3);
            calendar.set(Calendar.MONTH, Integer.parseInt(month));
            calendar.set(Calendar.DATE, Integer.parseInt(day));
        } else {
            showToast(payType);
        }
    }

    protected void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }


    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url, TAG;

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
            if (TAG.equals("completeFee")) {
                Log.d("completeFee", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("changeDate")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        changeUpdatePay();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("changeUpdatePay")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        changeMembershipName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("changeMembershipName")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        changeNotSubmit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("changeNotSubmit")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(ManageMembershipActivity.this, MembershipActivity.class);
                        setResult(159, intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}