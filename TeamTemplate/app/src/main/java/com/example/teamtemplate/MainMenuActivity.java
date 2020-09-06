package com.hongik.mnms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {
    private Member loginMember;
    FrameLayout containerList;
    ScrollView sendList,membershipList,shortList;
    TextView textName,accName,textBalance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        containerList = findViewById(R.id.containerList);
        sendList=findViewById(R.id.sendList);
        membershipList=findViewById(R.id.talkList);
        shortList=findViewById(R.id.shortList);


        loginMember= (Member) getIntent().getSerializableExtra("loginMember");

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
//                final CharSequence[] oItems = {"회비관리톡방", "짧은만남"};
//
//                AlertDialog.Builder oDialog = new AlertDialog.Builder(MainMenuActivity.this,
//                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
//
//                oDialog.setTitle("색상을 선택하세요")
//                        .setSingleChoiceItems(oItems, -1, new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                nSelectItem = which;
//                            }
//                        })
//                        .setNeutralButton("선택", new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (which >= 0)
//                                    Toast.makeText(getApplicationContext(),
//                                            oItems[nSelectItem], Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .setCancelable(false)
//                        .show();

            }
        });

    }
}
