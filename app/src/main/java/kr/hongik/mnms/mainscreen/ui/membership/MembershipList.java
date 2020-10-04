package kr.hongik.mnms.mainscreen.ui.membership;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Group;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.GroupAdapter;
import kr.hongik.mnms.mainscreen.OnGroupItemClickListener;
import kr.hongik.mnms.mainscreen.OnGroupItemLongClickListener;
import kr.hongik.mnms.membership.MembershipActivity;
import kr.hongik.mnms.membership.MembershipGroup;
import kr.hongik.mnms.membership.ui.home.NewFeeActivity;

public class MembershipList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;
    private Group outGroupNum;

    //layout
    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private Context context;
    private ViewGroup rootView;

    //variables
    private ArrayList<Integer> GIDArray;

    public MembershipList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_membership_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle = getArguments();
        if (bundle != null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            loginMemberAccount = (Account) bundle.getSerializable("loginMemberAccount");
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


    private void groupView() {
        //회원이 가입한 멤버십 그룹을 출력
        //memID 전송
        //가입한 멤버십들의 GID,groupName들을 받아옴
        String urlMemberGroupInfo = "http://" + loginMember.getIp() + "/member/membershipGroupList";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlMemberGroupInfo);
        networkTask.setTAG("memberGroupInfo");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);
    }

    private void intoMembership(int position) {
        Group item = groupAdapter.getItem(position);

        Intent intent = new Intent(rootView.getContext(), MembershipActivity.class);

        intent.putExtra("loginMember", loginMember);
        intent.putExtra("loginMemberAccount", loginMemberAccount);
        intent.putExtra("membershipGroup", item);

        startActivity(intent);
    }

    private void outGroup() {
        //현재 멤버가 멤버십그룹의 회장이 아닌경우 멤버십을 나갈 수 있다.
        //memID,GID를 전송하면
        //멤버십을 나간후 성공여부를 받음
        String urlOutMGroup = "http://" + loginMember.getIp() + "/membership/deleteMembershipgroup";
        showToast(urlOutMGroup);
        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlOutMGroup);
        networkTask.setTAG("membershipOutGroup");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("GID", outGroupNum.getGID()+"");

        networkTask.execute(params);
    }

    private void selectOutGroup(int position) {
        outGroupNum = groupAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(outGroupNum.getGroupName()).setMessage("그룹목록에서 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkPresident(outGroupNum);
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

    private void checkPresident(Group group){
        //멤버십 회장은 멤버십을 나갈수없다
        //GID과 멤버ID 보내면
        //해당 GID의 president가 memID라면 false를 받아야함
        String urlCheckPresident="http://"+loginMember.getIp()+"/membership/checkPresident";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("checkPresident");
        networkTask.setURL(urlCheckPresident);

        Map<String,String> params=new HashMap<>();
        params.put("memID",loginMember.getMemID());
        params.put("GID",group.getGID()+"");

        networkTask.execute(params);
    }

    private void checkMembershipSubmit(){
        String urlCheckSubmit="http://"+loginMember.getIp()+"/membership/check";

        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("checkSubmit");
        networkTask.setURL(urlCheckSubmit);

        Map<String,String> params=new HashMap<>();
        params.put("memID",loginMember.getMemID());
        params.put("GIDsize",GIDArray.size()+"");

        for(int i=0;i<GIDArray.size();i++){
            params.put("GID"+i,GIDArray.get(i)+"");
        }

        networkTask.execute(params);
    }


    private void showToast(String data) {
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }

    private void memberGroupInfoProcess(String response) {
        try {
            JSONObject jsonObject=new JSONObject(response);
            int membershipGroupSize=Integer.parseInt(jsonObject.getString("membershipGroupSize"));
            if (membershipGroupSize == 0) return;
            groupMembershiplList = rootView.findViewById(R.id.main_membership_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
            groupMembershiplList.setLayoutManager(layoutManager);

            groupAdapter = new GroupAdapter();
            GIDArray=new ArrayList<>();
            for (int i = 0; i < membershipGroupSize; i++) {
                String groupname = jsonObject.getString("groupName"+i);
                int gid = Integer.parseInt(jsonObject.getString("GID"+i));
                //String notSubmit=item.getString("notSubmit");
                //String groupTime=item.getString("groupTime");

                MembershipGroup group = new MembershipGroup();
                group.setGroupName(groupname);
                group.setGID(gid);
                GIDArray.add(gid);
                groupAdapter.addItem(group);
            }

            groupMembershiplList.setAdapter(groupAdapter);

            groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
                @Override
                public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                    intoMembership(position);
                }
            });

            groupAdapter.setOnItemLongClickListener(new OnGroupItemLongClickListener() {
                @Override
                public void onItemLongClick(GroupAdapter.ViewHolder holder, View view, int position) {
                    selectOutGroup(position);
                    groupView();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("오류 : " + e.toString());
        }

        checkMembershipSubmit();
    }

    private void membershipOutGroupProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                //삭제 성공여부 확인
                groupView();
            } else {
                showToast("그룹 나가기 실패");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkMembershipSubmitProcess(String response){
        Log.d("checkFee",response);
//        try {
//            JSONObject jsonObject=new JSONObject(response);
//            int size=jsonObject.getInt("size");
//            for(int i=0;i<size;i++){
//                int GID=jsonObject.getInt("GID"+i);
//                for(int j=0;j<groupAdapter.getItemCount();j++){
//                    if(GID==groupAdapter.getItem(j).getGID()){
//
//                        TextView textView=rootView.findViewById(R.id.group_name);
//                        textView.setTextColor(Color.RED);
//                    }
//                }
//            }
//        }catch (Exception e){
//
//        }
    }


    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        String TAG;

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
            if (TAG.equals("memberGroupInfo")) {
                memberGroupInfoProcess(response);
            } else if (TAG.equals("membershipOutGroup")) {
                membershipOutGroupProcess(response);
            }else if(TAG.equals("checkPresident")){
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success=jsonObject.getBoolean("success");
                    if(!success){
                        showToast("회장은 나갈 수 없습니다.");
                    }else{
                        outGroup();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(TAG.equals("checkSubmit")){
                checkMembershipSubmitProcess(response);
            }
        }
    }
}
