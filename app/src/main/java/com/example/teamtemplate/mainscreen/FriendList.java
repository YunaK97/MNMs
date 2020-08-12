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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.RequestShowFriend;
import com.example.teamtemplate.membership.MembershipActivity;
import com.example.teamtemplate.transaction.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FriendList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FriendListAdapter friendListAdapter;
    private Member loginMember;
    private String TAG_SUCCESS="success";
    private Context context;
    private ViewGroup rootView;

    public FriendList() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        showFriend(rootView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=container.getContext();

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_friend_list, container, false);

        Bundle bundle=getArguments();
        loginMember= (Member) bundle.getSerializable("loginMember");

        showFriend(rootView);

        return rootView;
    }
    public void showFriend(final ViewGroup rootView){
        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("showFriend",response);
                    JSONArray jsonArray=new JSONArray(response);

                    if(jsonArray.length()==0){
                        //showToast("친구가 없습니다.");
                        return;
                    }

                    RecyclerView friend_list = rootView.findViewById(R.id.main_friend_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);

                    friend_list.setLayoutManager(layoutManager);

                    friendListAdapter = new FriendListAdapter();

                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String friendId = item.getString("memID");
                        String friendName = item.getString("memName");

                        Member member=new Member();
                        member.setMemName(friendName);
                        member.setMemID(friendId);
                        friendListAdapter.addItem(member);
                    }
                    friend_list.setAdapter(friendListAdapter);
                    friendListAdapter.setOnItemLongClickListener(new OnFriendItemLongClickListener() {
                        @Override
                        public void onItemLongClick(FriendListAdapter.ViewHolder holder, View view, int position) {
                            final Member delMember=friendListAdapter.getItem(position);
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);

                            builder.setTitle(delMember.getMemID()).setMessage("친구목록에서 삭제하시겠습니까?");
                            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showToast("삭제");
                                    deleteFriend(delMember.getMemID());
                                    showFriend(rootView);
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
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestShowFriend requestShowFriend=new RequestShowFriend(loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(rootView.getContext());
        queue.add(requestShowFriend);
    }

    public void deleteFriend(final String delMemberId){
        final String url="http://jennyk97.dothome.co.kr/DeleteFriend.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Log.d("honey",response);
                    JSONObject jsonObject=new JSONObject(response);
                    Boolean success=jsonObject.getBoolean(TAG_SUCCESS);
                    if(success) {
                        //삭제 성공여부 확인
                        showToast("친구 삭제 성공");
                    }else{
                        showToast("친구 삭제 실패");
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
                params.put("friendID", delMemberId);
                params.put("memID",loginMember.getMemID());
                return params;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    public void showToast(String data){
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }
}
