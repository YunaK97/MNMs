package kr.hongik.mnms.membership.ui.manage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.ui.friend.FriendListAdapter;
import kr.hongik.mnms.membership.MembershipGroup;


public class ManageFeeFragment extends Fragment {

    private MembershipGroup membershipGroup;
    //layouts
    private TextView tv_monthly_membership;
    private TextView tv_notsubmit_cnt;
    private TextView tv_paid_membership;
    private TextView tv_paid_membership_cnt;
    private RecyclerView notsubmit_members;

    private ViewGroup viewGroup;

    private FriendListAdapter notsubmitListAdapter;
    private LinearLayoutManager linearLayoutManager;

    //URLs
    private String ip = "203.249.75.14";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_manage_fee, container, false);

        tv_monthly_membership = viewGroup.findViewById(R.id.tv_monthly_membership);
        tv_notsubmit_cnt = viewGroup.findViewById(R.id.tv_notsubmit_cnt);
        tv_paid_membership = viewGroup.findViewById(R.id.tv_paid_membership);
        tv_paid_membership_cnt = viewGroup.findViewById(R.id.tv_paid_membership_cnt);
        notsubmit_members = viewGroup.findViewById(R.id.notsubmit_members);


        Bundle bundle = getArguments();
        if (bundle != null) {
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");

            //setInformation(membershipGroup);
            tv_paid_membership_cnt.setText("10");
            tv_paid_membership.setText("500000");
            tv_notsubmit_cnt.setText("3");
            tv_monthly_membership.setText("50000");

            //getNotsubmitMembers();

        }
        return viewGroup;
    }

    public void setInformation(MembershipGroup membershipGroup) {
        //tv_monthly_membership.setText(membershipGroup.getMemberMoney());
        //tv_notsubmit_cnt.setText(membershipGroup.getNotSubmit());
        //tv_paid_membership 과 tv_paid_membership_cnt는 intent로 membershipfragment에서 해당 아이디의 transaction을 계산하여 넘겨주기
    }

    public void getNotsubmitMembers() {
        //미납자 명단 출력
        String urlNotSubmitMembers = "http://" + ip + "/notsubmitmembers";

        final String GID = membershipGroup.getGID();
        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlNotSubmitMembers);
        networkTask.setTAG("notSubmitMembers");

        Map<String, String> params = new HashMap<>();
        params.put("GID", GID);

        networkTask.execute(params);
    }

    private void notSubmitMembersProcess(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0) {
                return;
            }

            notsubmit_members = viewGroup.findViewById(R.id.notsubmit_members);
            linearLayoutManager = new LinearLayoutManager(viewGroup.getContext(), LinearLayoutManager.VERTICAL, false);
            notsubmit_members.setLayoutManager(linearLayoutManager);

            notsubmitListAdapter = new FriendListAdapter();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Member member = new Member();
                member.setMemID(jsonObject.getString("memID"));
                member.setMemName(jsonObject.getString("memName"));

                notsubmitListAdapter.addItem(member);
            }

            notsubmit_members.setAdapter(notsubmitListAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url, TAG;

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
            if(TAG.equals("notSubmitMembers")){
                notSubmitMembersProcess(response);
            }
        }
    }
}
