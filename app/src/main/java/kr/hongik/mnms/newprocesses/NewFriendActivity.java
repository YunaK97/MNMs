package kr.hongik.mnms.newprocesses;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import kr.hongik.mnms.NetworkTask;
import kr.hongik.mnms.ProgressDialog;
import kr.hongik.mnms.R;

public class NewFriendActivity extends AppCompatActivity {
    private Member loginMember;

    //layouts
    private TextView tvFriendName, tvFriendID;
    private ImageButton btnIDsearch;
    private Button btnAddFriend, btnRequestAccept, btnRequestReject;
    private LinearLayout LLFriendLayout, LLRequestFriend;
    private RecyclerView rvRequestFriend;
    private MemberAdapter memberAdapter;

    //variables
    private String TAG_SUCCESS = "success";
    private String friend_id;
    private ArrayList<Member> selectedFriend;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog.show();

        //intent 받아오기
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");

        //findViewById
        btnIDsearch = findViewById(R.id.btnIDsearch);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        LLFriendLayout = findViewById(R.id.LLFriendLayout);
        tvFriendName = findViewById(R.id.tvFriendName);
        tvFriendID = findViewById(R.id.tvFriendID);
        rvRequestFriend = findViewById(R.id.rvRequestFriend);
        btnRequestAccept = findViewById(R.id.btnRequestAccept);
        btnRequestReject = findViewById(R.id.btnRequestReject);
        LLRequestFriend = findViewById(R.id.LLRequestFriend);

        showRequest();

        progressDialog.dismiss();

        btnIDsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //친구 ID 검색
                friend_id = ((TextView) findViewById(R.id.etFriendSearch)).getText().toString();
                if (friend_id.equals(loginMember.getMemID()) || friend_id == null
                        || friend_id.length() < 4 || friend_id.length() > 20) {
                    showToast("불가능한 id 입니다.");
                } else {
                    LLRequestFriend.setVisibility(View.GONE);
                    searchFriend(friend_id);
                }
            }
        });

        //체크박스 -> 수락버튼 -> accept
        btnRequestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendResult("friend");
            }
        });

        //체크박스 -> 거절버튼 -> reject
        btnRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendResult("reject");
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        progressDialog.show();
        //검색한 id를 친구추가 요청
        //memID,friendID 전송
        //스프링에서 친구요청처리 해주면 됨
        //이미 친구사이인경우 처리해주기
        String urlNewFriendAdd = "http://" + loginMember.getIp() + "/member/friendAdd";

        //내가 상대방에게 친구추가 요청
        final NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewFriendAdd);
        networkTask.setTAG("newFriendAdd");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("friendID", friend_id);

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newFriendAddProcess(networkTask.getResponse());
            }
        }, 1500);
    }

    private void newFriendAddProcess(String response) {
        progressDialog.dismiss();
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
    }

    private void showRequest() {
        //내가 받은 친구요청리스트들을 출력
        //memID를 전송
        //friend테이블에서 request 상태인것들을 모두 출력
        String urlRequestedFriend = "http://" + loginMember.getIp() + "/member/requestedFriend";

        //나에게 들어온 요청 출력
        final NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlRequestedFriend);
        networkTask.setTAG("requestedFriend");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestedFriendProcess(networkTask.getResponse());
            }
        }, 1500);

    }

    private void requestedFriendProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            LLFriendLayout.setVisibility(View.GONE);
            rvRequestFriend = findViewById(R.id.rvRequestFriend);
            LinearLayoutManager layoutManager = new LinearLayoutManager(NewFriendActivity.this, LinearLayoutManager.VERTICAL, false);
            rvRequestFriend.setLayoutManager(layoutManager);
            memberAdapter = new MemberAdapter();

            int showFriendSize = jsonObject.getInt("showFriendSize");
            for (int i = 0; i < showFriendSize; i++) {
                String friendName = jsonObject.getString("memName" + i);
                String friendID = jsonObject.getString("memID" + i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendID);
                memberAdapter.addItem(member);
            }

            rvRequestFriend.setAdapter(memberAdapter);

            LLRequestFriend.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void friendResult(final String TAG_RESULT) {

        progressDialog.show();

        //내가 받은 요청들에 대한 응답
        //수락의 경우 : memID와 친구들의 friendID 배열을 전송 - 둘 사이가 친구가 되도록 하면됨
        //거절의 경우 : memID와 친구들의 friendID 배열을 전송 - friend테이블에서 friendID에 대한 컬럼들을 삭제하면 됨
        String urlRequestedResult = "http://" + loginMember.getIp() + "/member/friendResult";

        // 수락or거절 결과 전송
        selectedFriend = new ArrayList<>();
        for (int i = 0; i < memberAdapter.getItemCount(); i++) {
            if (memberAdapter.getItem(i).isChecked()) {
                selectedFriend.add(memberAdapter.getItem(i));
            }
        }

        final NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlRequestedResult);
        networkTask.setTAG("requestedResult");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("TAG", TAG_RESULT);
        params.put("friendSize", selectedFriend.size() + "");

        for (int i = 0; i < selectedFriend.size(); i++) {
            params.put("memID" + i, selectedFriend.get(i).getMemID());
        }

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestedResultProcess(networkTask.getResponse());
            }
        }, 1500);
    }

    private void requestedResultProcess(String response) {
        progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                showToast("친구 신청 처리 완료!");
                showRequest();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LLRequestFriend.setVisibility(View.GONE);
    }

    private void searchFriend(final String friend_id) {
        progressDialog.show();

        //친구 추가할라고 ID 검색
        //memID가 검색하려는 ID임
        //member테이블에 해당 멤버가 있는지 확인
        String urlNewFriend = "http://" + loginMember.getIp() + "/member/newFriend";

        final NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNewFriend);
        networkTask.setTAG("newFriend");

        Map<String, String> params = new HashMap<>();
        params.put("memID", friend_id);

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newFriendProcess(networkTask.getResponse());
            }
        }, 1500);
    }

    private void newFriendProcess(String response) {
        progressDialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                String friend_name = jsonObject.getString("memName");

                tvFriendName.setText(friend_name);
                tvFriendID.setText(friend_id);

                LLFriendLayout.setVisibility(View.VISIBLE);
            } else {
                showToast("ID 검색 실패");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

//    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
//        protected String url;
//        String TAG;
//
//        void setURL(String url) {
//            this.url = url;
//        }
//
//        void setTAG(String TAG) {
//            this.TAG = TAG;
//        }
//
//        @Override
//        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터
//
//            // Http 요청 준비 작업
//            HttpClient.Builder http = new HttpClient.Builder("POST", url);
//
//            // Parameter 를 전송한다.
//            http.addAllParameters(maps[0]);
//
//            //Http 요청 전송
//            HttpClient post = http.create();
//            post.request();
//            // 응답 상태코드 가져오기
//            int statusCode = post.getHttpStatusCode();
//            // 응답 본문 가져오기
//
//            return post.getBody();
//        }
//
//        @Override
//        protected void onPostExecute(String response) {
//            Log.d(TAG, response);
//            if (TAG.equals("newFriendAdd")) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean dup = jsonObject.getBoolean("dup");
//                    if (dup) {
//                        showToast("이미 친구사이입니다.");
//                    } else {
//                        boolean success = jsonObject.getBoolean(TAG_SUCCESS);
//                        if (success) {
//                            showToast("친구추가 신청 완료");
//                            finish();
//                        } else {
//                            showToast("친구 추가 실패ㅠ");
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if (TAG.equals("requestedFriend")) {
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//
//                    LLFriendLayout.setVisibility(View.GONE);
//                    rvRequestFriend = findViewById(R.id.rvRequestFriend);
//                    LinearLayoutManager layoutManager = new LinearLayoutManager(NewFriendActivity.this, LinearLayoutManager.VERTICAL, false);
//                    rvRequestFriend.setLayoutManager(layoutManager);
//                    memberAdapter = new MemberAdapter();
//
//                    int showFriendSize=jsonObject.getInt("showFriendSize");
//                    for (int i = 0; i < showFriendSize; i++) {
//                        String friendName = jsonObject.getString("memName"+i);
//                        String friendID = jsonObject.getString("memID"+i);
//
//                        Member member = new Member();
//                        member.setMemName(friendName);
//                        member.setMemID(friendID);
//                        memberAdapter.addItem(member);
//                    }
//
//                    rvRequestFriend.setAdapter(memberAdapter);
//
//                    LLRequestFriend.setVisibility(View.VISIBLE);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if (TAG.equals("requestedResult")) {
//                try {
//                    String TAG = "honey";
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
//                    if (success) {
//                        showToast("친구 신청 처리 완료!");
//                        showRequest();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                LLRequestFriend.setVisibility(View.GONE);
//            } else if (TAG.equals("deleteFriend")) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
//                    if (success) {
//                        //삭제 성공여부 확인
//                        showToast("거절 성공");
//                    } else {
//                        showToast("거절 실패");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                LLRequestFriend.setVisibility(View.GONE);
//            } else if (TAG.equals("newFriend")) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
//                    if (success) {
//                        String friend_name = jsonObject.getString("memName");
//
//                        tvFriendName.setText(friend_name);
//                        tvFriendID.setText(friend_id);
//
//                        LLFriendLayout.setVisibility(View.VISIBLE);
//                    } else {
//                        showToast("ID 검색 실패");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}