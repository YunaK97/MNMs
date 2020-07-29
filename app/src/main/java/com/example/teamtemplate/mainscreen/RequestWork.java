package com.example.teamtemplate.mainscreen;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.teamtemplate.Account;
import com.example.teamtemplate.Member;

import java.util.HashMap;
import java.util.Map;

public class RequestWork extends StringRequest {
    //login 진행
    //id,pw를 전송하여 해당 member가 존재하는지 확인
    //final static private String URL="http://jennyk97.dothome.co.kr/Login.php";
    final static private String URL_Login="http://jennyk97.dothome.co.kr/Login.php";
    //signin 진행
    //가입 멤버,계좌 정보 전송
    final static private String URL_Register="http://jennyk97.dothome.co.kr/Register.php";
    //겹치는 id가 있는지 확인
    final static private String URL_Overlap="http://jennyk97.dothome.co.kr/IdOverlap.php";
    private Map<String,String> map;

    //overlap
    public RequestWork(String memID, Response.Listener<String> listener){
        super(Method.POST,URL_Overlap,listener,null);
        map=new HashMap<>();
        map.put("memID", memID);
    }

    //login
    public RequestWork(String memID, String memPW, Response.Listener<String> listener){
        super(Method.POST,URL_Login,listener,null);
        map=new HashMap<>();
        map.put("memID", memID);
        map.put("memPW", memPW);
    }

    //signin
    public RequestWork(Member signinMember, Account signinMemberAccount, Response.Listener<String> listener){
        super(Method.POST,URL_Register,listener,null);

        map=new HashMap<>();
        map.put("memID", signinMember.getMemID());
        map.put("memPW", signinMember.getMemPW());
        map.put("memName", signinMember.getMemName());
        map.put("memEmail",signinMember.getMemEmail());

        map.put("accountBank",signinMemberAccount.getAccountBank());
        map.put("accountBalance",signinMemberAccount.getAccountBalance()+"");
        map.put("accountNum",signinMemberAccount.getAccountNum());
        map.put("accountPassword",signinMemberAccount.getAccountPassword());
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
