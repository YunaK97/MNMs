package com.example.teamtemplate;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestNewMembership extends StringRequest {
    //new membership 때
    //매개변수 더 추가해야함 - 아직 미완성
    final static private String URL="http://jennyk97.dothome.co.kr/NewMembership.php";
    private Map<String,String> map;

    public RequestNewMembership(String memID, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("memID", memID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
