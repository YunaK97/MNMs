package com.example.teamtemplate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {
    private Member loginMember;
    FrameLayout containerList;
    ScrollView sendList,membershipList,shortList;
    TextView textName,accName,textBalance;
    final int[] addType={0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        containerList = findViewById(R.id.containerList);
        sendList=findViewById(R.id.sendList);
        membershipList=findViewById(R.id.talkList);
        shortList=findViewById(R.id.shortList);

        Intent intent=getIntent();
        loginMember= (Member) getIntent().getSerializableExtra("loginMember");

        textName=findViewById(R.id.textName);
        textName.setText("이름 : "+loginMember.getMemName());
        accName=findViewById(R.id.accName);
        accName.setText("아이디 : "+loginMember.getMemID());

        Button btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendList.setVisibility(View.VISIBLE);
                membershipList.setVisibility(View.GONE);
                shortList.setVisibility(View.GONE);
            }
        });

        Button btn_membership=findViewById(R.id.btn_membership);
        btn_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendList.setVisibility(View.GONE);
                membershipList.setVisibility(View.VISIBLE);
                shortList.setVisibility(View.GONE);
            }
        });

        Button btn_short=findViewById(R.id.btn_short);
        btn_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendList.setVisibility(View.GONE);
                membershipList.setVisibility(View.GONE);
                shortList.setVisibility(View.VISIBLE);
            }
        });

        Button btn_addList=findViewById(R.id.btn_addList);
        btn_addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] select=new String[] {"회비모임","꿀잼모임"};

                AlertDialog.Builder dialog=new AlertDialog.Builder(MainMenuActivity.this);
                dialog.setTitle("어떤 모임을 생성하나요?")
                        .setSingleChoiceItems(select, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addType[0]=which;
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),select[addType[0]],Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),select[addType[0]],Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.create();
                dialog.show();
            }
        });
    }
}
