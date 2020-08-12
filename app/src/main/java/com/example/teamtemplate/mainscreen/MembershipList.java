package com.example.teamtemplate.mainscreen;

import android.content.Context;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Group;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.membership.MembershipActivity;
import com.example.teamtemplate.membership.ui.home.MembershipFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MembershipList extends Fragment {
    private Member loginMember;
    private Account loginMemberAccount;
    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private Context context;
    private String TAG_MEMBERSHIP="MEMBERSHIP";
    private ViewGroup rootView;

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
        context=container.getContext();

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_membership_list, container, false);

        //loginMember,loginMemberAccount 가져오기
        Bundle bundle=getArguments();
        loginMember= (Member) bundle.getSerializable("loginMember");
        loginMemberAccount= (Account) bundle.getSerializable("loginMemberAccount");

        //그룹리스트 출력
        groupView(rootView);
        //tmpGroupView(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupView(rootView);
    }

    public void groupView(final ViewGroup rootView){

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("membershipList",response);
                    JSONArray jsonArray=new JSONArray(response);
                    if(jsonArray.length()==0){
                        showToast("그룹이 없습니다.");

                    }else{
                        groupMembershiplList=(RecyclerView)rootView.findViewById(R.id.main_membership_list);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
                        groupMembershiplList.setLayoutManager(layoutManager);

                        groupAdapter=new GroupAdapter();

                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject item=jsonArray.getJSONObject(i);
                            String groupname=item.getString("groupName");
                            String gid=item.getString("groupID");
                            String mid=item.getString("MID");

                            Group group = new Group();
                            group.setGroupName(groupname);
                            group.setGid(gid);
                            group.setMid(mid);
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
                                intent.putExtra("membershipGroup",item);

                                startActivity(intent);
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("loginMember", loginMember);
                                bundle.putSerializable("loginMemberAccount", loginMemberAccount);

                                MembershipFragment membershipFragment=new MembershipFragment();
                                membershipFragment.setArguments(bundle);

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
