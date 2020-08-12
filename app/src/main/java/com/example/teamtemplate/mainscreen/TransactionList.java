package com.example.teamtemplate.mainscreen;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.membership.MembershipGroup;
import com.example.teamtemplate.transaction.Transaction;
import com.example.teamtemplate.transaction.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransactionList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;
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
        context=container.getContext();

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_transaction_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle=getArguments();
        loginMember= (Member) bundle.getSerializable("loginMember");
        loginMemberAccount= (Account) bundle.getSerializable("loginMemberAccount");

        //그룹리스트 출력
        transactionView(rootView);

        return rootView;
    }


    public void transactionView(ViewGroup rootView){
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

        return;

    }

    protected void transactionProcess(final Transaction transaction){
        final String accountNum = transaction.getAccountNum();
        final String url="http://jennyk97.dothome.co.kr/ListTransaction.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d("honey",response);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accountNum", accountNum);
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }
}
