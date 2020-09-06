package kr.hongik.mnms.mainscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import kr.hongik.mnms.Account;
import kr.hongik.mnms.Member;
import kr.hongik.mnms.R;
import kr.hongik.mnms.mainscreen.ui.daily.DailyList;
import kr.hongik.mnms.mainscreen.ui.friend.FriendList;
import kr.hongik.mnms.mainscreen.ui.membership.MembershipList;
import kr.hongik.mnms.mainscreen.ui.settings.SettingsActivity;
import kr.hongik.mnms.mainscreen.ui.transaction.TransactionList;
import kr.hongik.mnms.newgroupfriend.NewDailyActivity;
import kr.hongik.mnms.newgroupfriend.NewFriendActivity;
import kr.hongik.mnms.newgroupfriend.NewMembershipActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class MainMenuActivity extends AppCompatActivity {
    private Member loginMember;
    private Account loginMemberAccount;
    private String ip;

    //layouts
    private ActionBar actionBar;
    private TextView textName,accName,textBalance;
    private ImageButton btn_transaction,btn_membership,btn_daily,btn_friendList;
    private ViewPager pager;

    //variables
    final private int[] addType={0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //id찾기
        pager=findViewById(R.id.pager);
        textName=findViewById(R.id.textName);
        accName=findViewById(R.id.accName);
        textBalance=findViewById(R.id.textBalance);
        btn_transaction=findViewById(R.id.btn_transaction);
        btn_membership=findViewById(R.id.btn_membership);
        btn_daily=findViewById(R.id.btn_daily);
        btn_friendList=findViewById(R.id.btn_friendList);

        //액션바
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_mainmenu);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_USE_LOGO);

        //로그인 정보 가져오기
        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount=(Account)intent.getSerializableExtra("loginMemberAccount");
        ip=intent.getStringExtra("ip");

        //송금내역,membership,daily pager
        pager.setOffscreenPageLimit(4);

        MyPagerAdapter adapter= new MyPagerAdapter(getSupportFragmentManager());

        TransactionList transactionList=new TransactionList();
        adapter.addItem(transactionList);
        MembershipList membershipList=new MembershipList();
        adapter.addItem(membershipList);
        DailyList dailyList=new DailyList();
        adapter.addItem(dailyList);
        FriendList friendList=new FriendList();
        adapter.addItem(friendList);

        pager.setAdapter(adapter);
        btn_transaction.setSelected(true);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        btn_transaction.setSelected(true);
                        btn_membership.setSelected(false);
                        btn_daily.setSelected(false);
                        btn_friendList.setSelected(false);
                        break;
                    case 1:
                        btn_transaction.setSelected(false);
                        btn_membership.setSelected(true);
                        btn_daily.setSelected(false);
                        btn_friendList.setSelected(false);
                        break;
                    case 2:
                        btn_transaction.setSelected(false);
                        btn_membership.setSelected(false);
                        btn_daily.setSelected(true);
                        btn_friendList.setSelected(false);
                        break;
                    case 3:
                        btn_transaction.setSelected(false);
                        btn_membership.setSelected(false);
                        btn_daily.setSelected(false);
                        btn_friendList.setSelected(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Bundle bundle=new Bundle();
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);
        bundle.putString("ip",ip);

        transactionList.setArguments(bundle);
        membershipList.setArguments(bundle);
        dailyList.setArguments(bundle);
        friendList.setArguments(bundle);
        //////////////////////////////

        String text="이름 : "+loginMember.getMemName();
        textName.setText(text);
        text="계좌번호 : "+loginMemberAccount.getAccountNum();
        accName.setText(text);
        text="잔액 : "+loginMemberAccount.getAccountBalance();
        textBalance.setText(text);

        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_transaction.setSelected(true);
                btn_membership.setSelected(false);
                btn_daily.setSelected(false);
                btn_friendList.setSelected(false);
                pager.setCurrentItem(0);
            }
        });

        btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_transaction.setSelected(false);
                btn_membership.setSelected(true);
                btn_daily.setSelected(false);
                btn_friendList.setSelected(false);
                pager.setCurrentItem(1);
            }
        });

        btn_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_transaction.setSelected(false);
                btn_membership.setSelected(false);
                btn_daily.setSelected(true);
                btn_friendList.setSelected(false);
                pager.setCurrentItem(2);
            }
        });

        btn_friendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_transaction.setSelected(false);
                btn_membership.setSelected(false);
                btn_daily.setSelected(false);
                btn_friendList.setSelected(true);
                pager.setCurrentItem(3);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    //상단 우측의 추가 버튼 클릭
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        if(curId==R.id.new_process){
            newProcess();
        }else if(curId==R.id.settings){
            Intent intent=new Intent(MainMenuActivity.this, SettingsActivity.class);
            intent.putExtra("loginMember",loginMember);
            intent.putExtra("loginMemberAccount",loginMemberAccount);
            intent.putExtra("ip",ip);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public void newProcess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final String[] items = {"membership","daily","friend"};
        final Integer[] selected = {0};

        builder.setTitle("리스트 추가 예제");

        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                selected[0] =pos;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                if(selected[0] ==0){
                    Intent intent = new Intent(getApplicationContext(), NewMembershipActivity.class);
                    intent.putExtra("loginMember",loginMember);
                    intent.putExtra("ip",ip);
                    startActivity(intent);
                }else if(selected[0] ==1){
                    Intent intent = new Intent(getApplicationContext(), NewDailyActivity.class);
                    intent.putExtra("loginMember",loginMember);
                    intent.putExtra("ip",ip);
                    startActivity(intent);
                }else if(selected[0] ==2){
                    Intent intent = new Intent(getApplicationContext(), NewFriendActivity.class);
                    intent.putExtra("loginMember",loginMember);
                    intent.putExtra("ip",ip);
                    startActivity(intent);
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainMenuActivity.this,R.style.CustomDialog);

        dialog.setTitle("종료하시겠습니까?");
        dialog.setNeutralButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.create().show();
    }


    static class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<>();

        MyPagerAdapter(FragmentManager fm){
            super(fm);
        }

        public void addItem(Fragment item){
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
