package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.MemberAdapter;
import kr.hongik.mnms.R;

public class NewMembershipActivity extends AppCompatActivity {
    private Member loginMember;

    //layouts
    private RecyclerView rvMembershipSelectFriend;
    private MemberAdapter memberAdapter;
    private TextView tvNewMembershipAccountNum;
    private Button btnNewMembership;

    //variables
    private String TAG_SUCCESS = "success";
    private ArrayList<String> groupName;
    private ArrayList<Member> selectedMember;
    private String etNewMembershipPW, etMembershipName, etMembershipMoney, etMembershipNotsubmit, membershipAccountNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership);

        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");

        tvNewMembershipAccountNum = findViewById(R.id.tvNewMembershipAccountNum);

        Random random=new Random();
        random.setSeed(System.currentTimeMillis());
        int div1 = random.nextInt(100)+100;
        int div2 = random.nextInt(100)+1000;
        int div3 = random.nextInt(100)+1000;
        membershipAccountNum = (int) (Math.random() * div1) + "-" + (int) (Math.random() * div2) + "-" + (int) (Math.random() * div3);
        tvNewMembershipAccountNum.setText(membershipAccountNum);

        //친구 가져와서 출력
        showFriend();

        groupNameList();

        //membership 생성 버튼 클릭
        btnNewMembership = findViewById(R.id.btnNewMembership);
        btnNewMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMembership();
            }
        });
    }

    protected void NewMembership() {
        selectedMember = new ArrayList<>();
        etMembershipName = ((TextView) findViewById(R.id.etMembershipName)).getText().toString();
        etMembershipMoney = ((TextView) findViewById(R.id.etMembershipMoney)).getText().toString();
        etMembershipNotsubmit = ((TextView) findViewById(R.id.etMembershipNotsubmit)).getText().toString();
        etNewMembershipPW = ((TextView) findViewById(R.id.etNewMembershipPW)).getText().toString();

        if (TextUtils.isEmpty(etMembershipMoney) || TextUtils.isEmpty(etMembershipName)|| TextUtils.isEmpty(etMembershipNotsubmit)) {
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        } else {
            if (etNewMembershipPW.length() != 4) {
                showToast("비밀번호는 4자리 입니다.");
            } else if(Integer.parseInt(etMembershipNotsubmit)<1 || Integer.parseInt(etMembershipNotsubmit)>365) {
                showToast("미납횟수는 1~365입니다.");
            }else if(Integer.parseInt(etMembershipMoney)<1 || Integer.parseInt(etMembershipMoney)>2100000000){
                showToast("불가능한 회비입니다.");
            }
            else
             {
                boolean overlap = true;
                for (String s : groupName) {
                    if (s.equals(etMembershipName)) {
                        overlap = false;
                        break;
                    }
                }
                if (!overlap) {
                    showToast("이미 존재하는 그룹이름입니다.");
                } else if (overlap) {
                    for (int i = 0; i < memberAdapter.getItemCount(); i++) {
                        if (memberAdapter.getItem(i).isChecked()) {
                            Member member = new Member();
                            member.setMemID((memberAdapter.getItem(i)).getMemID());
                            selectedMember.add(member);
                        }
                    }

                    //멤버십 생성할거임
                    //멤버십 관련정보 모든것과 가입할 멤버들 전송
                    //멤버십 생성 후 성공했는지 받아야함

                    btnNewMembership.setClickable(false);

                    String urlNewMembership = "http://" + loginMember.getIp() + "/membership/new";

                    NetworkTask networkTask = new NetworkTask();
                    networkTask.setURL(urlNewMembership);
                    networkTask.setTAG("newMembership");

                    Map<String, String> params = new HashMap<>();

                    params.put("memID", loginMember.getMemID());
                    params.put("membershipName", etMembershipName);
                    params.put("membershipMoney", etMembershipMoney);
                    params.put("membershipNotSubmit", etMembershipNotsubmit);
                    params.put("accountNum", membershipAccountNum);
                    params.put("memberSize", selectedMember.size() + "");
                    params.put("passWD",etNewMembershipPW);
                    for (int i = 0; i < selectedMember.size(); i++) {
                        params.put("memID" + i, selectedMember.get(i).getMemID());
                    }

                    networkTask.execute(params);
                }
            }
        }
    }

    private void groupNameList() {
        //그룹이름은 중복을 허용하지않음
        //memID를 보내면
        //멤버가 가입한 그룹들의 이름을 받아옴

        String urlGroupNameList = "http://" + loginMember.getIp() + "/member/membershipGroupList";

        groupName = new ArrayList<>();

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlGroupNameList);
        networkTask.setTAG("groupNameList");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);
    }

    protected void showFriend() {
        //멤버십 생성시 친구일경우만 초대가능
        //멤버의 아이디를 전송함
        //멤버의 친구들을 받아옴
        String urlShowFriend = "http://" + loginMember.getIp() + "/member/showFriend";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlShowFriend);
        networkTask.setTAG("showFriend");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);
    }

    private void showFriendProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int showFriendSize = Integer.parseInt(jsonObject.getString("showFriendSize"));

            rvMembershipSelectFriend = findViewById(R.id.rvMembershipSelectFriend);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NewMembershipActivity.this, LinearLayoutManager.VERTICAL, false);
            rvMembershipSelectFriend.setLayoutManager(layoutManager);
            memberAdapter = new MemberAdapter();

            for (int i = 0; i < showFriendSize; i++) {
                String friendId = jsonObject.getString("memID" + i);
                String friendName = jsonObject.getString("memName" + i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);
                memberAdapter.addItem(member);
            }

            rvMembershipSelectFriend.setAdapter(memberAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void membershipGroupNameProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int membershipGroupSize = Integer.parseInt(jsonObject.getString("membershipGroupSize"));
            if (membershipGroupSize==0){
                return;
            }
            for (int i = 0; i < membershipGroupSize; i++) {
                String groupname = jsonObject.getString("groupName" + i);
                groupName.add(groupname);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("오류 : " + e.toString());
        }
    }

    protected void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        String TAG;

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
            if (TAG.equals("newMembership")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        showToast("payday는 1일, payduration은 한달 입니다.");
                        finish();
                    } else {
                        showToast("membership 실패!");
                        btnNewMembership.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("groupNameList")) {
                membershipGroupNameProcess(response);
            } else if (TAG.equals("showFriend")) {
                showFriendProcess(response);
            }

        }
    }
}
