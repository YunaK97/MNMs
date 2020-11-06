package kr.hongik.mnms.mainscreen.ui.daily;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.Account;
import kr.hongik.mnms.Group;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.daily.DailyActivity;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.mainscreen.GroupAdapter;
import kr.hongik.mnms.mainscreen.MainMenuActivity;
import kr.hongik.mnms.mainscreen.OnGroupItemClickListener;
import kr.hongik.mnms.mainscreen.OnGroupItemLongClickListener;

public class DailyList extends Fragment {
    //돈 정산 안끝났으면 데일리 못나감!

    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private RecyclerView rvMainDailyList;
    private GroupAdapter groupAdapter;
    private Context context;
    private ViewGroup rootView;

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

        context = container.getContext();

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_daily_list, container, false);

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
        String urlDailyGroupInfo = "http://" + loginMember.getIp() + "/member/dailyGroupList";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDailyGroupInfo);
        networkTask.setTAG("dailyGroupInfo");

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);
    }

    private void selectOutGroup(int position) {
        //돈정산 끝나야 나갈 수 있음
        final Group outGroup = groupAdapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(outGroup.getGroupName()).setMessage("친구목록에서 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dailyOutGroup(outGroup);
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

    private void intoDaily(int position) {
        DailyGroup item = (DailyGroup) groupAdapter.getItem(position);
        Intent intent = new Intent(rootView.getContext(), DailyActivity.class);

        intent.putExtra("loginMember", loginMember);
        intent.putExtra("loginMemberAccount", loginMemberAccount);
        intent.putExtra("dailyGroup", item);
        startActivity(intent);
    }

    private void dailyOutGroup(Group group){
        String urlDailyOutGroup = "http://" + loginMember.getIp() + "/daily/deleteDailygroup";
        NetworkTask networkTask=new NetworkTask();
        networkTask.setTAG("dailyOutGroup");
        networkTask.setURL(urlDailyOutGroup);

        Map<String,String> params=new HashMap<>();
        params.put("memID",loginMember.getMemID());
        params.put("GID",group.getGID()+"");

        networkTask.execute(params);
    }

    private void dailyGroupInfoProcess(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            int dailyGroupSize=Integer.parseInt(jsonObject.getString("dailyGroupSize"));
            if (dailyGroupSize == 0) return;
            rvMainDailyList = rootView.findViewById(R.id.rvMainDailyList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
            rvMainDailyList.setLayoutManager(layoutManager);

            groupAdapter = new GroupAdapter();
            for (int i = 0; i < dailyGroupSize; i++) {
                String groupname = jsonObject.getString("groupName"+i);
                int gid = Integer.parseInt(jsonObject.getString("GID"+i));

                DailyGroup group = new DailyGroup();
                group.setGroupName(groupname);
                group.setGID(gid);
                groupAdapter.addItem(group);
            }

            rvMainDailyList.setAdapter(groupAdapter);

            groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
                @Override
                public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                    intoDaily(position);
                }
            });

            groupAdapter.setOnItemLongClickListener(new OnGroupItemLongClickListener() {
                @Override
                public void onItemLongClick(GroupAdapter.ViewHolder holder, View view, int position) {
                    showToast("아직 구현중");
                    //selectOutGroup(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("오류 : " + e.toString());
        }
    }

    private void dailyOutGroupProcess(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                //삭제 성공여부 확인
                ((MainMenuActivity)getActivity()).refresh();
            } else {
                showToast("그룹 나가기 실패");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showToast(String data) {
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
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
            Log.d(TAG,response);
            if (TAG.equals("dailyGroupInfo")) {
                dailyGroupInfoProcess(response);
            } else if (TAG.equals("dailyOutGroup")) {
                dailyOutGroupProcess(response);
            }
        }
    }
}
