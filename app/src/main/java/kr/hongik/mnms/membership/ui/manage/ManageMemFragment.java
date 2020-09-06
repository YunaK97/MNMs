package kr.hongik.mnms.membership.ui.manage;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;

import kr.hongik.mnms.HttpClient;

import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.ui.friend.FriendListAdapter;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemClickListener;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemLongClickListener;
import kr.hongik.mnms.membership.MembershipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ManageMemFragment extends Fragment {

    private Member loginMember;
    private Account loginMemberAccount;
    private MembershipGroup membershipGroup;

    private RecyclerView memberList;
    private FriendListAdapter memberAdapter;
    private String president;

    private Context context;
    private ViewGroup rootView;

    //layouts
    public TextView tv_president;

    //URLs
    String ip = "203.249.75.14";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getContext();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_manage_mem, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");
            ip=bundle.getString("ip");

            showGroup(membershipGroup);
            showMember(membershipGroup);
        }

        return rootView;
    }

    protected void showGroup(MembershipGroup membershipGroup) {
        String urlShowGroup = "http://" + ip + "/membershipGroup";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlShowGroup);
        networkTask.setTAG("showGroup");
        Map<String, String> params = new HashMap<>();
        params.put("MID", membershipGroup.getMID());

        networkTask.execute(params);
    }

    protected void showMember(MembershipGroup membershipGroup) {
        String urlShowMember = "http://" + ip + "/showMember";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlShowMember);
        networkTask.setTAG("showMem");
        Map<String, String> params = new HashMap<>();
        params.put("MID", membershipGroup.getMID());

        networkTask.execute(params);
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
        String urlDeleteMember = "http://" + ip + "/deleteMember";

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

    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
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

            } else if (TAG.equals("showMem")) {
                try {
                    //JSONArray jsonArray=new JSONArray(response);
                    //if(jsonArray.length()==0){ return; }

                    memberList = rootView.findViewById(R.id.member_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
                    memberList.setLayoutManager(layoutManager);
                    memberAdapter = new FriendListAdapter();

                    for (int i = 0; i < 10/*jsonArray.length()*/; i++) {
                        //JSONObject item = jsonArray.getJSONObject(i);
                        //String friendId = item.getString("memID");
                        //String friendName = item.getString("memName");

                        Member member = new Member();
                        //member.setMemName(friendName);
                        //member.setMemID(friendId);
                        member.setMemName(i + "님");
                        member.setMemID(i + "as" + i);
                        memberAdapter.addItem(member);
                    }

                    Comparator<Member> noAsc = new Comparator<Member>() {
                        @Override
                        public int compare(Member item1, Member item2) {
                            if (item1.getMemID().equals(president)) {
                                item1.setMemName(president);
                                return -1;
                            }
                            return item1.getMemName().compareTo(item2.getMemName());
                        }
                    };
                    Collections.sort(memberAdapter.getList(), noAsc);
                    memberList.setAdapter(memberAdapter);


                    memberAdapter.setOnItemClickListener(new OnFriendItemClickListener() {
                        @Override
                        public void onItemClick(FriendListAdapter.ViewHolder holder, View view, int position) {

                        }
                    });
                    memberAdapter.setOnItemLongClickListener(new OnFriendItemLongClickListener() {
                        @Override
                        public void onItemLongClick(FriendListAdapter.ViewHolder holder, View view, int position) {
                            if (loginMember.getMemName().equals(president)) {
                                selectDelMember(position);
                            } else {
                                showToast("친구 선택");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (TAG.equals("showGroup")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    MembershipGroup mg = new MembershipGroup();
                    mg.setMID(jsonObject.getString("MID"));
                    mg.setPresident(jsonObject.getString("president"));
                    mg.setPayDay(jsonObject.getString("payDay"));
                    mg.setMemberMoney(jsonObject.getInt("memberMoney"));
                    mg.setTotalMoney(jsonObject.getInt("totalMoney"));
                    mg.setNotSubmit(jsonObject.getInt("notSubmit"));
                    mg.setGID(jsonObject.getString("GID"));

                    president = mg.getPresident();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
