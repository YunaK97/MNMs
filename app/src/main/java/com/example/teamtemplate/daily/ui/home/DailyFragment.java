package com.example.teamtemplate.daily.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.R;
import com.example.teamtemplate.daily.DailyGroup;
import com.example.teamtemplate.transaction.Transaction;
import com.example.teamtemplate.transaction.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> dataList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_daily, container, false);

//        // Spinner
//        Spinner yearSpinner = v.findViewById(R.id.spinner_year);
//        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_year, android.R.layout.simple_spinner_item);
//        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        yearSpinner.setAdapter(yearAdapter);
//
//        Spinner monthSpinner = v.findViewById(R.id.spinner_month);
//        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_month, android.R.layout.simple_spinner_item);
//        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        monthSpinner.setAdapter(monthAdapter);
//
//        // Spinner
//        Spinner yearSpinner2 = v.findViewById(R.id.spinner_year2);
//        ArrayAdapter yearAdapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.date_year, android.R.layout.simple_spinner_item);
//        yearAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        yearSpinner2.setAdapter(yearAdapter2);
//
//        Spinner monthSpinner2 = v.findViewById(R.id.spinner_month2);
//        ArrayAdapter monthAdapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.date_month, android.R.layout.simple_spinner_item);
//        monthAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        monthSpinner2.setAdapter(monthAdapter2);

        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataList = new ArrayList<>();
        mAdapter = new TransactionAdapter(dataList);
        mRecyclerView.setAdapter(mAdapter);

        Transaction transact = new Transaction();
        transact.setAccountNum("1010");
        transactionProcess(transact);

        DailyGroup dailyGroup = new DailyGroup();
        dailyGroup.setDID("D1");
        //dailyProcess(dailyGroup);

        return v;
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

        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }
}