package com.example.teamtemplate.membership;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.teamtemplate.R;


public class MembershipActivity extends AppCompatActivity {
    public TextView tv_president;
    public TextView tv_payday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        tv_president = findViewById(R.id.ms_president);
        tv_payday = findViewById(R.id.ms_payday);
    }


}
