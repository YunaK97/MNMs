package com.example.teamtemplate.daily.ui.dutch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.teamtemplate.Group;
import com.example.teamtemplate.HttpClient;
import com.example.teamtemplate.R;
import com.example.teamtemplate.daily.DailyGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DutchPayFragment extends Fragment {
    private TextView tv_total;
    private TextView tv_calc;
    private RadioButton rbt_up;
    private RadioButton rbt_round;
    private RadioButton rbt_down;
    private RadioGroup radioGroup;

    private String urlDailyGroup="http://jennyk97.dothome.co.kr/DailyGroup.php";

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
        if (bundle != null) {
            DailyGroup dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");

            dailyProcess(dailyGroup);

        }

        return v;
    }

    protected void dailyProcess(final DailyGroup dailyGroup) {
        String DID = dailyGroup.getDID();
        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlDailyGroup);

        Map<String, String> params = new HashMap<>();
        params.put("DID", DID);

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
            try{
                JSONArray j = new JSONArray(response);
                // Parse json
                for (int i = 0; i < j.length(); i++) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        DailyGroup dg = new DailyGroup();
                        dg.setDID(jsonObject.getString("DID"));
                        dg.setMoney(jsonObject.getInt("money"));
                        dg.setGID(jsonObject.getString("GID"));

                        tv_total.setText("총 사용 금액: " + String.valueOf(dg.getMoney()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
