package kr.hongik.mnms.mainscreen.ui.transaction;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.Transaction;
import kr.hongik.mnms.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TransactionList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private Context context;
    private ViewGroup rootView;
    private RecyclerView transactionList;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> dataList;

    //URLs
    public String ip;


    public TransactionList() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=container.getContext();

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_transaction_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle=getArguments();
        if(bundle!=null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
            ip=bundle.getString("ip");
        }
        //그룹리스트 출력
        transactionView(rootView);

        return rootView;
    }

    private void transactionView(ViewGroup rootView){
        transactionList=rootView.findViewById(R.id.main_transaction_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
        transactionList.setLayoutManager(layoutManager);

        dataList=new ArrayList<>();
        transactionAdapter=new TransactionAdapter(dataList);
        transactionList.setAdapter(transactionAdapter);

        Transaction transaction=new Transaction();
        Transaction transact = new Transaction();
        transact.setAccountNum("1010");
        transactionProcess(transact);
    }

    private void transactionProcess(final Transaction transaction){
        String urlListTransaction="http://"+ip+"/listTransaction";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlListTransaction);
        networkTask.setTAG("listTransaction");

        Map<String, String> params = new HashMap<>();
        params.put("accountNum", transaction.getAccountNum());

        networkTask.execute(params);
    }

    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        String TAG;

        void setURL(String url){
            this.url=url;
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
            if(TAG.equals("listTransaction")){
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

                            ((TransactionAdapter) transactionAdapter).addItem(transact);

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
}
