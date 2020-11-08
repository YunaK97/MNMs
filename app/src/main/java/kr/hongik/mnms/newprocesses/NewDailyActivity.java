package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

public class NewDailyActivity extends AppCompatActivity {

    private Member loginMember;

    //layouts
    private MemberAdapter memberAdapter;
    private ArrayList<Member> arrayList;
    private RecyclerView rvDailySelectedFriend;
    private Button btnNewDaily;

    //variable
    private ArrayList<Member> selectedMember;
    private ArrayList<String> groupName;
    private String TAG_SUCCESS = "success";
    private String etDailyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily);

        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");

        //친구 가져와서 출력
        showFriend();
        //기존 그룹 이름 가져오기
        groupNameList();

        //membership 생성 버튼 클릭

        btnNewDaily = findViewById(R.id.btnNewDaily);
        btnNewDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDaily();
            }
        });
    }

    private void NewDaily() {
        selectedMember = new ArrayList<>();
        etDailyName = ((TextView) findViewById(R.id.etDailyName)).getText().toString();
        if (TextUtils.isEmpty(etDailyName)) {
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        } else {
            boolean overlap = true;
            for (String s : groupName) {
                if (s.equals(etDailyName)) {
                    overlap = false;
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

                //새 데일리 생성
                //그룹이름전송, 가입하는 멤버들전송
                //그룹생성 성공여부를 받아야함
                String urlNewDaily = "http://" + loginMember.getIp() + "/daily/new";

                NetworkTask networkTask = new NetworkTask();
                networkTask.setURL(urlNewDaily);
                networkTask.setTAG("newDaily");

                Map<String, String> params = new HashMap<>();
                params.put("dailyName", etDailyName);
                params.put("memID",loginMember.getMemID());
                params.put("memberSize",selectedMember.size()+"");
                for (int i = 0; i < selectedMember.size(); i++) {
                    params.put("memID"+i, selectedMember.get(i).getMemID());
                }

                networkTask.execute(params);
            }
        }
    }


    private void groupNameList() {
        //그룹이름은 중복을 허용하지않음
        //memID를 보내면
        //멤버가 가입한 그룹들의 이름을 받아옴

        btnNewDaily.setClickable(false);

        String urlGroupNameList = "http://" + loginMember.getIp() + "/member/dailyGroupList";

        groupName = new ArrayList<>();

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlGroupNameList);
        networkTask.setTAG("groupNameList");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);
    }


    private void showFriend() {
        //데일리 생성시 친구일경우만 초대가능
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

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    private void newDailyProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                finish();
            } else {
                showToast("daily생성 실패!");
                btnNewDaily.setClickable(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showFriendProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int showFriendSize = Integer.parseInt(jsonObject.getString("showFriendSize"));

            rvDailySelectedFriend = findViewById(R.id.rvDailySelectedFriend);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NewDailyActivity.this, LinearLayoutManager.VERTICAL, false);
            rvDailySelectedFriend.setLayoutManager(layoutManager);
            memberAdapter = new MemberAdapter();

            for (int i = 0; i < showFriendSize; i++) {
                String friendId = jsonObject.getString("memID"+i);
                String friendName = jsonObject.getString("memName"+i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);
                memberAdapter.addItem(member);
            }

            rvDailySelectedFriend.setAdapter(memberAdapter);
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

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
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
            if (TAG.equals("newDaily")) {
                newDailyProcess(response);
            } else if (TAG.equals("groupNameList")) {
                dailyGroupNameProcess(response);
            } else if (TAG.equals("showFriend")) {
                showFriendProcess(response);
            }

        }
    }
}
