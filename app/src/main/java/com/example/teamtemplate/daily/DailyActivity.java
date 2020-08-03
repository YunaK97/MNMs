package com.example.teamtemplate.daily;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
    public RadioButton rbt_up;
    public RadioButton rbt_round;
    public RadioButton rbt_down;
    public RadioGroup radioGroup;
    public TextView tv_calc;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> dataList;

    DailyGroup dailyGroup = new DailyGroup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        rbt_up = findViewById(R.id.rbt_up);
        rbt_round = findViewById(R.id.rbt_round);
        rbt_down = findViewById(R.id.rbt_down);
        tv_calc = findViewById(R.id.tv_calc);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int i) {
                if(i == R.id.rbt_up) {
                    dailyGroup.setDutchPay("up");
                }
                else if(i== R.id.rbt_round){
                    dailyGroup.setDutchPay("round");
                }
                else if(i == R.id.rbt_down) {
                    dailyGroup.setDutchPay("down");
            }
        }
    });

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList, DailyActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        Transaction transact = new Transaction();
        transact.setAccountNum("1010");
        transactionProcess(transact);

        dailyGroup.setDID("D1");
        dailyProcess(dailyGroup);
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

        RequestQueue queue= Volley.newRequestQueue(DailyActivity.this);
        queue.add(stringRequest);
    }

    protected void dailyProcess(final DailyGroup dailyGroup) {
        final String DID = dailyGroup.getDID();
        final String dutchPay = dailyGroup.getDutchPay();
        final String url="http://jennyk97.dothome.co.kr/DailyGroup.php";

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("11111111111");
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    DailyGroup dg = new DailyGroup();
                    dg.setDID(jsonObject.getString("DID"));
                    dg.setMoney(jsonObject.getString("money"));
                    dg.setDutchPay(jsonObject.getString("dutchPay"));
                    dg.setGID(jsonObject.getString("GID"));

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
                params.put("dutchPay", dutchPay);
                params.put("DID", DID);
                System.out.println("========dutchPay DID========");
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(DailyActivity.this);
        queue.add(stringRequest2);
    }



}
