package kr.hongik.mnms.mainscreen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.HttpClient;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.NetworkTask;
import kr.hongik.mnms.ProgressDialog;
import kr.hongik.mnms.R;
import kr.hongik.mnms.firstscreen.MainActivity;
import kr.hongik.mnms.mainscreen.ui.daily.DailyList;
import kr.hongik.mnms.mainscreen.ui.friend.FriendList;
import kr.hongik.mnms.mainscreen.ui.membership.MembershipList;
import kr.hongik.mnms.mainscreen.ui.settings.SettingsActivity;
import kr.hongik.mnms.mainscreen.ui.transaction.TransactionList;
import kr.hongik.mnms.membership.MembershipGroup;
import kr.hongik.mnms.newprocesses.NewFeeActivity;
import kr.hongik.mnms.newprocesses.NewDailyActivity;
import kr.hongik.mnms.newprocesses.NewFriendActivity;
import kr.hongik.mnms.newprocesses.NewMembershipActivity;


public class MainMenuActivity extends AppCompatActivity {
    private Member loginMember;
    private Account loginMemberAccount;

    //layouts
    private ActionBar actionBar;
    private TextView tvName, tvAccountNum;
    private ImageButton btnTransaction, btnMembership, btnDaily, btnFriendList;
    private ViewPager vpMainList;
    public MyPagerAdapter adapter;
    private TransactionList transactionList;
    private FriendList friendList;
    private DailyList dailyList;
    private MembershipList membershipList;
    private ProgressDialog progressDialog;


    //variables
    public int TAG_LOGOUT = 322;
    private static String CHANNEL_ID = "channel1", CHANEL_NAME = "Channel1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //id찾기

        progressDialog=new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vpMainList = findViewById(R.id.vpMainList);
        tvName = findViewById(R.id.tvName);
        tvAccountNum = findViewById(R.id.tvAccountNum);
        btnTransaction = findViewById(R.id.btnTransaction);
        btnMembership = findViewById(R.id.btnMembership);
        btnDaily = findViewById(R.id.btnDaily);
        btnFriendList = findViewById(R.id.btnFriendList);

        //mainSwipeLayout.setOnRefreshListener(this);

        //액션바
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_mainmenu);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_USE_LOGO);

        //로그인 정보 가져오기
        Intent intent = getIntent();
        loginMember = (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount = (Account) intent.getSerializableExtra("loginMemberAccount");

        //송금내역,membership,daily pager
        vpMainList.setOffscreenPageLimit(4);

        adapter = new MyPagerAdapter(getSupportFragmentManager());

         transactionList = new TransactionList();
        adapter.addItem(transactionList);
         membershipList = new MembershipList();
        adapter.addItem(membershipList);
         dailyList = new DailyList();
        adapter.addItem(dailyList);
         friendList = new FriendList();
        adapter.addItem(friendList);

        vpMainList.setAdapter(adapter);
        btnTransaction.setSelected(true);
        vpMainList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Bundle bundle = new Bundle();
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);

        transactionList.setArguments(bundle);
        membershipList.setArguments(bundle);
        dailyList.setArguments(bundle);
        friendList.setArguments(bundle);

        showInfo();

        btnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTransaction.setSelected(true);
                btnMembership.setSelected(false);
                btnDaily.setSelected(false);
                btnFriendList.setSelected(false);
                vpMainList.setCurrentItem(0);
            }
        });

        btnMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTransaction.setSelected(false);
                btnMembership.setSelected(true);
                btnDaily.setSelected(false);
                btnFriendList.setSelected(false);
                vpMainList.setCurrentItem(1);
            }
        });

        btnDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTransaction.setSelected(false);
                btnMembership.setSelected(false);
                btnDaily.setSelected(true);
                btnFriendList.setSelected(false);
                vpMainList.setCurrentItem(2);
            }
        });

        btnFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTransaction.setSelected(false);
                btnMembership.setSelected(false);
                btnDaily.setSelected(false);
                btnFriendList.setSelected(true);
                vpMainList.setCurrentItem(3);
            }
        });

        checkMembershipSubmit();
    }

//    @Override
//    public void onRefresh(){
//        mainSwipeLayout.setRefreshing(true);
//
//        dailyList.groupView();
//        friendList.showFriend();
//        transactionList.showTransaction();
//        membershipList.groupView();
//
//        mainSwipeLayout.setRefreshing(false);
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        if (curId == R.id.new_process) {
            newProcess();
        } else if (curId == R.id.settings) {
            //다이얼로그로 비밀번호 입력 후 settings 들어갈 수 있슴
            Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
            intent.putExtra("loginMember", loginMember);
            intent.putExtra("loginMemberAccount", loginMemberAccount);
            startActivity(intent);
        } else if (curId == R.id.logout) {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            setResult(TAG_LOGOUT, intent);
            finish();
            //startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainMenuActivity.this, R.style.CustomDialog);

        dialog.setTitle("종료하시겠습니까?");
        dialog.setNeutralButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        dialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.create().show();
    }


    private void showInfo() {
        String text = "이름 : " + loginMember.getMemName();
        tvName.setText(text);
        text = "계좌번호 : " + loginMemberAccount.getAccountNum();
        tvAccountNum.setText(text);
    }

    private void switchPage(int position) {
        switch (position) {
            case 0:
                btnTransaction.setSelected(true);
                btnMembership.setSelected(false);
                btnDaily.setSelected(false);
                btnFriendList.setSelected(false);
                break;
            case 1:
                btnTransaction.setSelected(false);
                btnMembership.setSelected(true);
                btnDaily.setSelected(false);
                btnFriendList.setSelected(false);
                break;
            case 2:
                btnTransaction.setSelected(false);
                btnMembership.setSelected(false);
                btnDaily.setSelected(true);
                btnFriendList.setSelected(false);
                break;
            case 3:
                btnTransaction.setSelected(false);
                btnMembership.setSelected(false);
                btnDaily.setSelected(false);
                btnFriendList.setSelected(true);
                break;
        }
    }

    private void checkMembershipSubmit() {
        progressDialog.show();

        String urlCheckSubmit = "http://" + loginMember.getIp() + "/membership/check";

        final NetworkTask networkTask = new NetworkTask();
        networkTask.setTAG("checkSubmit");
        networkTask.setURL(urlCheckSubmit);

        Map<String, String> params = new HashMap<>();
        params.put("memID", loginMember.getMemID());

        networkTask.execute(params);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                checkMembershipSubmitProcess(networkTask.getResponse());
            }
        }, 1500);

    }

    public void showNoti(int GID, String groupName) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NewFeeActivity.class);
        MembershipGroup membershipGroup = new MembershipGroup();
        membershipGroup.setGID(GID);
        membershipGroup.setGroupName(groupName);
        intent.putExtra("membershipGroup", membershipGroup);
        intent.putExtra("loginMember", loginMember);
        intent.putExtra("loginMemberAccount", loginMemberAccount);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, GID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.launcher_foreground))
                .setContentTitle("MnMs")
                .setContentText(groupName + " 회비 내야해요")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.logo_fore_mnms2); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName = "노티 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        } else
            builder.setSmallIcon(R.mipmap.launcher_round); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(GID, builder.build()); // 고유숫자로 노티피케이션 동작시킴

//        notiBuilder = null;
//        notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        //버전 오레오 이상일 경우
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notiManager.createNotificationChannel(
//                    new NotificationChannel(GID + "", CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
//            notiBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//
//            //하위 버전일 경우
//        } else {
//            notiBuilder = new NotificationCompat.Builder(this);
//        }
//
//        Intent intent = new Intent(this, NewFeeActivity.class);
//        MembershipGroup membershipGroup = new MembershipGroup();
//        membershipGroup.setGID(GID);
//        membershipGroup.setGroupName(groupName);
//        intent.putExtra("membershipGroup", membershipGroup);
//        intent.putExtra("loginMember", loginMember);
//        intent.putExtra("loginMemberAccount", loginMemberAccount);
//        intent.putExtra("feeType","regular");
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, GID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        //알림창 제목
//        notiBuilder.setContentTitle("MnMs");
//        //알림창 메시지
//        notiBuilder.setContentText(groupName + "의 회비내는날! ");
//        //알림창 아이콘
//        notiBuilder.setSmallIcon(R.mipmap.launcher_foreground);
//        //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
//        notiBuilder.setAutoCancel(true);
//
//        //pendingIntent를 builder에 설정 해줍니다.
//        // 알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
//        notiBuilder.setContentIntent(pendingIntent);
//        Notification notification = notiBuilder.build();
//
//        //알림창 실행
//        notiManager.notify(GID, notification);
    }

    private void checkMembershipSubmitProcess(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int GIDsize = jsonObject.getInt("GIDsize");
            for (int i = 0; i < GIDsize; i++) {
                int GID = jsonObject.getInt("GID" + i);
                String groupName = jsonObject.getString("groupName" + i);
                showNoti(GID, groupName);
            }
        } catch (Exception e) {

        }
    }

    //상단 우측의 추가 버튼 클릭


    public void refresh() {
//        progressDialog.show();
//        adapter.notifyDataSetChanged();
//        progressDialog.dismiss();
    }


    protected void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public void newProcess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);

        final String[] items = {"membership", "daily", "friend"};
        final Integer[] selected = {0};

        builder.setTitle("추가 하실 것은?");

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                selected[0] = pos;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (selected[0] == 0) {
                    Intent intent = new Intent(getApplicationContext(), NewMembershipActivity.class);
                    intent.putExtra("loginMember", loginMember);
                    startActivity(intent);
                    vpMainList.setCurrentItem(1);
                } else if (selected[0] == 1) {
                    Intent intent = new Intent(getApplicationContext(), NewDailyActivity.class);
                    intent.putExtra("loginMember", loginMember);
                    startActivity(intent);
                    vpMainList.setCurrentItem(2);
                } else if (selected[0] == 2) {
                    Intent intent = new Intent(getApplicationContext(), NewFriendActivity.class);
                    intent.putExtra("loginMember", loginMember);
                    startActivity(intent);
                    vpMainList.setCurrentItem(3);
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        alertDialog.show();
    }

    static class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<>();

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

}
