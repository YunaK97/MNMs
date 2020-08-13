package com.example.teamtemplate.mainscreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Group;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.daily.DailyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DailyList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;
    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private Context context;
    private String TAG_MEMBERSHIP="MEMBERSHIP";
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

        context=container.getContext();

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_daily_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle=getArguments();
        loginMember= (Member) bundle.getSerializable("loginMember");
        loginMemberAccount= (Account) bundle.getSerializable("loginMemberAccount");

        //그룹리스트 출력
        groupView(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupView(rootView);
    }

    protected void groupView(final ViewGroup rootView){

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()==0){
                        showToast("그룹이 없습니다.");

                    }else{
                        groupMembershiplList=(RecyclerView)rootView.findViewById(R.id.main_daily_list);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
                        groupMembershiplList.setLayoutManager(layoutManager);

                        groupAdapter=new GroupAdapter();
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject item=jsonArray.getJSONObject(i);
                            String groupname=item.getString("groupName");
                            String gid=item.getString("groupID");
                            String did=item.getString("DID");

                            Group group = new Group();
                            group.setGroupName(groupname);
                            group.setGid(gid);
                            group.setDid(did);
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
                                groupView(rootView);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("오류 : "+e.toString());
                }
            }
        };
        RequestGroup requestGroup=new RequestGroup("daily",loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(rootView.getContext());
        queue.add(requestGroup);
    }

    protected void outGroup(final Group outGroup){
        showToast(outGroup.getGroupName()+":"+outGroup.getGid() + "나가기 구현중");

        final String url="http://jennyk97.dothome.co.kr/OutGroup.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d("outDaily",response);
                    JSONObject jsonObject=new JSONObject(response);
                    Boolean success=jsonObject.getBoolean("success");
                    if(success) {
                        //삭제 성공여부 확인
                        showToast("그룹 나가기 성공");
                    }else{
                        showToast("그룹 나가기 실패");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("memID",loginMember.getMemID());
                params.put("groupName",outGroup.getGroupName());
                params.put("groupID",outGroup.getGid());
                params.put("DID",outGroup.getDid());
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    protected void selectOutGroup(int position){
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

    protected void intoDaily(int position){
        Group item=groupAdapter.getItem(position);
        Intent intent = new Intent(rootView.getContext(), DailyActivity.class);

        intent.putExtra("loginMember",loginMember);
        intent.putExtra("loginMemberAccount",loginMemberAccount);
        intent.putExtra("dailyGroup",item);
        startActivity(intent);
    }

    protected void showToast(String data){
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }
}
