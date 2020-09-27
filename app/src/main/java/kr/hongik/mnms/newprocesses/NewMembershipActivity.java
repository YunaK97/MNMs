package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private RecyclerView friend_list;
    private MemberAdapter memberAdapter;

    //variables
    private String TAG_SUCCESS = "success";
    private ArrayList<String> groupName;
    private ArrayList<Member> selectedMember;
    private String membership_name, membership_money, membership_notsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership);

        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");

        //친구 가져와서 출력
        showFriend();

        groupNameList();

        //membership 생성 버튼 클릭
        Button btn_new_membership = findViewById(R.id.btn_new_membership);
        btn_new_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMembership();
            }
        });
    }

    protected void NewMembership() {
        selectedMember = new ArrayList<>();
        membership_name = ((TextView) findViewById(R.id.membership_name)).getText().toString();
        membership_money = ((TextView) findViewById(R.id.membership_money)).getText().toString();
        membership_notsubmit = ((TextView) findViewById(R.id.membership_notsubmit)).getText().toString();
        String membershipAccountNum;
        int div1=((int) (Math.random() * 10))%5,div2=((int) (Math.random() * 10))%5,div3=((int) (Math.random() * 10))%5;
        membershipAccountNum= (int) (Math.random() * div1) +"-"+ (int) (Math.random() * div2) +"-"+ (int) (Math.random() * div3);
        showToast("계좌 : "+membershipAccountNum);
        if (membership_money == null || membership_name == null || membership_notsubmit == null) {
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        } else {
            boolean overlap = true;
            for (String s : groupName) {
                if (s.equals(membership_name)) {
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
                String urlNewMembership = "http://" + loginMember.getIp() + "/newMembership";

                NetworkTask networkTask = new NetworkTask();
                networkTask.setURL(urlNewMembership);
                networkTask.setTAG("newMembership");

                Map<String, String> params = new HashMap<>();
                params.put("president", loginMember.getMemName());
                params.put("membershipName", membership_name);
                params.put("membershipMoney", membership_money);
                params.put("membershipNotSubmit", membership_notsubmit);
                params.put("membershipAccountNum",membershipAccountNum);
                params.put("membersSize",selectedMember.size()+"");

                try {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < selectedMember.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("memID"+i, selectedMember.get(i).getMemID());
                        jsonArray.put(jsonObject);
                    }
                    params.put("members", jsonArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                networkTask.execute(params);
            }
        }
    }

    private void groupNameList() {
        //그룹이름은 중복을 허용하지않음
        //memID를 보내면
        //멤버가 가입한 그룹들의 이름을 받아옴

        String urlGroupNameList = "http://" + loginMember.getIp() + "/memberGroupInfo";

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

            friend_list = findViewById(R.id.membership_select_friend);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NewMembershipActivity.this, LinearLayoutManager.VERTICAL, false);
            friend_list.setLayoutManager(layoutManager);
            memberAdapter = new MemberAdapter();

            for (int i = 0; i < showFriendSize; i++) {
                String friendId = jsonObject.getString("memID"+i);
                String friendName = jsonObject.getString("memName"+i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);
                memberAdapter.addItem(member);
            }

            friend_list.setAdapter(memberAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dailyGroupNameProcess(String response) {
        try {
            JSONObject jsonObject=new JSONObject(response);
            int dailyGroupSize=jsonObject.getInt("dailyGroupSize");
            for (int i = 0; i < dailyGroupSize; i++) {
                String groupname = jsonObject.getString("groupName"+i);
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
            if (TAG.equals("newMembership")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        showToast("생성 성공!");
                        finish();
                    } else {
                        showToast("membership 실패!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("groupNameList")) {
               dailyGroupNameProcess(response);
            } else if (TAG.equals("showFriend")) {
                showFriendProcess(response);
            }

        }
    }
}
