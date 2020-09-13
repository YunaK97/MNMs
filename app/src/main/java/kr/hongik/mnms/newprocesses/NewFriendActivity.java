package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class NewFriendActivity extends AppCompatActivity {
    private Member loginMember;

    //layouts
    private TextView friend_name_text, friend_id_text;
    private ImageButton id_search;
    private Button btn_addFriend, request_accept, request_reject;
    private LinearLayout linearLayout, request_friend_layout;
    private RecyclerView requestedRecyclerView;
    private MemberAdapter memberAdapter;

    //variables
    private String TAG_SUCCESS = "success";
    private String friend_id;
    private ArrayList<Member> selectedFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        //intent 받아오기
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");

        //findViewById
        id_search = findViewById(R.id.id_search);
        btn_addFriend = findViewById(R.id.btn_addFriend);
        linearLayout = findViewById(R.id.friend_layout);
        friend_name_text = findViewById(R.id.friend_name);
        friend_id_text = findViewById(R.id.friend_id);
        requestedRecyclerView = findViewById(R.id.request_friend);
        request_accept = findViewById(R.id.request_accept);
        request_reject = findViewById(R.id.request_reject);
        request_friend_layout = findViewById(R.id.request_friend_layout);

        showRequest();
        id_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                friend_id = ((TextView) findViewById(R.id.friend_search)).getText().toString();
                if (friend_id.equals(loginMember.getMemID())) {
                    showToast("불가능한 id 입니다.");
                } else {
                    request_friend_layout.setVisibility(View.GONE);
                    searchFriend(friend_id);
                }
            }
        });

        btn_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        String urlNewFriendAdd = "http://" + loginMember.getIp() + "/newFriendAdd";
        urlNewFriendAdd = "http://jennyk97.dothome.co.kr/NewFriendAdd.php";

        //내가 상대방에게 친구추가 요청
        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewFriendAdd);
        networkTask.setTAG("newFriendAdd");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("friendID", friend_id);

        networkTask.execute(params);
    }

    private void showRequest() {
        String urlRequestedFriend = "http://" + loginMember.getIp() + "/requestedFriend";
        urlRequestedFriend = "http://jennyk97.dothome.co.kr/RequestedFriend.php";

        //나에게 들어온 요청 출력
        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlRequestedFriend);
        networkTask.setTAG("requestedFriend");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);

        //체크박스 -> 수락버튼 -> accept
        request_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFriend("friend");
            }
        });


        //체크박스 -> 거절버튼 -> reject
        request_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestFriend("reject");
            }
        });
    }

    private void deleteFriend(final String delMemberId) {
        String urlDeleteFriend = "http://" + loginMember.getIp() + "/deleteFriend";
        urlDeleteFriend = "http://jennyk97.dothome.co.kr/DeleteFriend.php";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDeleteFriend);
        networkTask.setTAG("deleteFriend");

        Map<String, String> params = new HashMap<>();
        params.put("friendID", delMemberId);
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);
    }

    private void requestFriend(final String TAG_RESULT) {
        String urlRequestedResult = "http://" + loginMember.getIp() + "/requestedResult";
        urlRequestedResult = "http://jennyk97.dothome.co.kr/RequestedResult.php";

        // 수락or거절 결과 전송
        selectedFriend = new ArrayList<>();
        for (int i = 0; i < memberAdapter.getItemCount(); i++) {
            if (memberAdapter.getItem(i).isChecked()) {
                selectedFriend.add(memberAdapter.getItem(i));
            }
        }

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlRequestedResult);
        networkTask.setTAG("requestedResult");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("TAG", TAG_RESULT);
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < selectedFriend.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("memID", selectedFriend.get(i).getMemID());
                jsonObject.put("memName", selectedFriend.get(i).getMemName());
                jsonArray.put(jsonObject);
            }
            if (TAG_RESULT.equals("reject")) {
                params.put("reject", jsonArray.toString());
            } else {
                params.put("friend", jsonArray.toString());
            }
            networkTask.execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchFriend(final String friend_id) {
        String urlNewFriend = "http://" + loginMember.getIp() + "/newFriend";
        urlNewFriend = "http://jennyk97.dothome.co.kr/NewFriend.php";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewFriend);
        networkTask.setTAG("newFriend");

        Map<String, String> params = new HashMap<>();
        params.put("memID", friend_id);

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
            if (TAG.equals("newFriendAdd")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean dup = jsonObject.getBoolean("dup");
                    if (dup) {
                        showToast("이미 친구사이입니다.");
                    } else {
                        boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                        if (success) {
                            showToast("친구추가 신청 완료");
                            finish();
                        } else {
                            showToast("친구 추가 실패ㅠ");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("requestedFriend")) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        request_friend_layout.setVisibility(View.GONE);
                        return;
                    }

                    linearLayout.setVisibility(View.GONE);

                    requestedRecyclerView = findViewById(R.id.request_friend);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(NewFriendActivity.this, LinearLayoutManager.VERTICAL, false);
                    requestedRecyclerView.setLayoutManager(layoutManager);
                    memberAdapter = new MemberAdapter();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String friendName = jsonObject.getString("memName");
                        String friendID = jsonObject.getString("memID");

                        Member member = new Member();
                        member.setMemName(friendName);
                        member.setMemID(friendID);
                        memberAdapter.addItem(member);
                    }

                    requestedRecyclerView.setAdapter(memberAdapter);

                    request_friend_layout.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("requestedResult")) {
                try {
                    String TAG = "honey";
                    Log.d(TAG, response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        showToast("친구 신청 처리 완료!");
                        showRequest();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("deleteFriend")) {
                try {
                    Log.d("deleteFriend", response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        //삭제 성공여부 확인
                        showToast("거절 성공");
                    } else {
                        showToast("거절 실패");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("newFriend")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        String friend_name = jsonObject.getString("memName");

                        friend_name_text.setText(friend_name);
                        friend_id_text.setText(friend_id);

                        linearLayout.setVisibility(View.VISIBLE);
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