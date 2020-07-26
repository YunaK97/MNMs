package com.example.teamtemplate.daily;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class DailyActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList, DailyActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        Transaction transact = new Transaction();
        transact.setAccountNum("979796");

        transactionProcess(transact);
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
                Map<String, String> params = new HashMap<String, String >();
                params.put("accountNum", accountNum);
                System.out.println("========accountNUM========");
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue( DailyActivity.this);
        queue.add(stringRequest);
    }

}
