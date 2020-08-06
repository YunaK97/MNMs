package com.example.teamtemplate.daily.ui.dutch;

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
import com.example.teamtemplate.R;
import com.example.teamtemplate.daily.DailyGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class DutchPayFragment extends Fragment {

    TextView tv_total;
    TextView tv_calc;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dutch, container, false);

        tv_total = v.findViewById(R.id.tv_total);
        tv_calc = v.findViewById(R.id.tv_calc);

        DailyGroup dailyGroup = new DailyGroup();
        dailyGroup.setDID("D1");
        dailyProcess(dailyGroup);
        return v;
    }

    protected void dailyProcess(final DailyGroup dailyGroup) {
        final String DID = dailyGroup.getDID();
        final String url="http://jennyk97.dothome.co.kr/DailyGroup.php";

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    DailyGroup dg = new DailyGroup();
                    dg.setDID(jsonObject.getString("DID"));
                    dg.setMoney(jsonObject.getString("money"));
                    dg.setDutchPay(jsonObject.getString("dutchPay"));
                    dg.setGID(jsonObject.getString("GID"));

                    tv_total.setText(dg.getMoney());

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
                params.put("DID", DID);
                System.out.println("========dutchPay DID========");
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest2);
    }



}
