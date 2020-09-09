package kr.hongik.mnms.membership.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
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

    private Member loginMember;
    private Account loginMemberAccount;
    private MembershipGroup membershipGroup;

    //loginMember의 회비 납입
    private ArrayList<Transaction> transactionArrayList;

    //layouts
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //URLs
    private String ip = "203.149.75.14";

    //variables
    private List<Transaction> dataList;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_membership, container, false);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList);

        Bundle bundle = getArguments();
        if (bundle != null) {
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
            ip = bundle.getString("ip");

            setTransaction(membershipGroup);
        }

        return v;
    }

    private void setTransaction(final MembershipGroup membershipGroup) {
        String urlMembershipTransaction = "http://" + ip + "/membership";
        urlMembershipTransaction = "http://jennyk97.dothome.co.kr/MembershipTransaction.php";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlMembershipTransaction);
        networkTask.setTAG("setTransaction");

        Map<String, String> params = new HashMap<>();
        params.put("GID", membershipGroup.getGID());

        networkTask.execute(params);
    }

    private void setTransactionProcess(String response){
        try {
            JSONArray j = new JSONArray(response);
            // Parse json
            transactionArrayList = new ArrayList<>();
            for (int i = 0; i < j.length(); i++) {
                try {

                    JSONObject jsonObject = j.getJSONObject(i);

                    Transaction transact = new Transaction();
                    transact.setAccountNum(jsonObject.getString("accountNum"));
                    transact.setTransactID(jsonObject.getString("transactID"));
                    transact.setTransactHistroy(jsonObject.getString("transactHistory"));
                    transact.setTransactMoney(jsonObject.getString("transactMoney"));
                    transact.setSince(jsonObject.getString("since"));
                    transact.setMID(jsonObject.getString("MID"));

                    ((TransactionAdapter) mAdapter).addItem(transact);
                    if (transact.getAccountNum().equals(loginMember.getAccountNum())) {
                        transactionArrayList.add(transact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mRecyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        private String url,TAG;

        void setURL(String url) {
            this.url = url;
        }
        void setTAG(String TAG){
            this.TAG=TAG;
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
            if(TAG.equals("setTransaction")){
                setTransactionProcess(response);
            }

        }
    }


}
