package com.hongik.mnms;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestLogin extends StringRequest {
    //서버 URL 설정(PHP 파일 연동)
    final static private String URL="http://jennyk97.dothome.co.kr/Login.php";
    private Map<String,String> map;

    public RequestLogin(String memID, String memPW, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("memID", memID);
        map.put("memPW", memPW);
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
