package com.example.teamtemplate;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestShowFriend extends StringRequest {
    //new daily,new membership 때 친구 목록 출력
    final static private String URL="http://jennyk97.dothome.co.kr/IdOverlap.php";
    private Map<String,String> map;

    public RequestShowFriend(String memID, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("memID", memID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
