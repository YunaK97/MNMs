package com.example.teamtemplate.mainscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private TextView textName,accName,textBalance;
    private ImageButton btn_transaction,btn_membership,btn_daily,btn_friendList;
    final private int[] addType={0};
    private ViewPager pager;

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

        //송금내역,membership,daily pager
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
//                        btn_transaction.setImageResource(R.drawable.selected);
//                        btn_membership.setImageResource(R.drawable.no2);
//                        btn_daily.setImageResource(R.drawable.no3);
//                        btn_friendList.setImageResource(R.drawable.no4);
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
        //membership 탈퇴
        btn_membership.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //

                return true;
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
        //daily 탈퇴
        btn_daily.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //

                return true;
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
        //친구 삭제
        btn_friendList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //

                return true;
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

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    protected void plusAction(){
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
