package com.example.teamtemplate.newgroupfriend;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.RequestShowFriend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestNewFriend extends StringRequest {
    //new friend -> 친구 id 검색 시
    //해당 id의 사람을 member table에서 검색
    final static private String URL1="http://jennyk97.dothome.co.kr/NewFriend.php";

    //친구 추가 요청
    //해당 친구 id 를 현재 loginMember의 친구로 등록
    final static private String URL2="http://jennyk97.dothome.co.kr/NewFriendAdd.php";

    //요청받은 친구 아이디,이름 출력
    final static private String URL3="http://jennyk97.dothome.co.kr/RequestedFriend.php";

    //선택된 친구 수락/거절
    final static private String URL4="http://jennyk97.dothome.co.kr/RequestedResult.php";

    private Map<String,String> map;

    public RequestNewFriend(String friendID, Response.Listener<String> listener){
        super(Method.POST,URL1,listener,null);
        map=new HashMap<>();
        map.put("memID", friendID);
    }

    public RequestNewFriend(String tag,String memID,String friendID, Response.Listener<String> listener){
        super(Method.POST,URL2,listener,null);
        map=new HashMap<>();
        map.put("memID",memID);
        map.put("friendID", friendID);
    }

    public RequestNewFriend(String tag,String memID,Response.Listener<String> listener){
        super(Method.POST,URL3,listener,null);
        map=new HashMap<>();
        map.put("memID",memID);
    }

    public RequestNewFriend(Member loginMember,ArrayList<Member> selectedFriend,String TAG,Response.Listener<String> listener){
        super(Method.POST,URL4,listener,null);
        map=new HashMap<>();

        map.put("memID",loginMember.getMemID());
        map.put("TAG",TAG);
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
