package kr.hongik.mnms.membership;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.NetworkTask;
import kr.hongik.mnms.ProgressDialog;
import kr.hongik.mnms.R;
import kr.hongik.mnms.newprocesses.NewFeeActivity;
import kr.hongik.mnms.membership.ui.home.NewMembershipMemActivity;
import kr.hongik.mnms.membership.ui.manage.ManageMembershipActivity;
import kr.hongik.mnms.newprocesses.NewTransactionActivity;

public class MembershipActivity extends AppCompatActivity implements View.OnClickListener {

    private Member loginMember;
    private Account loginMemberAccount;
    private MembershipGroup membershipGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private ViewPager vpMembership;
    private FloatingActionButton fabMembershipMain, fabMembershipMember, fabMembershipTrans, fabMembershipManage;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private ProgressDialog progressDialog;

    //variables
    public static int TAG_TRANS = 111, TAG_MEM = 123, TAG_MANAGE = 159;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        progressDialog=new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabMembershipMain = findViewById(R.id.fabMembershipMain);
        fabMembershipMember = findViewById(R.id.fabMembershipMember);
        fabMembershipTrans = findViewById(R.id.fabMembershipTrans);
        fabMembershipManage = findViewById(R.id.fabMembershipManage);
        fabMembershipMain.setOnClickListener(this);
        fabMembershipTrans.setOnClickListener(this);
        fabMembershipMember.setOnClickListener(this);
        fabMembershipManage.setOnClickListener(this);

        Intent intent = getIntent();
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        setTitle(membershipGroup.getGroupName());
        getMembershipGroupInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == TAG_MEM) {
            showToast("멤버 추가 완료");
        } else if (requestCode == TAG_TRANS) {
            showToast("내역 추가 완료");
        }else if(requestCode==TAG_MANAGE){
            getMembershipGroupInfo();
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fabMembershipMain:
                toggleFab();
                break;
            case R.id.fabMembershipTrans:
                toggleFab();
                if (membershipGroup.getPresident().equals(loginMember.getMemID())) {
                    //돈사용 or 회비납입 중 선택
                    selectTransType();
                } else {
                    //회비납입만 가능
                    intent = new Intent(MembershipActivity.this, NewFeeActivity.class);
                    intent.putExtra("loginMember", loginMember);
                    intent.putExtra("loginMemberAccount", loginMemberAccount);
                    intent.putExtra("membershipGroup", membershipGroup);

                    startActivity(intent);
                }

                break;
            case R.id.fabMembershipMember:
                toggleFab();
                intent = new Intent(MembershipActivity.this, NewMembershipMemActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("membershipGroup", membershipGroup);
                intent.putExtra("memberArrayList", memberArrayList);

                startActivityForResult(intent, TAG_MEM);
                break;
            case R.id.fabMembershipManage:
                toggleFab();
                intent = new Intent(MembershipActivity.this, ManageMembershipActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("membershipGroup", membershipGroup);
                intent.putExtra("memberArrayList", memberArrayList);

                startActivityForResult(intent, TAG_MANAGE);
                break;
        }
    }

    private void selectTransType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);

        final String[] items = {"공금사용", "회비내기"};
        final Integer[] selected = {0};

        builder.setTitle("추가 하실 것은?");

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                selected[0] = pos;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (selected[0] == 0) {
                    //돈사용
                    Intent intent = new Intent(MembershipActivity.this, NewTransactionActivity.class);
                    intent.putExtra("loginMember", loginMember);
                    intent.putExtra("loginMemberAccount", loginMemberAccount);
                    intent.putExtra("membershipGroup", membershipGroup);
                    intent.putExtra("mainActivity", "membership");

                    startActivityForResult(intent, TAG_TRANS);

                } else if (selected[0] == 1) {
                    //회비납입
                    Intent intent = new Intent(MembershipActivity.this, NewFeeActivity.class);
                    intent.putExtra("loginMember", loginMember);
                    intent.putExtra("loginMemberAccount", loginMemberAccount);
                    intent.putExtra("membershipGroup", membershipGroup);
                    startActivity(intent);
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        alertDialog.show();
    }

    private void getMembershipGroupInfo() {
        String urlMembershipGroup = "http://" + loginMember.getIp() + "/membership/info";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlMembershipGroup);
        networkTask.setTAG("membershipGroup");

        Map<String, String> params = new HashMap<>();
        params.put("GID", membershipGroup.getGID() + "");

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void toggleFab() {
        if (isFabOpen) {
            fabMembershipMember.startAnimation(fab_close);
            fabMembershipTrans.startAnimation(fab_close);
            fabMembershipTrans.setClickable(false);
            fabMembershipMember.setClickable(false);
            if (membershipGroup.getPresident().equals(loginMember.getMemID())) {
                fabMembershipManage.startAnimation(fab_close);
                fabMembershipManage.setClickable(false);
            }
            isFabOpen = false;
        } else {
            fabMembershipMember.startAnimation(fab_open);
            fabMembershipTrans.startAnimation(fab_open);
            fabMembershipTrans.setClickable(true);
            fabMembershipMember.setClickable(true);
            if (membershipGroup.getPresident().equals(loginMember.getMemID())) {
                fabMembershipManage.startAnimation(fab_open);
                fabMembershipManage.setClickable(true);
            }
            isFabOpen = true;
        }
    }

    private void membershipGroupProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            membershipGroup = new MembershipGroup();
            membershipGroup.setMID(jsonObject.getInt("MID"));
            membershipGroup.setPresident(jsonObject.getString("president"));
            membershipGroup.setPayDay(jsonObject.getString("payDay"));
            membershipGroup.setFee(jsonObject.getInt("fee"));
            membershipGroup.setNotSubmit(jsonObject.getInt("notSubmit"));
            membershipGroup.setGID(jsonObject.getInt("GID"));
            membershipGroup.setAccountNum(jsonObject.getString("accountNum"));
            membershipGroup.setGroupName(jsonObject.getString("groupName"));
            membershipGroup.setPayDuration(jsonObject.getString("payDuration"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showMembershipMem();
    }

    private void showMembershipMem() {
        String urlShowMembershipMem = "http://" + loginMember.getIp() + "/membership/member";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("showMembershipMem");
        networkTask.setURL(urlShowMembershipMem);

        Map<String, String> params = new HashMap<>();
        params.put("GID", membershipGroup.getGID() + "");

        networkTask.execute(params);
    }

    private void showMemberProcess(String response) {
        String presidentName = "a";
        memberArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int membershipMemberSize = Integer.parseInt(jsonObject.getString("membershipMemberSize"));

            for (int i = 0; i < membershipMemberSize; i++) {
                String friendId = jsonObject.getString("memID" + i);
                String friendName = jsonObject.getString("memName" + i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);
                memberArrayList.add(member);
                if (membershipGroup.getPresident().equals(friendId)) {
                    presidentName = friendName;
                }
            }

            Comparator<Member> noAsc = new Comparator<Member>() {
                @Override
                public int compare(Member item1, Member item2) {
                    if (item1.getMemID().equals(membershipGroup.getPresident())) {
                        return -1;
                    }
                    return item1.getMemName().compareTo(item2.getMemName());
                }
            };
            Collections.sort(memberArrayList, noAsc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView tvMembershipPayDay, tvMembershipPresident, tvMembershipAccountNum;
        tvMembershipPayDay = findViewById(R.id.tvMembershipPayDay);
        tvMembershipPresident = findViewById(R.id.tvMembershipPresident);
        tvMembershipAccountNum = findViewById(R.id.tvMembershipAccountNum);

        tvMembershipPayDay.setText("회비 마감일:" + membershipGroup.getPayDay());
        tvMembershipPresident.setText("회장: " + presidentName);
        tvMembershipAccountNum.setText("그룹계좌번호: " + membershipGroup.getAccountNum());

        Bundle bundle = new Bundle();
        bundle.putSerializable("membershipGroup", membershipGroup);
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);
        bundle.putSerializable("memberArrayList", memberArrayList);

        vpMembership = findViewById(R.id.vpMembership);
        MembershipPagerAdapter adapter = new MembershipPagerAdapter(getSupportFragmentManager(), bundle);
        vpMembership.setAdapter(adapter);

        TabLayout tvMembership = findViewById(R.id.tlMembership);
        tvMembership.setupWithViewPager(vpMembership);
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
            //Log.d(TAG, response);
            if (TAG.equals("membershipGroup")) {
                membershipGroupProcess(response);
            } else if (TAG.equals("showMembershipMem")) {
                showMemberProcess(response);
            }
        }
    }
}