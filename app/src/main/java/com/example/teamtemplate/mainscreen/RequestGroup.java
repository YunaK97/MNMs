package com.example.teamtemplate.mainscreen;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestGroup extends StringRequest {
    //그룹을 출력 -> membership,daily 2개이므로 request를 하나 더 생성해야함 ->(미구현)
    //memID에 해당하는 그룹을 출력
    final static private String URL="http://jennyk97.dothome.co.kr/MembergroupInfo.php";
    final static private String URL2="";
    private Map<String,String> map;

    public RequestGroup(String memID, Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("memID", memID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
