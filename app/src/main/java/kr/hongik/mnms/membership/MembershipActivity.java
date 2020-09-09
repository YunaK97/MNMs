package kr.hongik.mnms.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.membership.ui.home.PlusTransactionActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MembershipActivity extends AppCompatActivity {

    Member loginMember;
    Account loginMemberAccount;
    MembershipGroup membershipGroup;

    //Layouts
    //private FloatingActionButton plus_memtransaction;
    ViewPager viewPager;

    //URLs
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        FloatingActionButton plus_memtransaction = findViewById(R.id.plus_memtransaction);
        plus_memtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MembershipActivity.this, PlusTransactionActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("loginMemberAccouont", loginMemberAccount);
                intent.putExtra("membershipGroup", membershipGroup);

                startActivityForResult(intent, 111);
            }
        });

        Intent intent = getIntent();
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");
        ip = intent.getStringExtra("ip");

        getMembershipGroupInfo();

        Bundle bundle = new Bundle();
        bundle.putSerializable("membershipGroup", membershipGroup);
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);
        bundle.putString("ip", ip);

        viewPager = findViewById(R.id.viewpager);
        MembershipPagerAdapter adapter = new MembershipPagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void getMembershipGroupInfo() {
        String urlMembershipGroup = "http://" + ip + "/fee";
        urlMembershipGroup = "http://jennyk97.dothome.co.kr/MembershipGroup.php";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlMembershipGroup);
        networkTask.setTAG("membershipGroup");

        Map<String, String> params = new HashMap<>();
        params.put("MID", membershipGroup.getGID());

        networkTask.execute(params);
    }

    private void membershipGroupProcess(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);

            membershipGroup = new MembershipGroup();
            membershipGroup.setMID(jsonObject.getString("MID"));
            membershipGroup.setPresident(jsonObject.getString("president"));
            membershipGroup.setPayDay(jsonObject.getString("payDay"));
            membershipGroup.setMemberMoney(jsonObject.getInt("memberMoney"));
            membershipGroup.setNotSubmit(jsonObject.getInt("notSubmit"));
            membershipGroup.setGID(jsonObject.getString("GID"));
            membershipGroup.setAccountNum("12-123456789");
            membershipGroup.setGroupName("groupName");
            membershipGroup.setTime("2020-07-07");
//                membershipGroup.setAccountNum(jsonObject.getString("accountNum"));
//                membershipGroup.setGroupName(jsonObject.getString("groupName"));
//                membershipGroup.setTime(jsonObject.getString("time"));

        } catch (JSONException e) {
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
            if(TAG.equals("membershipGroup")){
                membershipGroupProcess(response);
            }
        }
    }
}