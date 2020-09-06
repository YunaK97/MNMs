package kr.hongik.mnms.mainscreen.ui.daily;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.Group;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyActivity;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.mainscreen.GroupAdapter;
import kr.hongik.mnms.mainscreen.OnGroupItemClickListener;
import kr.hongik.mnms.mainscreen.OnGroupItemLongClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DailyList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;

    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private Context context;
    private ViewGroup rootView;

    //URLs
    String ip="";

    public DailyList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=container.getContext();

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_daily_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle=getArguments();
        if(bundle!=null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
            ip=bundle.getString("ip");
        }

        //그룹리스트 출력
        groupView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupView();
    }

    private void groupView(){
        String urlDailyGroupInfo="http://"+ip+"/dailyGroupInfo";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlDailyGroupInfo);
        networkTask.setTAG("dailyGroupInfo");

        Map<String, String> params = new HashMap<>();
        params.put("memID",loginMember.getMemID());

        networkTask.execute(params);
    }

    private void outGroup(final Group outGroup){
        String urlDailyOutGroup="http://"+ip+"/OutDGroup";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setURL(urlDailyOutGroup);
        networkTask.setTAG("dailyOutGroup");

        Map<String, String> params = new HashMap<>();
        params.put("memID",loginMember.getMemID());
        params.put("groupName",outGroup.getGroupName());
        params.put("groupID",outGroup.getGID());
        params.put("DID",((DailyGroup)outGroup).getDID());

        networkTask.execute(params);
    }


    private void selectOutGroup(int position){
        final Group outGroup=groupAdapter.getItem(position);
        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.CustomDialog);

        builder.setTitle(outGroup.getGroupName()).setMessage("친구목록에서 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                outGroup(outGroup);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("삭제 취소");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void intoDaily(int position){
        Group item=groupAdapter.getItem(position);
        Intent intent = new Intent(rootView.getContext(), DailyActivity.class);

        intent.putExtra("loginMember",loginMember);
        intent.putExtra("loginMemberAccount",loginMemberAccount);
        intent.putExtra("dailyGroup",item);
        startActivity(intent);
    }

    private void showToast(String data){
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
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
            if(TAG.equals("dailyGroupInfo")){
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()==0){
                        showToast("그룹이 없습니다.");

                    }else{
                        groupMembershiplList= rootView.findViewById(R.id.main_daily_list);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
                        groupMembershiplList.setLayoutManager(layoutManager);

                        groupAdapter=new GroupAdapter();
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject item=jsonArray.getJSONObject(i);
                            String groupname=item.getString("groupName");
                            String gid=item.getString("groupID");
                            String did=item.getString("DID");
                            //String groupTime=item.getString("groupTime");

                            DailyGroup group = new DailyGroup();
                            group.setGroupName(groupname);
                            group.setGID(gid);
                            group.setDID(did);
                            //group.setTime(groupTime);
                            groupAdapter.addItem(group);
                        }

                        groupMembershiplList.setAdapter(groupAdapter);

                        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
                            @Override
                            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                                intoDaily(position);
                            }
                        });

                        groupAdapter.setOnItemLongClickListener(new OnGroupItemLongClickListener() {
                            @Override
                            public void onItemLongClick(GroupAdapter.ViewHolder holder, View view, int position) {
                                selectOutGroup(position);
                                groupView();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("오류 : "+e.toString());
                }
            }
            else if(TAG.equals("dailyOutGroup")){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean("success");
                    if(success) {
                        //삭제 성공여부 확인
                        groupView();
                    }else{
                        showToast("그룹 나가기 실패");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
