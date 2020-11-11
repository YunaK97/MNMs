package kr.hongik.mnms.membership.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.Transaction;
import kr.hongik.mnms.TransactionAdapter;
import kr.hongik.mnms.membership.MembershipGroup;

public class MembershipFragment extends Fragment {
    //멤버십에서 사용한 돈 출력

    private Member loginMember;
    private Account loginMemberAccount;
    private MembershipGroup membershipGroup;

    //Membership계좌의 내역
    private ArrayList<Transaction> transactionArrayList;

    //layouts
    private RecyclerView rvMembership;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onResume() {
        super.onResume();
        setTransaction(membershipGroup);
    }

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_membership, container, false);

        rvMembership = v.findViewById(R.id.rvMembership);

        Bundle bundle = getArguments();
        if (bundle != null) {
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");

            setTransaction(membershipGroup);
        }

        return v;
    }

    private void setTransaction(final MembershipGroup membershipGroup) {
        //GID,MID,멤버십 계좌번호를 전송
        //멤버십과 관련된 모든 거래내역을 받아와야함(회비 입금,회비 사용내역)
        String urlMembershipTransaction = "http://" + loginMember.getIp() + "/membership/";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlMembershipTransaction);
        networkTask.setTAG("setTransaction");

        Map<String, String> params = new HashMap<>();
        params.put("GID", membershipGroup.getGID() + "");
        params.put("MID", membershipGroup.getMID() + "");
        params.put("accountNum", membershipGroup.getAccountNum());

        networkTask.execute(params);
    }

    private void setTransactionProcess(String response) {
        try {
            rvMembership.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            rvMembership.setLayoutManager(mLayoutManager);

            mAdapter = new TransactionAdapter();

            JSONObject jsonObject = new JSONObject(response);
            // Parse json
            transactionArrayList = new ArrayList<>();
            int membershipTransactionSize = jsonObject.getInt("membershipTransactionSize");
            for (int i = 0; i < membershipTransactionSize; i++) {

                Transaction transact = new Transaction();
                transact.setAccountNum(jsonObject.getString("accountNum" + i));
                transact.setTransactID(Integer.parseInt(jsonObject.getString("transactID" + i)));
                transact.setTransactHistroy(jsonObject.getString("transactHistory" + i));
                transact.setTransactMoney(Integer.parseInt(jsonObject.getString("transactMoney" + i)));
                transact.setSince(jsonObject.getString("since" + i));
                transact.setMID(Integer.parseInt(jsonObject.getString("MID" + i)));

                ((TransactionAdapter) mAdapter).addItem(transact);
                if (transact.getAccountNum().equals(loginMember.getAccountNum())) {
                    transactionArrayList.add(transact);
                }
            }
            rvMembership.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        private String url, TAG;

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
            Log.d(TAG, response);
            if (TAG.equals("setTransaction")) {
                setTransactionProcess(response);
            }

        }
    }


}
