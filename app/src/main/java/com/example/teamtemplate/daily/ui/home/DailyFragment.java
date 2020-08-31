package com.example.teamtemplate.daily.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamtemplate.Group;
import com.example.teamtemplate.HttpClient;
import com.example.teamtemplate.R;
import com.example.teamtemplate.daily.DailyGroup;
import com.example.teamtemplate.Transaction;
import com.example.teamtemplate.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyFragment extends Fragment {

    //layouts
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //variables
    private List<Transaction> dataList;

    //URLs
    private  String urlDailyTransaction="http://jennyk97.dothome.co.kr/DailyTransaction.php";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily, container, false);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList);
        mRecyclerView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            DailyGroup dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");

            transactionProcess(dailyGroup);
        }

        return v;
    }

    protected void transactionProcess(final DailyGroup dailyGroup) {
        String GID = dailyGroup.getGID();

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlDailyTransaction);
        Map<String, String> params = new HashMap<>();
        params.put("GID", GID);

        networkTask.execute(params);

    }
    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        void setURL(String url){
            this.url=url;
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
            try{
                JSONArray j = new JSONArray(response);
                // Parse json
                for (int i = 0; i < j.length(); i++) {
                    try {

                        JSONObject jsonObject = j.getJSONObject(i);

                        Transaction transact = new Transaction();
                        transact.setAccountNum(jsonObject.getString("accountNum"));
                        transact.setTransactID(jsonObject.getString("transactID"));
                        transact.setTransactHistroy(jsonObject.getString("transactHistory"));
                        transact.setTransactMoney(jsonObject.getString("transactMoney"));
                        transact.setTransactVersion(jsonObject.getString("transactVersion"));
                        transact.setSince(jsonObject.getString("since"));
                        transact.setMID(jsonObject.getString("MID"));

                        ((TransactionAdapter) mAdapter).addItem(transact);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
