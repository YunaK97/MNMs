package kr.hongik.mnms.mainscreen.ui.transaction;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.NetworkTask;
import kr.hongik.mnms.R;
import kr.hongik.mnms.Transaction;
import kr.hongik.mnms.TransactionAdapter;


public class TransactionList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private Context context;
    private ViewGroup rootView;
    private RecyclerView rvMainTransactionList;
    private TransactionAdapter transactionAdapter;

    public TransactionList() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        showTransaction();
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
        showTransaction();

        return rootView;
    }

    public void showTransaction() {
        String urlListTransaction = "http://" + loginMember.getIp() + "/member/listTransaction";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlListTransaction);
        networkTask.setTAG("listTransaction");

        Map<String, String> params = new HashMap<>();
        params.put("accountNum", loginMember.getAccountNum());

        networkTask.execute(params);
    }

    private void listTransactionProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            loginMemberAccount.setAccountBalance(Integer.parseInt(jsonObject.getString("accountBalance")));

            rvMainTransactionList = rootView.findViewById(R.id.rvMainTransactionList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
            rvMainTransactionList.setLayoutManager(layoutManager);
            transactionAdapter=new TransactionAdapter();

            ArrayList<Transaction> transactionArrayList=new ArrayList<>();

            TextView tvAccountBalance=rootView.findViewById(R.id.tvAccountBalance);
            tvAccountBalance.setText(jsonObject.getString("accountBalance"));
            int listTransactionSize = Integer.parseInt(jsonObject.getString("listTransactionSize"));
            if (listTransactionSize == 0) return;
            for (int i=0;i<listTransactionSize;i++) {
                Transaction transact = new Transaction();
                transact.setAccountNum(jsonObject.getString("accountNum" + i));
                transact.setTransactID(Integer.parseInt(jsonObject.getString("transactID"+i)));
                transact.setTransactHistroy(jsonObject.getString("transactHistory"+i));
                transact.setTransactMoney(Integer.parseInt(jsonObject.getString("transactMoney"+i)));
                transact.setSince(jsonObject.getString("since"+i));
                transact.setMID(Integer.parseInt(jsonObject.getString("MID"+i)));
                transact.setDID(Integer.parseInt(jsonObject.getString("DID"+i)));

                transactionArrayList.add(transact);
            }

//            Comparator<Transaction> noAsc = new Comparator<Transaction>() {
//                @Override
//                public int compare(Transaction item1, Transaction item2) {
//                    return item1.getSince().compareTo(item2.getSince());
//                }
//            };
//            Collections.sort(transactionArrayList, noAsc);

            transactionAdapter.setItems(transactionArrayList);
            rvMainTransactionList.setAdapter(transactionAdapter);

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
