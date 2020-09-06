package kr.hongik.mnms.newgroupfriend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.MemberAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.R;

public class NewDailyActivity extends AppCompatActivity {
    private Member loginMember;

    //layouts
    private MemberAdapter memberAdapter;
    private ArrayList<Member> arrayList;
    private RecyclerView friend_list;

    //URLs
    public String ip;

    //variable
    private ArrayList<Member> selectedMember;
    private ArrayList<String> groupName;
    private String TAG_SUCCESS="success";
    private String daily_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily);

        Intent intent=getIntent();
        loginMember=(Member)intent.getSerializableExtra("loginMember");
        ip=intent.getStringExtra("ip");

        //친구 가져와서 출력
        showFriend();
        //기존 그룹 이름 가져오기
        groupNameList();

        //membership 생성 버튼 클릭

        Button btn_new_daily=findViewById(R.id.btn_new_daily);
        btn_new_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDaily();
            }
        });
    }

    protected void NewDaily(){
        selectedMember=new ArrayList<>();
        daily_name=((TextView)findViewById(R.id.daily_name)).getText().toString();
        if(daily_name==null){
            showToast("이러시면 안됨니다 고갱님 정보를 쓰세욥");
        } else
        {
            boolean overlap=true;
            for(String s:groupName){
                if(s.equals(daily_name)){
                    overlap=false;
                }
            }
            if(!overlap){
                showToast("이미 존재하는 그룹이름입니다.");
            }else if(overlap) {
                for (int i = 0; i < memberAdapter.getItemCount(); i++) {
                    if (memberAdapter.getItem(i).isChecked()) {
                        Member member = new Member();
                        member.setMemID((memberAdapter.getItem(i)).getMemID());
                        selectedMember.add(member);
                    }
                }

                String urlNewDaily= "http://"+ip+"/newDaily";

                NetworkTask networkTask=new NetworkTask();
                networkTask.setURL(urlNewDaily);
                networkTask.setTAG("newDaily");

                Map<String, String> params = new HashMap<>();
                params.put("memID", loginMember.getMemID());
                params.put("memName", loginMember.getMemName());
                params.put("dailyName", daily_name);
                try {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < selectedMember.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("memID", selectedMember.get(i).getMemID());
                        jsonArray.put(jsonObject);
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("memID", loginMember.getMemID());
                    jsonArray.put(jsonObject);
                    params.put("friend", jsonArray.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                networkTask.execute(params);
            }
        }
    }


    private void groupNameList(){
        String urlDailyGroupInfo="http://"+ip+"/dailyGroupInfo";

        groupName=new ArrayList<>();

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlDailyGroupInfo);
        networkTask.setTAG("dailyGroupInfo");

        Map<String, String> params = new HashMap<>();
        params.put("memID",loginMember.getMemID());

        networkTask.execute(params);
    }


    protected void showFriend(){
        String urlShowFriend="http://"+ip+"/showFriend";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlShowFriend);
        networkTask.setTAG("showFriend");

        Map<String, String> params = new HashMap<>();
        params.put("memID",loginMember.getMemID());

        networkTask.execute(params);
    }

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        String TAG;

        void setURL(String url){
            this.url=url;
        }
        void setTAG(String TAG){
            this.TAG=TAG;
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
            Log.d("dailyLog",response);
            if(TAG.equals("newDaily")){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(TAG_SUCCESS);
                    if (success) {
                        finish();
                    } else {
                        showToast("daily생성 실패!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(TAG.equals("dailyGroupInfo")){
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()==0){
                        showToast("그룹이 없습니다.");

                    }else{
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject item=jsonArray.getJSONObject(i);
                            String groupname=item.getString("groupName");
                            groupName.add(groupname);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("오류 : "+e.toString());
                }
            }
            else if(TAG.equals("showFriend")){
                try {
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        showToast("친구가 없습니다.");
                        return;
                    }

                    friend_list = findViewById(R.id.daily_selected_friend);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(NewDailyActivity.this, LinearLayoutManager.VERTICAL, false);
                    friend_list.setLayoutManager(layoutManager);
                    memberAdapter = new MemberAdapter();

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String friendId = item.getString("memID");
                        String friendName = item.getString("memName");

                        Member member=new Member();
                        member.setMemName(friendName);
                        member.setMemID(friendId);
                        memberAdapter.addItem(member);
                    }

                    friend_list.setAdapter(memberAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
