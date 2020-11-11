package kr.hongik.mnms.daily.ui.mem;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.Transaction;
import kr.hongik.mnms.TransactionAdapter;
import kr.hongik.mnms.daily.DailyGroup;
import kr.hongik.mnms.mainscreen.ui.friend.FriendListAdapter;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemClickListener;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemLongClickListener;


public class DailyMemFragment extends Fragment {
    private DailyGroup dailyGroup;
    private Member loginMember;
    private ArrayList<Member> memberArrayList;

    //layouts
    private RecyclerView rvDailyMemberList;
    private FriendListAdapter memberAdapter;

    private Context context;
    private ViewGroup rootView;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_daily_mem, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            dailyGroup = (DailyGroup) bundle.getSerializable("dailyGroup");
            loginMember = (Member) bundle.getSerializable("loginMember");
            memberArrayList=(ArrayList<Member>)bundle.getSerializable("memberArrayList");

            showMember();
        }
        return rootView;
    }

    private void showMember() {
        rvDailyMemberList = rootView.findViewById(R.id.rvDailyMemberList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        rvDailyMemberList.setLayoutManager(layoutManager);
        memberAdapter = new FriendListAdapter();
        memberAdapter.setItems(memberArrayList);
        rvDailyMemberList.setAdapter(memberAdapter);
    }

    private class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
        protected String url;
        protected String TAG;

        void setURL(String url) {
            this.url = url;
        }

        void setTAG(String TAG) {
            this.TAG = TAG;
        }

        @Override
        protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터

            // Http 요청 준비 작업
            HttpClient.Builder http = new HttpClient.Builder("POST", url);

            // Parameter 를 전송한다.
            http.addAllParameters(maps[0]);

            //Http 요청 전송
            HttpClient post = http.create();
            post.request();
            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();
            // 응답 본문 가져오기

            return post.getBody();
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d(TAG, response);

        }
    }


}