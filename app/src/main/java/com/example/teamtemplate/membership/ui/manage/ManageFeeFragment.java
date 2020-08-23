package com.example.teamtemplate.membership.ui.manage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Group;
import com.example.teamtemplate.R;
import com.example.teamtemplate.membership.MembershipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ManageFeeFragment extends Fragment {
    public TextView tv_payday;
    public TextView tv_memberMoney;
    public TextView tv_totalMoney;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_fee, container, false);

        tv_payday = v.findViewById(R.id.tv_payday);
        tv_memberMoney = v.findViewById(R.id.tv_memberMoney);
        tv_totalMoney = v.findViewById(R.id.tv_totalMoney);

        Bundle bundle = getArguments();
        if (bundle == null) {
            System.out.println("------------NULL------------");

        } else {
            System.out.println("------------ManageFeeFragment------------");
            Group group = (Group) bundle.getSerializable("membershipGroup");

            MembershipGroup membershipGroup = new MembershipGroup();
            membershipGroup.setMID(group.getMid());
            membershipProcess(membershipGroup);
        }

        return v;
    }

    protected void membershipProcess(final MembershipGroup membershipGroup) {
        final String MID = membershipGroup.getMID();
        final String url="http://jennyk97.dothome.co.kr/MembershipGroup.php";

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    MembershipGroup mg = new MembershipGroup();
                    mg.setMID(jsonObject.getString("MID"));
                    mg.setPresident(jsonObject.getString("president"));
                    mg.setPayDay(jsonObject.getString("payDay"));
                    mg.setMemberMoney(jsonObject.getString("memberMoney"));
                    mg.setTotalMoney(jsonObject.getString("totalMoney"));
                    mg.setGID(jsonObject.getString("GID"));

                    tv_payday.setText(mg.getPayDay());
                    tv_memberMoney.setText(mg.getMemberMoney());
                    tv_payday.setText(mg.getTotalMoney());

                    System.out.println("----------OOO444OOO-----------");

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
        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest2);
    }

}
