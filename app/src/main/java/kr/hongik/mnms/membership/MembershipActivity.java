package kr.hongik.mnms.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.membership.ui.home.NewMembershipMemActivity;
import kr.hongik.mnms.membership.ui.manage.ManageMembershipActivity;
import kr.hongik.mnms.newprocesses.NewTransactionActivity;

public class MembershipActivity extends AppCompatActivity implements View.OnClickListener {

    private Member loginMember;
    private Account loginMemberAccount;
    private MembershipGroup membershipGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private ViewPager viewPager;
    private FloatingActionButton fab_membership_main, fab_membership_member, fab_membership_trans, fab_membership_manage;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    //variables
    public static int TAG_TRANS = 111, TAG_MEM = 123, TAG_MANAGE = 159;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_membership_main = findViewById(R.id.fab_membership_main);
        fab_membership_member = findViewById(R.id.fab_membership_member);
        fab_membership_trans = findViewById(R.id.fab_membership_trans);
        fab_membership_manage = findViewById(R.id.fab_membership_manage);
        fab_membership_main.setOnClickListener(this);
        fab_membership_trans.setOnClickListener(this);
        fab_membership_member.setOnClickListener(this);
        fab_membership_manage.setOnClickListener(this);

        Intent intent = getIntent();
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        getMembershipGroupInfo();

        Bundle bundle = new Bundle();
        bundle.putSerializable("membershipGroup", membershipGroup);
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);
        bundle.putSerializable("memberArrayList", memberArrayList);

        viewPager = findViewById(R.id.viewpager_membership);
        MembershipPagerAdapter adapter = new MembershipPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout_membership);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;
        if (requestCode == TAG_MEM) {
            showToast("멤버 추가 완료");
        } else if (requestCode == TAG_TRANS) {
            showToast("내역 추가 완료");
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fab_membership_main:
                toggleFab();
                break;
            case R.id.fab_membership_trans:
                toggleFab();
                intent = new Intent(MembershipActivity.this, NewTransactionActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("loginMemberAccouont", loginMemberAccount);
                intent.putExtra("membershipGroup", membershipGroup);
                intent.putExtra("mainActivity", "membership");

                startActivityForResult(intent, TAG_TRANS);
                break;
            case R.id.fab_membership_member:
                toggleFab();
                intent = new Intent(MembershipActivity.this, NewMembershipMemActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("membershipGroup", membershipGroup);
                intent.putExtra("memberArrayList", memberArrayList);

                startActivityForResult(intent, TAG_MEM);
                break;
            case R.id.fab_membership_manage:
                toggleFab();
                intent = new Intent(MembershipActivity.this, ManageMembershipActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("membershipGroup", membershipGroup);
                intent.putExtra("memberArrayList", memberArrayList);

                startActivityForResult(intent, TAG_MANAGE);
                break;
        }
    }

    private void getMembershipGroupInfo() {
        String urlMembershipGroup = "http://" + loginMember.getIp() + "/fee";
        urlMembershipGroup = "http://jennyk97.dothome.co.kr/MembershipGroup.php";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlMembershipGroup);
        networkTask.setTAG("membershipGroup");

        Map<String, String> params = new HashMap<>();
        params.put("MID", membershipGroup.getGID() + "");

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void toggleFab() {
        if (isFabOpen) {
            fab_membership_member.startAnimation(fab_close);
            fab_membership_trans.startAnimation(fab_close);
            fab_membership_trans.setClickable(false);
            fab_membership_member.setClickable(false);
            if (membershipGroup.getPresident().equals(loginMember.getMemID())) {
                fab_membership_manage.startAnimation(fab_close);
                fab_membership_manage.setClickable(false);
            }
            isFabOpen = false;
        } else {
            fab_membership_member.startAnimation(fab_open);
            fab_membership_trans.startAnimation(fab_open);
            fab_membership_trans.setClickable(true);
            fab_membership_member.setClickable(true);
            if (membershipGroup.getPresident().equals(loginMember.getMemID())) {
                fab_membership_manage.startAnimation(fab_open);
                fab_membership_manage.setClickable(true);
            }
            isFabOpen = true;
        }
    }

    private void membershipGroupProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            membershipGroup = new MembershipGroup();
            membershipGroup.setMID(Integer.parseInt(jsonObject.getString("MID")));
            membershipGroup.setPresident(jsonObject.getString("president"));
            membershipGroup.setPayDay(Integer.parseInt(jsonObject.getString("payDay")));
            membershipGroup.setFee(jsonObject.getInt("fee"));
            membershipGroup.setNotSubmit(jsonObject.getInt("notSubmit"));
            membershipGroup.setGID(Integer.parseInt(jsonObject.getString("GID")));
            membershipGroup.setAccountNum("12-123456789");
            membershipGroup.setGroupName("groupName");
//                membershipGroup.setAccountNum(jsonObject.getString("accountNum"));
//                membershipGroup.setGroupName(jsonObject.getString("groupName"));
//                membershipGroup.setTime(jsonObject.getString("time"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMemberProcess(String response) {
        memberArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                return;
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String friendId = item.getString("memID");
                String friendName = item.getString("memName");

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);
                memberArrayList.add(member);
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
            if (TAG.equals("membershipGroup")) {
                membershipGroupProcess(response);
            } else if (TAG.equals("showMem")) {
                showMemberProcess(response);
            }
        }
    }
}