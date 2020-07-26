package com.example.teamtemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MembershipActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new MembershipAdapter(dataList, MembershipActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        Transaction transact = new Transaction();
        transact.setAccountNum("979796");

        //process(transact);
        membershipProcess(transact);
    }

    protected void process(final Transaction transact) {
        final String accountNum = transact.getAccountNum();
        final String url="http://jennyk97.dothome.co.kr/ListTransaction.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            System.out.println("----------OOO55OOO-----------");
                            JSONArray j = new JSONArray(response);
                            System.out.println("----------OOO777OOO-----------");
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

                                    ((MembershipAdapter) mAdapter).addItem(transact);

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

        RequestQueue queue= Volley.newRequestQueue(MembershipActivity.this);
        queue.add(stringRequest);
    }


    protected void membershipProcess(final Transaction transact){

        final String accountNum = transact.getAccountNum();
        final String url="http://jennyk97.dothome.co.kr/ListTransaction.php";

        //결과를 JsonArray 받을 것이므로..
        //StringRequest가 아니라..
        //JsonArrayRequest를 이용할 것임

        final JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.POST, url,  null, new Response.Listener<JSONArray>() {
            //volley 라이브러리의 GET방식은 버튼 누를때마다 새로운 갱신 데이터를 불러들이지 않음. 그래서 POST 방식 사용

            @Override
            public void onResponse(JSONArray response) {
                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Transaction transact = new Transaction();
                        transact.setAccountNum(jsonObject.getString("accountNum"));
                        transact.setTransactID(jsonObject.getString("transactID"));
                        transact.setTransactHistroy(jsonObject.getString("transactHistory"));
                        transact.setTransactMoney(jsonObject.getString("transactMoney"));
                        transact.setTransactVersion(jsonObject.getString("transactVersion"));
                        transact.setSince(jsonObject.getString("since"));

                        ((MembershipAdapter) mAdapter).addItem(transact);
                        System.out.println("----------OOOOOO-----------");

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
        });

        RequestQueue queue= Volley.newRequestQueue(MembershipActivity.this);
        queue.add(jsonArrayRequest);
    }

}