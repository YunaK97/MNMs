package kr.hongik.mnms.membership.ui.manage;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.ui.friend.FriendListAdapter;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemClickListener;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemLongClickListener;
import kr.hongik.mnms.membership.MembershipGroup;


public class MembershipMemFragment extends Fragment {

    private Member loginMember;
    private MembershipGroup membershipGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private RecyclerView memberList;
    private FriendListAdapter memberAdapter;

    private Context context;
    private ViewGroup rootView;

    public MembershipMemFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getContext();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_membership_mem, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");
            memberArrayList = (ArrayList<Member>) bundle.get("memberArrayList");

            //showMember();
        }

        return rootView;
    }

    private void showMember() {
        memberList = rootView.findViewById(R.id.member_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        memberList.setLayoutManager(layoutManager);
        memberAdapter = new FriendListAdapter();
        memberAdapter.setItems(memberArrayList);
        memberList.setAdapter(memberAdapter);

        memberAdapter.setOnItemClickListener(new OnFriendItemClickListener() {
            @Override
            public void onItemClick(FriendListAdapter.ViewHolder holder, View view, int position) {

            }
        });
        memberAdapter.setOnItemLongClickListener(new OnFriendItemLongClickListener() {
            @Override
            public void onItemLongClick(FriendListAdapter.ViewHolder holder, View view, int position) {
                if (loginMember.getMemName().equals(membershipGroup.getPresident())) {
                    //selectDelMember(position);
                } else {
                    showToast("친구삭제는 회장만 가능합니다.");
                }
            }
        });
    }

    private void selectDelMember(int position) {
        final Member delMember = memberAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(delMember.getMemName()).setMessage("membership에서 삭제하시겠습니까?");

        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember(delMember.getMemID());
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

    private void deleteMember(String delMemberId) {
        String urlDeleteMember = "http://" + loginMember.getIp() + "/deleteMember";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDeleteMember);
        networkTask.setTAG("delMem");

        Map<String, String> params = new HashMap<>();
        params.put("memID", delMemberId);
        params.put("MID", membershipGroup.getMID());

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }


    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        protected String TAG;

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
            if (TAG.equals("delMem")) {

            }
        }
    }

}