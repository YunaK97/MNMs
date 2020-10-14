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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

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

    //Layouts
    private EditText etNewNotSubmit,etNewName,etNewFee;
    private TextView new_membership_payType,new_membership_payTypeNum;
    private Button btn_feeSubmitComplete;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_membership_manage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.membership_manage) {
            changeMembershipInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_membership);

        final Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");
        membershipGroup= (MembershipGroup) intent.getSerializableExtra("membershipGroup");

        etNewFee=findViewById(R.id.new_membership_fee);
        etNewName=findViewById(R.id.new_membership_name);
        etNewNotSubmit=findViewById(R.id.new_membership_notsubmit);
        btn_feeSubmitComplete=findViewById(R.id.btn_feeSubmitComplete);

        etNewFee.setText(membershipGroup.getFee()+"");
        etNewNotSubmit.setText(membershipGroup.getNotSubmit()+"");
        etNewName.setText(membershipGroup.getGroupName());

        new_membership_payType=findViewById(R.id.new_membership_payType);
        new_membership_payTypeNum=findViewById(R.id.new_membership_payTypeNum);
        new_membership_payType.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                CustomDialogDuration customDialogDuration=new CustomDialogDuration(ManageMembershipActivity.this);
                customDialogDuration.callFunction(new_membership_payType,new_membership_payTypeNum);
            }
        });
        new_membership_payTypeNum.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                CustomDialogDuration customDialogDuration=new CustomDialogDuration(ManageMembershipActivity.this);
                customDialogDuration.callFunction(new_membership_payType,new_membership_payTypeNum);
            }
        });

        checkEndDay();

        btn_feeSubmitComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ManageMembershipActivity.this,R.style.CustomDialog);

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

    private void checkEndDay(){
        //마감날 체크
        //마감날 이후 - 버튼 항상 보임,클릭 가능
        //마감날 이전 - 버튼 가려둠, 클릭 불가능
    }
    private void completeFee(){
        String urlCompleteFee="http://"+loginMember.getIp()+"/membership/unpay";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("completeFee");
        networkTask.setURL(urlCompleteFee);

        Map<String,String> params=new HashMap<>();
        params.put("MID",membershipGroup.getMID()+"");
        params.put("NotSubmit",membershipGroup.getNotSubmit()+"");
        params.put("GID",membershipGroup.getGID()+"");

        networkTask.execute(params);
    }

    protected void changeMembershipInfo(){
        //멤버십 관련 정보 수정
        //MID,GID와 변경된 회비,그룹이름,미납가능횟수를 전달
        //성공적으로 멤버십정보 수정됐는지 여부를 받아야함
        String urlChangeMembershipInfo=""+loginMember.getIp()+"";

        etNewFee=findViewById(R.id.new_membership_fee);
        etNewName=findViewById(R.id.new_membership_name);
        etNewNotSubmit=findViewById(R.id.new_membership_notsubmit);

        String newFee=(etNewFee).getText().toString();
        String newName=(etNewName).getText().toString();
        String newNotSubmit=(etNewNotSubmit).getText().toString();

        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("changeMembershipInfo");
        networkTask.setURL(urlChangeMembershipInfo);

        Map<String,String> params=new HashMap<>();
        params.put("fee",newFee);
        params.put("groupName",newName);
        params.put("notSubmit",newNotSubmit);
        params.put("payDuration",new_membership_payType+"");
        params.put("payDuration",new_membership_payTypeNum+"");

        networkTask.execute(params);
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
            if(TAG.equals("changeMembershipInfo")){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean("success");
                    if(success){
                        Intent intent=new Intent(ManageMembershipActivity.this, MembershipActivity.class);
                        setResult(159,intent);
                        finish();
                    }else{

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(TAG.equals("completeFee")){
                Log.d("completeFee",response);
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean("success");
                    if(success){
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
