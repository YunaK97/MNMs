package kr.hongik.mnms.membership.ui.manage;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.ui.friend.FriendListAdapter;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemClickListener;
import kr.hongik.mnms.mainscreen.ui.friend.OnFriendItemLongClickListener;
import kr.hongik.mnms.membership.MembershipGroup;


public class MembershipMemFragment extends Fragment {
    //회원내역 보는 곳
    /*
     * 1.납입일 다음날~다음 납입일 이전 까지 회비 제출자는 이름 옆에 (1)로 표시 (DB에서)
     * 2.납입 마감 버튼 클릭 시
     * 3.미납횟수 초과시 자동 탈퇴
     *   회장만이 멤버십 카운트 조정 가능 - 멤버별 미납횟수 받아오기
     * */

    private Member loginMember;
    private MembershipGroup membershipGroup;
    private ArrayList<Member> memberArrayList;

    //Layouts
    private RecyclerView memberList;
    private MemberListAdapter presidentMemberAdapter;
    private FriendListAdapter normalMemberAdapter;

    private Context context;
    private ViewGroup rootView;

    public MembershipMemFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getContext();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_membership_mem, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            loginMember = (Member) bundle.getSerializable("loginMember");
            membershipGroup = (MembershipGroup) bundle.getSerializable("membershipGroup");
            memberArrayList = (ArrayList<Member>) bundle.get("memberArrayList");

            if (loginMember.getMemID().equals(membershipGroup.getPresident())) {
                submitLateFee();
            } else {
                showMember();
            }

        }

        return rootView;
    }

    private void showMember() {
        memberList = rootView.findViewById(R.id.RV_membership_member_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        memberList.setLayoutManager(layoutManager);
        normalMemberAdapter = new FriendListAdapter();
        normalMemberAdapter.setItems(memberArrayList);
        memberList.setAdapter(normalMemberAdapter);
    }

    private void selectDelMember(final int position) {
        //미납횟수 변경하능하게 하기
        //멤버 삭제 기능
        final EditText edittext = new EditText(rootView.getContext());
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        edittext.setBackground(ContextCompat.getDrawable(context, R.drawable.sidecustom_round));
        final Member selMember = presidentMemberAdapter.getItem(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext(), R.style.CustomDialog);
        builder.setTitle(selMember.getMemName());
        builder.setMessage("미납횟수 변경");
        builder.setView(edittext);
        final AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //개인적으로 회비 냄
                        //회비 낸 사람 이름, (미납 횟수 - 바꾼 미납 횟수)*회비 = 회장에게 준 회비 라고 가정
                        //미납횟수가 증가하는 방향이면 트랜잭션은 어케 되나?
                        int newcnt = Integer.parseInt(edittext.getText().toString());
                        if (newcnt < 0/*미납횟수*/) {
                            showToast("잘못된 숫자입니다.");

                        } else {
                            showToast(newcnt + "");

                            int submitFee = membershipGroup.getFee() * (/*미납횟수*/-newcnt);
                        }
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        builder.setNeutralButton("삭제",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context, R.style.CustomDialog);

                        builder2.setTitle(selMember.getMemName()).setMessage(selMember.getMemName() + "삭제하시겠습니까?");

                        final AlertDialog alertDialog2 = builder2.create();
                        alertDialog2.show();

                        builder2.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteMember(selMember.getMemID());
                            }
                        });
                        builder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog2.dismiss();
                            }
                        });

                    }
                });
        builder.show();
    }

    private void submitLateFee() {
        String urlFeeLateMember = "http://" + loginMember.getIp() + "/membership/notSubmit";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("submitLateFee");
        networkTask.setURL(urlFeeLateMember);

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());
        params.put("MID", membershipGroup.getMID() + "");
        params.put("GID", membershipGroup.getGID() + "");

        networkTask.execute(params);
    }

    private void submitLateFeeProcess(String response) {
        //회장만 미납자 볼수있게해야함
        memberList = rootView.findViewById(R.id.RV_membership_member_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        memberList.setLayoutManager(layoutManager);
        presidentMemberAdapter = new MemberListAdapter();

        Map<String, String> submitList = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            int membershipMemberSize = Integer.parseInt(jsonObject.getString("MemberSize"));

            for (int i = 0; i < membershipMemberSize; i++) {
                String memberID = jsonObject.getString("memID" + i);
                String notSubmit = jsonObject.getString("count" + i);

                submitList.put(memberID, notSubmit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //멤버십 애들이랑 미납자 연결
        //presidentMemberAdapter에 나중에 set

        for (int i = 0; i < memberArrayList.size(); i++) {
            Member member=memberArrayList.get(i);
            MembershipMember membershipMember=new MembershipMember();
            membershipMember.setMemID(member.getMemID());
            membershipMember.setMemName(member.getMemName());
            String notsubmit = submitList.get(membershipMember.getMemID());
            membershipMember.setNotSubmit(Integer.parseInt(notsubmit));
            presidentMemberAdapter.addItem(membershipMember);
        }

        memberList.setAdapter(presidentMemberAdapter);
    }

    private void deleteMember(String delMemberId) {
        //멤버십에서 회원삭제할것임
        //MID,GID,삭제할 회원memID를 보냄
        //삭제 성공 여부를 받아야함
        String urlDeleteMember = "http://" + loginMember.getIp() + "/membership/deleteMember";

        NetworkTask networkTask = new NetworkTask();
        networkTask.setURL(urlDeleteMember);
        networkTask.setTAG("delMem");

        Map<String, String> params = new HashMap<>();
        params.put("memID", delMemberId);
        params.put("GID", membershipGroup.getGID() + "");
        params.put("MID", membershipGroup.getMID() + "");

        networkTask.execute(params);
    }

    private void showToast(String data) {
        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
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
            if (TAG.equals("delMem")) {

            } else if (TAG.equals("submitLateFee")) {
                submitLateFeeProcess(response);
            }
        }
    }

    public class MembershipMember extends Member {
        private int notSubmit;

        public int getNotSubmit() {
            return notSubmit;
        }

        public void setNotSubmit(int notSubmit) {
            this.notSubmit = notSubmit;
        }
    }

}
