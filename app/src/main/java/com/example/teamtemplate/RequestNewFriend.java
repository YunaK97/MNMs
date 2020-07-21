package com.example.teamtemplate;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestNewFriend extends StringRequest {
    final static private String URL="http://jennyk97.dothome.co.kr/NewFriend.php";
    private Map<String,String> map;

    public RequestNewFriend(String friendID, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("friendID", friendID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
