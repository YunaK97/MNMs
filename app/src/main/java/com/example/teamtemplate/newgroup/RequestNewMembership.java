package com.example.teamtemplate.newgroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.teamtemplate.Member;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestNewMembership extends StringRequest {
    //new membership 때
    //매개변수 더 추가해야함 - 아직 미완성
    final static private String URL="http://jennyk97.dothome.co.kr/NewMembership.php";
    private Map<String,String> map;

    public RequestNewMembership(Member loginMember, ArrayList<Member> selectedFriend,String membershipName,String membershipMoney,Response.Listener<String> listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("memID", loginMember.getMemID());
        map.put("memName",loginMember.getMemName());
        map.put("membershipName",membershipName);
        map.put("membershipMoney",membershipMoney);
        try {
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<selectedFriend.size();i++){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("memID",selectedFriend.get(i).getMemID());
                jsonObject.put("memName", selectedFriend.get(i).getMemName());
                jsonArray.put(jsonObject);
            }
            map.put("friend",jsonArray.toString());
        }catch (Exception e){

        }

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
