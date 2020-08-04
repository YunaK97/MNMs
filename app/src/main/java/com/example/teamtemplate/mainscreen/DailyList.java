package com.example.teamtemplate.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Group;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.RequestGroup;
import com.example.teamtemplate.membership.MembershipActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DailyList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;
    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private Context context;
    private String TAG_MEMBERSHIP="MEMBERSHIP";

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

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_daily_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle=getArguments();
        loginMember= (Member) bundle.getSerializable("loginMember");
        loginMemberAccount= (Account) bundle.getSerializable("loginMemberAccount");

        //그룹리스트 출력
        //groupView(rootView);
        tmpGroupView(rootView);

        return rootView;
    }

    public void tmpGroupView(final ViewGroup rootView){
        groupMembershiplList=rootView.findViewById(R.id.main_daily_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
        groupMembershiplList.setLayoutManager(layoutManager);

        groupAdapter=new GroupAdapter();

        for(int i=0;i<10;i++){
            Group group = new Group();
            group.setGroupName("daily : "+i);
            groupAdapter.addItem(group);
        }
        groupMembershiplList.setAdapter(groupAdapter);

        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
            @Override
            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                Group item=groupAdapter.getItem(position);
                showToast("아이템 선택됨 : "+ item.getGroupName());

                Intent intent = new Intent(rootView.getContext(), MembershipActivity.class);

                intent.putExtra("loginMember",loginMember);
                intent.putExtra("loginMemberAccount",loginMemberAccount);
                intent.putExtra("gname",item.getGroupName());
                intent.putExtra("gid",item.getGid());

                startActivity(intent);
            }
        });

    }

    public void groupView(final ViewGroup rootView){
        groupMembershiplList=(RecyclerView)rootView.findViewById(R.id.main_daily_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
        groupMembershiplList.setLayoutManager(layoutManager);

        groupAdapter=new GroupAdapter();

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()==0){
                        showToast("그룹이 없습니다.");

                    }else{
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject item=jsonArray.getJSONObject(i);
                            String groupname=item.getString("groupName");
                            String gid=item.getString("groupID");

                            showToast(groupname);

                            Group group = new Group();
                            group.setGroupName(groupname);
                            group.setGid(gid);
                            groupAdapter.addItem(group);
                        }

                        groupMembershiplList.setAdapter(groupAdapter);

                        groupAdapter.setOnItemClickListener(new OnGroupItemClickListener() {
                            @Override
                            public void onItemClick(GroupAdapter.ViewHolder holder, View view, int position) {
                                Group item=groupAdapter.getItem(position);
                                showToast("아이템 선택됨 : "+ item.getGroupName());

                                Intent intent = new Intent(rootView.getContext(), MembershipActivity.class);

                                intent.putExtra("loginMember",loginMember);
                                intent.putExtra("loginMemberAccount",loginMemberAccount);
                                intent.putExtra("gname",item.getGroupName());
                                intent.putExtra("gid",item.getGid());
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("오류 : "+e.toString());
                }
            }
        };
        RequestGroup requestGroup=new RequestGroup(loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(rootView.getContext());
        queue.add(requestGroup);
    }

    public void showToast(String data){
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }
}
