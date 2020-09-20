package kr.hongik.mnms.daily.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import kr.hongik.mnms.daily.DailyGroup;

public class DailyFragment extends Fragment {

    private DailyGroup dailyGroup;
    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //variables
    private ViewGroup rootView;
    private List<Transaction> dataList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_daily, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerView_daily);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList);
        mRecyclerView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");

            transactionProcess(dailyGroup);
        }

        return rootView;
    }

    private void transactionProcess(final DailyGroup dailyGroup) {
        //데일리 그룹에서 사용한 돈을 출력
        //GID전송함
        //응답으로 GID와 관련된 모든 transaction들이 와야함
        String urlDailyTransaction = "http://" + loginMember.getIp() + "/daily";

        int GID = dailyGroup.getGID();

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDailyTransaction);
        Map<String, String> params = new HashMap<>();
        params.put("GID", GID + "");

        networkTask.execute(params);

    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;

        void setURL(String url) {
            this.url = url;
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
            mRecyclerView = rootView.findViewById(R.id.recyclerView_daily);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            dataList = new ArrayList<>();
            mAdapter = new TransactionAdapter(dataList);
            mRecyclerView.setAdapter(mAdapter);

            try {
                JSONObject jsonObject = new JSONObject(response);
                int dailyTransactionSize = Integer.parseInt(jsonObject.getString("dailyTransactionSize"));
                for (int i = 0; i < dailyTransactionSize; i++) {

                    Transaction transact = new Transaction();
                    transact.setAccountNum(jsonObject.getString("accountNum"+i));
                    transact.setTransactID(Integer.parseInt(jsonObject.getString("transactID"+i)));
                    transact.setTransactHistroy(jsonObject.getString("transactHistory"+i));
                    transact.setTransactMoney(Integer.parseInt(jsonObject.getString("transactMoney"+i)));
                    transact.setSince(jsonObject.getString("since"+i));
                    transact.setMID(Integer.parseInt(jsonObject.getString("DID"+i)));

                    ((TransactionAdapter) mAdapter).addItem(transact);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
