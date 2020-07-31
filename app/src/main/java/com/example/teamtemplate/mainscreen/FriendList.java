package com.example.teamtemplate.mainscreen;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.RequestShowFriend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FriendListAdapter friendListAdapter;
    private Member loginMember;
    private String TAG_SUCCESS="success";
    private Context context;

    public FriendList() {
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

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_friend_list, container, false);

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
                    String TAG="response";
                    Log.d(TAG,response);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestShowFriend requestShowFriend=new RequestShowFriend(loginMember.getMemID(),responseListener);
        RequestQueue queue= Volley.newRequestQueue(rootView.getContext());
        queue.add(requestShowFriend);
    }

    public void showToast(String data){
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }
}
