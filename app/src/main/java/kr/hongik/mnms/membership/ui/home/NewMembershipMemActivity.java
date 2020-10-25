package kr.hongik.mnms.membership.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.MemberAdapter;
import kr.hongik.mnms.R;
import kr.hongik.mnms.membership.MembershipActivity;
import kr.hongik.mnms.membership.MembershipGroup;

public class NewMembershipMemActivity extends AppCompatActivity {
    /*
     * 내 친구들 다부름
     * -> 멤버십에 가입된 애들은 제외
     *
     * 내가 여러명의 멤버들을 선택해서 전송함, GID
     * */
    private Member loginMember;
    private MembershipGroup membershipGroup;
    private ArrayList<Member> memberArrayList, friendArrayList;

    //layouts
    private MemberAdapter memberAdapter;
    private RecyclerView friend_list;
    private Button btn_newMembershipMem;

    //variables
    private String TAG_SUCCESS = "success";
    private String memberId;
    private ArrayList<Member> selectedFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership_mem);

        setTitle("친구초대");

        //intent 받아오기
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        memberArrayList = (ArrayList<Member>) intent.getSerializableExtra("memberArrayList");

        btn_newMembershipMem = findViewById(R.id.btn_newMembershipMem);
        btn_newMembershipMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAddMem();
            }
        });

        showFriend();
    }

    private void showFriend() {
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
            if (showFriendSize == 0) return;

            friend_list = findViewById(R.id.RV_newMembershipMem);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NewMembershipMemActivity.this, LinearLayoutManager.VERTICAL, false);
            friend_list.setLayoutManager(layoutManager);
            memberAdapter = new MemberAdapter();
            friendArrayList = new ArrayList<>();

            for (int i = 0; i < showFriendSize; i++) {
                String friendId = jsonObject.getString("memID" + i);
                String friendName = jsonObject.getString("memName" + i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);

                boolean valid = true;
                for (int j = 0; j < memberArrayList.size(); j++) {
                    if (memberArrayList.get(j).getMemID().equals(friendId)) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    friendArrayList.add(member);
            }

            Comparator<Member> noAsc = new Comparator<Member>() {
                @Override
                public int compare(Member item1, Member item2) {
                    return item1.getMemName().compareTo(item2.getMemName());
                }
            };

            Collections.sort(friendArrayList, noAsc);

            memberAdapter.setItems(friendArrayList);
            friend_list.setAdapter(memberAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestAddMem() {
        String urlAddmembershipMem = "http://" + loginMember.getIp() + "/membership/add";

        // 수락or거절 결과 전송
        selectedFriend = new ArrayList<>();
        for (int i = 0; i < memberAdapter.getItemCount(); i++) {
            if (memberAdapter.getItem(i).isChecked()) {
                selectedFriend.add(memberAdapter.getItem(i));
            }
        }

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlAddmembershipMem);
        networkTask.setTAG("addMembershipMem");

        Map<String, String> params = new HashMap<>();
        params.put("GID",membershipGroup.getGID()+"");
        params.put("friendSize",selectedFriend.size()+"");
        for (int i = 0; i < selectedFriend.size(); i++) {
            params.put("memID"+i, selectedFriend.get(i).getMemID());
        }
        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
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
            if (TAG.equals("addMembershipMem")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        showToast("멤버 추가 완료");
                        Intent intent = new Intent(NewMembershipMemActivity.this, MembershipActivity.class);
                        setResult(MembershipActivity.TAG_MEM, intent);
                        finish();
                    } else {
                        showToast("멤버 추가 실패ㅠ");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("showFriend")) {
                showFriendProcess(response);
            }
        }
    }
}