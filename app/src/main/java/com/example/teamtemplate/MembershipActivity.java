package com.example.teamtemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MembershipActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> dataList;

    String TAG_SUCCESS="success";

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

        membershipProcess(transact);

    }

    protected void membershipProcess(Transaction transact){

        final String accountNum = transact.getAccountNum();

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    Boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success){
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
                    else{
                        System.out.println("----------FFF-----------");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestMembership requestMembership =new RequestMembership(accountNum,responseListener);
        RequestQueue queue= Volley.newRequestQueue(MembershipActivity.this);
        queue.add(requestMembership);
    }
}