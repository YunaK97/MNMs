package kr.hongik.mnms.daily.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.TransactionAdapter;
import kr.hongik.mnms.daily.DailyGroup;


public class PreviewFragment extends Fragment {


    private DailyGroup dailyGroup;
    private Member loginMember;
    private Account loginMemberAccount;
    private ArrayList<Member> memberArrayList;
    private ArrayList<DutchMember> dutchMemberArrayList;

    private ViewGroup rootView;
    private RecyclerView rvPreviewMembers;

    public PreviewFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static PreviewFragment newInstance(String param1, String param2) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_preview, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
            memberArrayList=(ArrayList<Member>)bundle.getSerializable("memberArrayList");

            //정산 미리보기 출력
            setPreview();
        }

        return rootView;
    }

    private void setPreview(){
        String urlSetPreview = "http://" + loginMember.getIp() + "/daily/result";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlSetPreview);
        networkTask.setTAG("setPreview");

        Map<String, String> params = new HashMap<>();
        params.put("DID", dailyGroup.getDID() + "");

        networkTask.execute(params);
    }

    private void setPreviewProcess(String response){
        Map<String, String> memberMap = new HashMap<>();
        TextView tvTotalMoney = rootView.findViewById(R.id.tvPreviewMoney);
        int perMoney=0;

        rvPreviewMembers=rootView.findViewById(R.id.rvPreviewMembers);
        LinearLayoutManager layoutManager =new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
        rvPreviewMembers.setLayoutManager(layoutManager);
        dutchMemberArrayList=new ArrayList<>();

        DutchListAdapter dutchListAdapter=new DutchListAdapter();
        dutchListAdapter.setItems(dutchMemberArrayList);
        rvPreviewMembers.setAdapter(dutchListAdapter);

        try {
            JSONObject jsonObject = new JSONObject(response);

            int memSize = Integer.parseInt(jsonObject.getString("memSize"));


            for (int i = 0; i < memSize; i++) {
                String memID = jsonObject.getString("memID" + i);
                String money = jsonObject.getString("money" + i);

                memberMap.put(memID, money);
            }

            String totalMoney = jsonObject.getString("total");

            tvTotalMoney.setText(totalMoney);
            perMoney = Integer.parseInt(totalMoney) / memberArrayList.size();

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < memberArrayList.size(); i++) {
            Member member = memberArrayList.get(i);
            DutchMember dutchMember = new DutchMember();
            dutchMember.setMemID(member.getMemID());
            dutchMember.setMemName(member.getMemName());
            String money = memberMap.get(dutchMember.getMemID());
            if (money == null) {
                dutchMember.setUsedMoney(0);
                dutchMember.setRsMoney(-perMoney);
                dutchMember.setTmpRSMoney(-perMoney);
            } else {
                dutchMember.setUsedMoney(Integer.parseInt(money));
                dutchMember.setRsMoney(Integer.parseInt(money) - perMoney);
                dutchMember.setTmpRSMoney(Integer.parseInt(money) - perMoney);
            }

            dutchMemberArrayList.add(dutchMember);

            Comparator<DutchMember> noAsc = new Comparator<DutchMember>() {
                @Override
                public int compare(DutchMember item1, DutchMember item2) {
                    if(item1.getMemName().equals(item2.getMemName())){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            };
            Collections.sort(dutchMemberArrayList, noAsc);

            dutchListAdapter.setItems(dutchMemberArrayList);
        }

        rvPreviewMembers.setAdapter(dutchListAdapter);
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url, TAG;

        void setURL(String url) {
            this.url = url;
        }

        void setTAG(String TAG) {
            this.TAG = TAG;
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
            Log.d(TAG, response);
            if (TAG.equals("setPreview")) {
                setPreviewProcess(response);
            }

        }
    }

}