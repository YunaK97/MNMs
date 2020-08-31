package com.example.teamtemplate.membership.ui.manage;

import android.os.AsyncTask;
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
import com.example.teamtemplate.HttpClient;
import com.example.teamtemplate.R;
import com.example.teamtemplate.membership.MembershipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ManageMemFragment extends Fragment {

    //layouts
    public TextView tv_president;

    //URLs
    public String urlMembershipGroup="http://jennyk97.dothome.co.kr/MembershipGroup.php";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_mem, container, false);

        tv_president = v.findViewById(R.id.tv_president);

        Bundle bundle = getArguments();
        if (bundle != null) {
            MembershipGroup membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");

            membershipProcess(membershipGroup);
        }

        return v;
    }

    protected void membershipProcess(final MembershipGroup membershipGroup) {
        final String MID = membershipGroup.getMID();

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlMembershipGroup);
        Map<String, String> params = new HashMap<>();
        params.put("MID", MID);

        networkTask.execute(params);

    }

    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        void setURL(String url){
            this.url=url;
        }
        @Override
        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터

            // Http 요청 준비 작업
            HttpClient.Builder http = new HttpClient.Builder("POST", url);

            // Parameter 를 전송한다.
            http.addAllParameters(maps[0]);

            //Http 요청 전송
            HttpClient post = http.create();
            post.request();
            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();
            // 응답 본문 가져오기

            return post.getBody();
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                MembershipGroup mg = new MembershipGroup();
                mg.setMID(jsonObject.getString("MID"));
                mg.setPresident(jsonObject.getString("president"));
                mg.setPayDay(jsonObject.getString("payDay"));
                mg.setMemberMoney(jsonObject.getInt("memberMoney"));
                mg.setTotalMoney(jsonObject.getInt("totalMoney"));
                mg.setNotSubmit(jsonObject.getInt("notSubmit"));
                mg.setGID(jsonObject.getString("GID"));

                tv_president.setText("방장: " + mg.getPresident());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
