package com.example.teamtemplate.newgroupfriend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestNewFriend extends StringRequest {
    //new friend -> 친구 id 검색 시
    //해당 id의 사람을 member table에서 검색
    final static private String URL1="http://jennyk97.dothome.co.kr/NewFriend.php";

    //친구 추가 요청
    //해당 친구 id 를 현재 loginMember의 친구로 등록
    final static private String URL2="http://jennyk97.dothome.co.kr/NewFriendAdd.php";
    private Map<String,String> map;

    public RequestNewFriend(String friendID, Response.Listener<String> listener){
        super(Method.POST,URL1,listener,null);
        map=new HashMap<>();
        map.put("memID", friendID);
    }

    public RequestNewFriend(String tag,String memID,String friendID, Response.Listener<String> listener){
        super(Method.POST,URL2,listener,null);
        System.out.println(tag+memID+friendID);
        map=new HashMap<>();
        map.put("memID",memID);
        map.put("friendID", friendID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
