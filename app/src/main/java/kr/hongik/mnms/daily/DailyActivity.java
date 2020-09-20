package kr.hongik.mnms.daily;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.ui.home.DailyQRActivity;
import kr.hongik.mnms.daily.ui.home.NewDailyMemActivity;
import kr.hongik.mnms.newprocesses.NewTransactionActivity;

public class DailyActivity extends AppCompatActivity implements View.OnClickListener {

    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private ViewPager viewPager;
    private FloatingActionButton fab_daily_main, fab_daily_member, fab_daily_trans;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    //variables
    private int TAG_NEW_MEM=159,TAG_NEW_TRANS=160,TAG_SUCCESS=111;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_trans_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.daily_QRsend){
            Intent intent=new Intent(DailyActivity.this, DailyQRActivity.class);
            intent.putExtra("loginMember", loginMember);
            intent.putExtra("loginMemberAccount", loginMemberAccount);
            intent.putExtra("dailiyGroup", dailyGroup);
            intent.putExtra("memberArrayList",memberArrayList);

            //돈얼마송금해야하는지 가능하면 담아서 보내기

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_daily_main = findViewById(R.id.fab_daily_main);
        fab_daily_member = findViewById(R.id.fab_daily_member);
        fab_daily_trans = findViewById(R.id.fab_daily_trans);
        fab_daily_main.setOnClickListener(this);
        fab_daily_trans.setOnClickListener(this);
        fab_daily_member.setOnClickListener(this);

        Intent intent = getIntent();
        dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        getDailyGroupInfo();

        Bundle bundle = new Bundle();
        bundle.putSerializable("dailyGroup", dailyGroup);
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);
        bundle.putSerializable("memberArrayList", memberArrayList);

        viewPager = findViewById(R.id.viewpager_daily);
        DailyPagerAdapter adapter = new DailyPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout_membership);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        if (requestCode == TAG_NEW_MEM) {
            if(resultCode==TAG_SUCCESS)
                showToast("멤버 추가 완료");
        } else if (requestCode == TAG_NEW_TRANS) {
            if(resultCode==TAG_SUCCESS)
                showToast("내역 추가 완료");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fab_daily_main:
                toggleFab();
                break;
            case R.id.fab_daily_trans:
                toggleFab();
                intent = new Intent(DailyActivity.this, NewTransactionActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("loginMemberAccount", loginMemberAccount);
                intent.putExtra("dailiyGroup", dailyGroup);
                intent.putExtra("mainActivity","daily");

                startActivityForResult(intent, TAG_NEW_TRANS);
                break;
            case R.id.fab_daily_member:
                toggleFab();
                intent = new Intent(DailyActivity.this, NewDailyMemActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("membershipGroup", dailyGroup);
                intent.putExtra("memberArrayList", memberArrayList);

                startActivityForResult(intent, TAG_NEW_MEM);
                break;
        }
    }

    private void getDailyGroupInfo() {
        String urlDailyGroup = "http://" + loginMember.getIp() + "/daily/dutch";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDailyGroup);
        networkTask.setTAG("dailyGroup");

        Map<String, String> params = new HashMap<>();
        params.put("MID", dailyGroup.getGID()+"");

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void toggleFab() {
        if (isFabOpen) {
            fab_daily_member.startAnimation(fab_close);
            fab_daily_trans.startAnimation(fab_close);
            fab_daily_trans.setClickable(false);
            fab_daily_member.setClickable(false);
            //멤버 추가 버튼
//            if (dailyGroup.getPresident().equals(loginMember.getMemName())) {
//                fab_membership_manage.startAnimation(fab_close);
//                fab_membership_manage.setClickable(false);
//            }
            isFabOpen = false;
        } else {
            fab_daily_member.startAnimation(fab_open);
            fab_daily_trans.startAnimation(fab_open);
            fab_daily_trans.setClickable(true);
            fab_daily_member.setClickable(true);
            //멤버추가버튼
//            if (dailyGroup.getPresident().equals(loginMember.getMemName())) {
//                fab_membership_manage.startAnimation(fab_open);
//                fab_membership_manage.setClickable(true);
//            }
            isFabOpen = true;
        }
    }

    private void dailyGroupProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            dailyGroup = new DailyGroup();
            dailyGroup.setDID(Integer.parseInt(jsonObject.getString("DID")));
            dailyGroup.setGID(Integer.parseInt(jsonObject.getString("GID")));
            dailyGroup.setGroupName(jsonObject.getString("groupName"));

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
            if (TAG.equals("dailyGroup")) {
                dailyGroupProcess(response);
            } else if (TAG.equals("showMem")) {
                showMemberProcess(response);
            }
        }
    }
}