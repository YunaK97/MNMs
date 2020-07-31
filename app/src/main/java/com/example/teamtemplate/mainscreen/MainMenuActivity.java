package com.example.teamtemplate.mainscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamtemplate.Account;
import com.example.teamtemplate.Member;
import com.example.teamtemplate.R;
import com.example.teamtemplate.newgroupfriend.NewDailyActivity;
import com.example.teamtemplate.newgroupfriend.NewFriendActivity;
import com.example.teamtemplate.newgroupfriend.NewMembershipActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


public class MainMenuActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Member loginMember;
    private Account loginMemberAccount;
    private RecyclerView groupMembershiplList;
    private GroupAdapter groupAdapter;
    private TextView textName,accName,textBalance;
    final private int[] addType={0};
    private ViewPager pager;

    private String TAG_MEMBERSHIP="membership",TAG_DAILY="daily",TAG_SUCCESS="success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //액션바
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_mainmenu);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_USE_LOGO);

        //로그인 정보 가져오기
        Intent intent=getIntent();
        loginMember= (Member) intent.getSerializableExtra("loginMember");
        loginMemberAccount=(Account)intent.getSerializableExtra("loginMemberAccount");

        //송금내역,membership,daily pager
        pager=findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);

        MyPagerAdapter adapter=new MyPagerAdapter(getSupportFragmentManager());

        TransactionList transactionList=new TransactionList();
        adapter.addItem(transactionList);
        MembershipList membershipList=new MembershipList();
        adapter.addItem(membershipList);
        DailyList dailyList=new DailyList();
        adapter.addItem(dailyList);
        FriendList friendList=new FriendList();
        adapter.addItem(friendList);

        pager.setAdapter(adapter);

        Bundle bundle=new Bundle();
        bundle.putSerializable("loginMember", loginMember);
        bundle.putSerializable("loginMemberAccount", loginMemberAccount);

        transactionList.setArguments(bundle);
        membershipList.setArguments(bundle);
        dailyList.setArguments(bundle);
        friendList.setArguments(bundle);
        //////////////////////////////

        textName=findViewById(R.id.textName);
        String text="이름 : "+loginMember.getMemName();
        textName.setText(text);
        accName=findViewById(R.id.accName);
        text="계좌번호 : "+loginMemberAccount.getAccountNum();
        accName.setText(text);
        textBalance=findViewById(R.id.textBalance);
        text="잔액 : "+loginMemberAccount.getAccountBalance();
        textBalance.setText(text);

        ImageButton btn_transaction=findViewById(R.id.btn_transaction);
        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0);
            }
        });
        ImageButton btn_membership=findViewById(R.id.btn_membership);
        btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1);
            }
        });
        ImageButton btn_daily=findViewById(R.id.btn_daily);
        btn_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2);
            }
        });
        ImageButton btn_friendList=findViewById(R.id.btn_friendList);
        btn_friendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(3);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        //Intent intent;
        if(curId==R.id.add_membership){
            Intent intent = new Intent(getApplicationContext(), NewMembershipActivity.class);
            intent.putExtra("loginMember",loginMember);
            startActivity(intent);
        }else if(curId==R.id.add_daily){
            Intent intent = new Intent(getApplicationContext(), NewDailyActivity.class);
            intent.putExtra("loginMember",loginMember);
            startActivity(intent);
        }else if(curId==R.id.add_friend){
            Intent intent=new Intent(getApplicationContext(), NewFriendActivity.class);
            intent.putExtra("loginMember",loginMember);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public void plusAction(){
        final String[] select=new String[] {"회비모임","꿀잼모임"};

        AlertDialog.Builder dialog=new AlertDialog.Builder(MainMenuActivity.this);
        dialog.setTitle("추가!")
                .setSingleChoiceItems(select, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addType[0]=which;
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(addType[0]==0){
                            Intent intent = new Intent(getApplicationContext(), NewMembershipActivity.class);
                            intent.putExtra("loginMember",loginMember);
                            startActivity(intent);
                        }else if(addType[0]==1){
                            Intent intent = new Intent(getApplicationContext(), NewDailyActivity.class);
                            intent.putExtra("loginMember",loginMember);
                            startActivity(intent);
                        }
                    }
                })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"취소",Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.create();
        dialog.show();

}
    class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items =new ArrayList<Fragment>();

        public MyPagerAdapter(FragmentManager fm){
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
