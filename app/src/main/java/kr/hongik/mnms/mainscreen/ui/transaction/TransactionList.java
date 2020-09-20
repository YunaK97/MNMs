package kr.hongik.mnms.mainscreen.ui.transaction;

import android.content.Context;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.Transaction;
import kr.hongik.mnms.TransactionAdapter;


public class TransactionList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private Context context;
    private ViewGroup rootView;
    private RecyclerView transactionList;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> dataList;

    public TransactionList() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_transaction_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle = getArguments();
        if (bundle != null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
        }
        //그룹리스트 출력
        transactionView(rootView);

        return rootView;
    }

    private void transactionView(ViewGroup rootView) {
        transactionList = rootView.findViewById(R.id.main_transaction_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        transactionList.setLayoutManager(layoutManager);

        dataList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(dataList);
        transactionList.setAdapter(transactionAdapter);

        Transaction transact = new Transaction();
        transact.setAccountNum(loginMember.getAccountNum());
        showTransaction(transact);
    }

    private void showTransaction(Transaction transaction) {
        String urlListTransaction = "http://" + loginMember.getIp() + "/listTransaction";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlListTransaction);
        networkTask.setTAG("listTransaction");

        Map<String, String> params = new HashMap<>();
        params.put("accountNum", transaction.getAccountNum());

        networkTask.execute(params);
    }

    private void listTransactionProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int transactionSize = Integer.parseInt(jsonObject.getString("transactionSize"));
            if (transactionSize == 0) return;

            for (int i=0;i<transactionSize;i++) {
                Transaction transact = new Transaction();
                transact.setAccountNum(jsonObject.getString("accountNum" + i));
                transact.setTransactID(Integer.parseInt(jsonObject.getString("transactID"+i)));
                transact.setTransactHistroy(jsonObject.getString("transactHistory"+i));
                transact.setTransactMoney(Integer.parseInt(jsonObject.getString("transactMoney"+i)));
                transact.setSince(jsonObject.getString("since"+i));
                transact.setMID(Integer.parseInt(jsonObject.getString("MID"+i)));

                transactionAdapter.addItem(transact);

                transactionList.setAdapter(transactionAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
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
            if (TAG.equals("listTransaction")) {
                listTransactionProcess(response);
            }
        }
    }
}
