package kr.hongik.mnms.daily.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.IdRes;
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
    /*
     * 더치페이
     * mon1. 내가 쓴돈
     * mon2. 남이 쓴돈
     * 총합 : monAll=mon1+mon2
     * 인당 : monOne=monAll/n;
     * if(mon1>monOne){
     *   내가 받을 돈 : mon1-monOne
     * }else if(mon1<monOne){
     *   내가 줄 돈 : monOne-mon1
     * }else (mon1==monOne){
     *   0
     * }
     *
     * 여러명일땐?
     * - 올림,내림,반올림 단위 설정(10원~10000원)
     * - 리셋버튼 필요 (이전까지의 기록 없애고 지금부터 더치페이 계산)
     * 각자 쓴돈 합치기
     * mon1~monN
     * 총 사용한돈 monAll=mon1+...+monN;
     * 인당 : monOne=monAll/n;
     * mon1~monN까지 줄세우기 오름차순
     * mon1이 monN에게 돈 주기 (monOne-mon1 만큼)
     * monOne과 본인이 쓴 돈 차이가 0이 될때까지 돈 주기
     * monOne보다 많이 쓴사람도 돈 차이가 0이 될때까지 돈 받기
     *
     *
     *
     * */

    private DailyGroup dailyGroup;
    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RadioGroup dutchType;
    private Spinner dutchRange;

    //variables
    private ArrayAdapter dutchRangeAdapter;
    private ViewGroup rootView;
    private List<Transaction> dataList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_daily, container, false);

        dutchRange = rootView.findViewById(R.id.dutch_type);
        dutchRangeAdapter = ArrayAdapter.createFromResource(rootView.getContext(), R.array.dutch_money, R.layout.support_simple_spinner_dropdown_item);
        dutchRangeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        dutchRange.setAdapter(dutchRangeAdapter);

        mRecyclerView = rootView.findViewById(R.id.recyclerView_daily);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList);
        mRecyclerView.setAdapter(mAdapter);

        dutchType = rootView.findViewById(R.id.dutch_radioGroup);
        dutchType.setOnCheckedChangeListener(radioGroupListener);

        Bundle bundle = getArguments();
        if (bundle != null) {
            dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");

            setTransaction(dailyGroup);
        }

        return rootView;
    }

    RadioGroup.OnCheckedChangeListener radioGroupListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            //단위 가져오기 - spinner
            if (i == R.id.rbt_up) {

            } else if (i == R.id.rbt_down) {

            } else if (i == R.id.rbt_round) {

            }
        }

    };

    private void setTransaction(final DailyGroup dailyGroup) {
        //데일리 그룹에서 사용한 돈을 출력
        //GID전송함
        //응답으로 GID와 관련된 모든 transaction들이 와야함
        String urlDailyTransaction = "http://" + loginMember.getIp() + "/daily/";

        int GID = dailyGroup.getGID();

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDailyTransaction);
        networkTask.setTAG("dailyTransaction");
        Map<String, String> params = new HashMap<>();
        params.put("GID", GID + "");

        networkTask.execute(params);

    }

    private void setTransactionProcess(String response){
        mRecyclerView = rootView.findViewById(R.id.recyclerView_daily);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList);


        try {
            JSONObject jsonObject = new JSONObject(response);
            int dailyTransactionSize = Integer.parseInt(jsonObject.getString("dailyTransactionSize"));
            for (int i = 0; i < dailyTransactionSize; i++) {

                Transaction transact = new Transaction();
                transact.setAccountNum(jsonObject.getString("accountNum" + i));
                transact.setTransactID(Integer.parseInt(jsonObject.getString("transactID" + i)));
                transact.setTransactHistroy(jsonObject.getString("transactHistory" + i));
                transact.setTransactMoney(Integer.parseInt(jsonObject.getString("transactMoney" + i)));
                transact.setSince(jsonObject.getString("since" + i));
                transact.setMID(Integer.parseInt(jsonObject.getString("DID" + i)));

                ((TransactionAdapter) mAdapter).addItem(transact);
            }

            mRecyclerView.setAdapter(mAdapter);

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
            if(TAG.equals("dailyTransaction")){
                setTransactionProcess(response);
            }

        }
    }

}
