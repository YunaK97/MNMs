package com.example.teamtemplate.daily.ui.dutch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
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
import com.example.teamtemplate.daily.DailyGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class DutchPayFragment extends Fragment {
    public TextView tv_total;
    public TextView tv_calc;
    public RadioButton rbt_up;
    public RadioButton rbt_round;
    public RadioButton rbt_down;
    public RadioGroup radioGroup;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dutch, container, false);

        tv_total = v.findViewById(R.id.tv_total);
        tv_calc = v.findViewById(R.id.tv_calc);

        radioGroup = v.findViewById(R.id.radioGroup);
        rbt_up = v.findViewById(R.id.rbt_up);
        rbt_round = v.findViewById(R.id.rbt_round);
        rbt_down = v.findViewById(R.id.rbt_down);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int i) {
                if(i == R.id.rbt_up) {

                }
                else if(i== R.id.rbt_round){

                }
                else if(i == R.id.rbt_down) {

                }
            }
        });

        Bundle bundle = getArguments();
        if (bundle == null) {
            System.out.println("------------NULL------------");

        }
        else {
            System.out.println("------------DutchPayFragment------------");
            Group group = (Group) bundle.getSerializable("dailyGroup");

            DailyGroup dailyGroup = new DailyGroup();
            dailyGroup.setDID(group.getDid());
            dailyProcess(dailyGroup);

        }

        return v;
    }

    protected void dailyProcess(final DailyGroup dailyGroup) {
        final String DID = dailyGroup.getDID();
        final String url="http://jennyk97.dothome.co.kr/DailyGroup.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    DailyGroup dg = new DailyGroup();
                    dg.setDID(jsonObject.getString("DID"));
                    dg.setMoney(jsonObject.getInt("money"));
                    dg.setDutchPay(jsonObject.getString("dutchPay"));
                    dg.setGID(jsonObject.getString("GID"));

                    tv_total.setText("총 사용 금액: " + String.valueOf(dg.getMoney()));

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
        queue.add(stringRequest);
    }



}
