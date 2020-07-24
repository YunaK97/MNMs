package com.example.teamtemplate;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestMembership extends StringRequest {
    //서버 URL 설정(PHP 파일 연동)
    final static private String URL="http://jennyk97.dothome.co.kr/ListTransaction.php";
    private Map<String,String> map;

    public RequestMembership(String accountNum, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("accountNum", accountNum);
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
