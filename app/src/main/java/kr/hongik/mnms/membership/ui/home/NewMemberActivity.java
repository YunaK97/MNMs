package kr.hongik.mnms.membership.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.membership.MembershipActivity;
import kr.hongik.mnms.membership.MembershipGroup;

public class NewMemberActivity extends AppCompatActivity {
    private Member loginMember;
    private MembershipGroup membershipGroup;
    private ArrayList<String> memberArrayList;

    //layouts
    private TextView memberIdText, memberNameText;
    private ImageButton btnMemberSearch;
    private Button btnAddMember;
    private LinearLayout memberLayout;

    //URLs
    private String ip = "203.249.75.14";

    //variables
    private String TAG_SUCCESS = "success";
    private String memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        //intent 받아오기
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        memberArrayList=intent.getStringArrayListExtra("memberArrayList");

        ip = intent.getStringExtra("ip");

        //findViewById
        btnMemberSearch = findViewById(R.id.btn_member_search);
        btnAddMember = findViewById(R.id.btn_addMember);
        memberLayout = findViewById(R.id.member_layout);
        memberIdText=findViewById(R.id.member_id_text);
        memberNameText=findViewById(R.id.member_name_text);


        btnMemberSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                memberId = ((TextView) findViewById(R.id.member_id)).getText().toString();
                //기존 멤버 사람들과 같으면 불가능
                boolean valid=true;
                for(int i=0;i<memberArrayList.size();i++){
                    if(memberId.equals(memberArrayList.get(i))){
                        showToast("불가능한 id 입니다.");
                        valid=false;
                        break;
                    }
                }
                if (valid) {
                    searchFriend(memberId);
                }
            }
        });

        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        String urlNewFriendAdd = "http://" + ip + "/newMemberAdd";
        urlNewFriendAdd = "http://jennyk97.dothome.co.kr/NewMemberAdd.php";

        //내가 상대방에게 친구추가 요청
        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewFriendAdd);
        networkTask.setTAG("newMemberAdd");

        Map<String, String> params = new HashMap<>();
        params.put("GID", membershipGroup.getGID());
        params.put("memberID", memberId);

        networkTask.execute(params);
    }

    private void searchFriend(String member_id) {
        String urlNewFriend = "http://" + ip + "/newFriend";
        urlNewFriend = "http://jennyk97.dothome.co.kr/NewFriend.php";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewFriend);
        networkTask.setTAG("newMember");

        Map<String, String> params = new HashMap<>();
        params.put("memID", member_id);

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
            if (TAG.equals("newMemberAdd")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        showToast("멤버 추가 완료");
                        Intent intent=new Intent(NewMemberActivity.this, MembershipActivity.class);
                        setResult(123,intent);
                        finish();
                    } else {
                        showToast("멤버 추가 실패ㅠ");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("newMember")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        String member_name = jsonObject.getString("memName");
                        String member_id=jsonObject.getString("memID");

                        memberNameText.setText(member_name);
                        memberIdText.setText(member_id);

                        memberLayout.setVisibility(View.VISIBLE);
                    } else {
                        showToast("ID 검색 실패");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}