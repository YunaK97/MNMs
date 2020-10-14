package kr.hongik.mnms.membership.ui.manage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.ui.friend.FriendListAdapter;
import kr.hongik.mnms.membership.MembershipGroup;


public class ManageFeeFragment extends Fragment {

    private MembershipGroup membershipGroup;
    private Member loginMember;
    //layouts
    private TextView tv_monthly_membership;
    private TextView tv_notsubmit_cnt;
    private TextView tv_my_notSubmit;

    private ViewGroup viewGroup;

    public ManageFeeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_manage_fee, container, false);

        tv_monthly_membership = viewGroup.findViewById(R.id.tv_monthly_membership);
        tv_notsubmit_cnt = viewGroup.findViewById(R.id.tv_notsubmit_cnt);
        tv_my_notSubmit=viewGroup.findViewById(R.id.tv_my_notSubmit);

        Bundle bundle = getArguments();
        if (bundle != null) {
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");

            setInformation(membershipGroup);
        }
        return viewGroup;
    }

    private void setInformation(MembershipGroup membershipGroup){
        String urlSetInfo="http://"+loginMember.getIp()+"/membership/notSubmit";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("setInfo");
        networkTask.setURL(urlSetInfo);

        Map<String,String> params=new HashMap<>();
        params.put("memID",loginMember.getMemID());
        params.put("MID",membershipGroup.getMID()+"");
        params.put("GID",membershipGroup.getGID()+"");

        //멤버십의 회비
        tv_monthly_membership.setText(membershipGroup.getFee()+"");
        //멤버십의 미납가능횟수
        tv_notsubmit_cnt.setText(membershipGroup.getNotSubmit()+"");

        networkTask.execute(params);
    }
    public void setInformationProcess(String response) {
        //내 미납횟수
        tv_my_notSubmit.setText("1");

        if(loginMember.getMemID().equals(membershipGroup.getPresident())){
            try {
                JSONObject jsonObject=new JSONObject(response);
                int notSubmitSize=jsonObject.getInt("MemberSize");
                for(int i=0;i<notSubmitSize;i++){
                    String memID=jsonObject.getString("memID"+i);
                    if(memID.equals(loginMember.getMemID())){
                        tv_my_notSubmit.setText(jsonObject.getString("count"+i));
                        break;
                    }
                }
            }catch (Exception e){

            }
        }else {
            try {
                JSONObject jsonObject = new JSONObject(response);
                tv_my_notSubmit.setText(jsonObject.getString("notSubmit"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            Log.d("fee관련",response);
            if(TAG.equals("setInfo")){
                setInformationProcess(response);
            }
        }
    }
}
