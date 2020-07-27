package com.example.teamtemplate.membership;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.transaction.Transaction;
import com.example.teamtemplate.transaction.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MembershipActivity extends AppCompatActivity {
    public TextView tv_president;
    public TextView tv_payday;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> dataList;

    MembershipGroup membershipGroup = new MembershipGroup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        tv_president = findViewById(R.id.ms_president);
        tv_payday = findViewById(R.id.ms_payday);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList, MembershipActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        Transaction transact = new Transaction();
        transact.setAccountNum("1010");
        transactionProcess(transact);

        membershipGroup.setMID("M1");
        membershipProcess(membershipGroup);
    }

    protected void transactionProcess(final Transaction transact) {
        final String accountNum = transact.getAccountNum();
        final String url="http://jennyk97.dothome.co.kr/ListTransaction.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

                            System.out.println("----------OOOOOO-----------");

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
                System.out.println("********에러********");
                Log.e("#####볼리에러####", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accountNum", accountNum);
                System.out.println("========accountNUM========");
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(MembershipActivity.this);
        queue.add(stringRequest);
    }

    protected void membershipProcess(final MembershipGroup membershipGroup) {
        final String MID = membershipGroup.getMID();
        final String url="http://jennyk97.dothome.co.kr/MembershipGroup.php";

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("11111111111");
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            MembershipGroup mg = new MembershipGroup();
                            mg.setMID(jsonObject.getString("MID"));
                            mg.setPresident(jsonObject.getString("president"));
                            mg.setPayDay(jsonObject.getString("payDay"));
                            mg.setMemberMoney(jsonObject.getString("memberMoney"));
                            mg.setTotalMoney(jsonObject.getString("totalMoney"));
                            mg.setGID(jsonObject.getString("GID"));

                            tv_president.setText("방장:"+mg.getPresident()+" ");
                            tv_payday.setText("회비 납입일:"+mg.getPayDay()+" ");

                            System.out.println("----------OOO222OOO-----------");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("********에러********");
                Log.e("#####볼리에러####", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("MID", MID);
                System.out.println("========MID========");
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(MembershipActivity.this);
        queue.add(stringRequest2);
    }



}
