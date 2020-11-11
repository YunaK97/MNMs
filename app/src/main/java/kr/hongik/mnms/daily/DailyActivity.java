package kr.hongik.mnms.daily;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.ui.home.DailySendActivity;
import kr.hongik.mnms.newprocesses.NewTransactionActivity;

public class DailyActivity extends AppCompatActivity implements View.OnClickListener {

    private Member loginMember;
    private Account loginMemberAccount;
    private DailyGroup dailyGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private ViewPager viewpagerDaily;
    private FloatingActionButton fabDailyMain, fabDailyTrans;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;

    //variables
    public static int TAG_NEW_MEM=159,TAG_NEW_TRANS=160,TAG_SUCCESS=111;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dailyqr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.daily_result){
            floatCalculateDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabDailyMain = findViewById(R.id.fabDailyMain);
        fabDailyTrans = findViewById(R.id.fabDailyTrans);
        fabDailyMain.setOnClickListener(this);
        fabDailyTrans.setOnClickListener(this);

        Intent intent = getIntent();
        dailyGroup = (DailyGroup) intent.getSerializableExtra("dailyGroup");
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        setTitle(dailyGroup.getGroupName());

        getDailyGroupInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        if (requestCode == TAG_NEW_MEM) {
            if(resultCode==TAG_SUCCESS)
                showToast("멤버 추가 완료 - 멤버십 업데이트 필요");
        } else if (requestCode == TAG_NEW_TRANS) {
            if(resultCode==TAG_SUCCESS){
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fabDailyMain:
                toggleFab();
                break;
            case R.id.fabDailyTrans:
                toggleFab();
                intent = new Intent(DailyActivity.this, NewTransactionActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("loginMemberAccount", loginMemberAccount);
                intent.putExtra("dailyGroup", dailyGroup);
                intent.putExtra("mainActivity","daily");

                startActivityForResult(intent, TAG_NEW_TRANS);
                break;
        }
    }

    private void floatCalculateDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(DailyActivity.this,R.style.CustomDialog);
        builder.setTitle("정산하시겠습니까?");
        builder.setMessage("다시 되돌릴수 없습니다");
        builder.setPositiveButton("정산", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DailyActivity.this, DailySendActivity.class);
                intent.putExtra("loginMember", loginMember);
                intent.putExtra("loginMemberAccount", loginMemberAccount);
                intent.putExtra("dailyGroup", dailyGroup);
                intent.putExtra("memberArrayList", memberArrayList);

                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void getDailyGroupInfo() {
        //데일리 그룹 정보를 불러오기 위함
        //데일리 그룹의 GID,DID를 전달함
        //성공 시, 데일리 그룹과 관련된 모든 정보를 불러옴
        String urlDailyGroup = "http://" + loginMember.getIp() + "/daily/info";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDailyGroup);
        networkTask.setTAG("dailyGroup");

        Map<String, String> params = new HashMap<>();
        params.put("GID", dailyGroup.getGID()+"");

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void toggleFab() {
        if (isFabOpen) {
            fabDailyTrans.startAnimation(fab_close);
            fabDailyTrans.setClickable(false);
            isFabOpen = false;
        } else {
            fabDailyTrans.startAnimation(fab_open);
            fabDailyTrans.setClickable(true);

            isFabOpen = true;
        }
    }

    private void dailyGroupProcess(String response) {
        Log.d("dailyGroup",response);
        try {
            JSONObject jsonObject = new JSONObject(response);

            dailyGroup = new DailyGroup();
            dailyGroup.setDID(jsonObject.getInt("DID"));
            dailyGroup.setGID(jsonObject.getInt("GID"));
            dailyGroup.setGroupName(jsonObject.getString("groupName"));
            showMember();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showMember(){
        //해당 데일리 그룹에 가입된 멤버들을 모두 가져옴
        //GID,DID를 보냄
        //응답으로 멤버들의 id,name을 받아와야함
        String urlDailyGroup = "http://" + loginMember.getIp() + "/daily/member";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDailyGroup);
        networkTask.setTAG("showMem");

        Map<String, String> params = new HashMap<>();
        params.put("GID", dailyGroup.getGID()+"");
        params.put("DID", dailyGroup.getDID()+"");

        networkTask.execute(params);
    }
    private void showMemberProcess(String response) {
        memberArrayList=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(response);
            int dailyMemberSize=jsonObject.getInt("dailyMemberSize");

            for (int i = 0; i < dailyMemberSize; i++) {
                String friendId = jsonObject.getString("memID"+i);
                String friendName = jsonObject.getString("memName"+i);

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

        Bundle bundle = new Bundle();
        bundle.putSerializable("dailyGroup", dailyGroup);
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);
        bundle.putSerializable("memberArrayList", memberArrayList);

        viewpagerDaily = findViewById(R.id.viewpagerDaily);
        DailyPagerAdapter adapter = new DailyPagerAdapter(getSupportFragmentManager(), bundle);
        viewpagerDaily.setAdapter(adapter);

        TabLayout tabLayoutDaily = findViewById(R.id.tabLayoutDaily);
        tabLayoutDaily.setupWithViewPager(viewpagerDaily);
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