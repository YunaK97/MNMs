package kr.hongik.mnms.mainscreen.ui.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;

public class FriendList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Member loginMember;

    //layouts
    private FriendListAdapter friendListAdapter;
    private RecyclerView friend_list;
    private Context context;
    private ViewGroup rootView;

    //variabls
    private String TAG_SUCCESS = "success";

    public FriendList() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        showFriend();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
        }
        showFriend();

        return rootView;
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

    private void selectDelFriend(int position) {
        final Member delMember = friendListAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(delMember.getMemID()).setMessage("친구목록에서 삭제하시겠습니까?");

        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFriend(delMember.getMemID());
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("삭제 취소");
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void showToast(String data) {
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }

    private void showFriendProcess(String response) {
        try {
            JSONObject jsonObject=new JSONObject(response);
            int showFriendSize=Integer.parseInt(jsonObject.getString("showFriendSize"));
            if (showFriendSize==0) return;

            friend_list = rootView.findViewById(R.id.main_friend_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
            friend_list.setLayoutManager(layoutManager);
            friendListAdapter = new FriendListAdapter();

            for (int i = 0; i < showFriendSize; i++) {
                String friendId = jsonObject.getString("memID"+i);
                String friendName = jsonObject.getString("memName"+i);

                Member member = new Member();
                member.setMemName(friendName);
                member.setMemID(friendId);
                friendListAdapter.addItem(member);
            }
            friend_list.setAdapter(friendListAdapter);
            friendListAdapter.setOnItemLongClickListener(new OnFriendItemLongClickListener() {
                @Override
                public void onItemLongClick(FriendListAdapter.ViewHolder holder, View view, int position) {
                    selectDelFriend(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteFriendProcess(String response) {
        try {
            Log.d("deleteFriend", response);
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean(TAG_SUCCESS);
            if (success) {
                //삭제 성공여부 확인
                showToast("친구 삭제 성공");
                showFriend();
            } else {
                showToast("친구 삭제 실패");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            if (TAG.equals("showFriend")) {
                showFriendProcess(response);
            } else if (TAG.equals("deleteFriend")) {
                deleteFriendProcess(response);
            }
        }
    }
}
