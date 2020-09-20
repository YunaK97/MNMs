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

public class NewMembershipMemActivity extends AppCompatActivity {
    private Member loginMember;
    private MembershipGroup membershipGroup;
    private ArrayList<Member> memberArrayList;

    //layouts
    private TextView memberIdText, memberNameText;
    private ImageButton btnMemberSearch;
    private Button btnAddMember;
    private LinearLayout memberLayout;

    //variables
    private String TAG_SUCCESS = "success";
    private String memberId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_membership_mem);

        //intent 받아오기
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        membershipGroup = (MembershipGroup) intent.getSerializableExtra("membershipGroup");
        memberArrayList = (ArrayList<Member>) intent.getSerializableExtra("memberArrayList");

        //findViewById
        btnMemberSearch = findViewById(R.id.btn_newMembershipMemID);
        btnAddMember = findViewById(R.id.btn_addMembershipMem);
        memberLayout = findViewById(R.id.layout_newMembershipMemLayout);
        memberIdText = findViewById(R.id.tv_newMembershipMemID);
        memberNameText = findViewById(R.id.tv_newMembershipMemName);


        btnMemberSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                memberId = ((TextView) findViewById(R.id.membership_memID)).getText().toString();
                if (memberId==null || memberId.length()<4 || memberId.length()>20){
                    showToast("불가능한 ID 입니다");
                    return;
                }
                //기존 멤버 사람들과 같으면 불가능
                boolean valid = true;
                for (int i = 0; i < memberArrayList.size(); i++) {
                    if (memberId.equals(memberArrayList.get(i).getMemID())) {
                        showToast("이미 있는 멤버입니다.");
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    searchMember(memberId);
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
        //검색한 멤버를 멤버십에 가입시킬것임
        //멤버십의 GID, 가입할 멤버ID 전송함
        //멤버가 성공적으로 멤버십에 가입됐는지 여부를 받아야함
        String urlNewMembershipMem = "http://" + loginMember.getIp() + "/newMemberAdd";

        //멤버 추가
        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewMembershipMem);
        networkTask.setTAG("newMembershipMem");

        Map<String, String> params = new HashMap<>();
        params.put("GID", membershipGroup.getGID()+"");
        params.put("memberID", memberId);

        networkTask.execute(params);
    }

    private void searchMember(String memID) {
        //가입할 id를 검색, 존재하는 멤버인지 확인
        //id가 있는지 없는지 유무를 전달받아야함
        String urlSearchMember = "http://" + loginMember.getIp() + "/newFriend";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlSearchMember);
        networkTask.setTAG("searchMember");

        Map<String, String> params = new HashMap<>();
        params.put("memID", memID);

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
            if (TAG.equals("newMembershipMem")) {
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
            } else if (TAG.equals("searchMember")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        String member_name = jsonObject.getString("memName");
                        String member_id = jsonObject.getString("memID");

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