package com.example.teamtemplate;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestRegister extends StringRequest {
    //서버 URL 설정(PHP 파일 연동)
    final static private String URL="http://jennyk97.dothome.co.kr/Register2.php";
    private Map<String,String> map;

    public RequestRegister(String memID, String memPW, String memName, String memEmail,Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        //map.put("member",member);
        map.put("memID", memID);
        map.put("memPW", memPW);
        map.put("memName", memName);
        map.put("memEmail",memEmail);
        System.out.println(memEmail);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
