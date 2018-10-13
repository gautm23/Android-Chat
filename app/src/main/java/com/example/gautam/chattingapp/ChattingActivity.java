package com.example.gautam.chattingapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

public class ChattingActivity extends AppCompatActivity {
    public static String user_name="abc";
    public static String user_phone_no="def";
    public static String user_Id="ghi";
    public static String friend_name="jkl";
    public static String friend_phone_no="mno";
    public static String friend_Id="pqr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Toolbar toolbar=(Toolbar)findViewById(R.id.chatting_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        String chatname=intent.getStringExtra(user_name);
        String chatnumber=intent.getStringExtra(user_phone_no);
        String chatid=intent.getStringExtra(user_Id);
        String friendname=intent.getStringExtra(friend_name);
        String friendnumber=intent.getStringExtra(friend_phone_no);
        String friendid=intent.getStringExtra(friend_Id);
        setTitle(friendname);



    }


}
