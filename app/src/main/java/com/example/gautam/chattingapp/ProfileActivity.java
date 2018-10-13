package com.example.gautam.chattingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    public static String Nm="rf";
    public static String ph="dc";
    public static String uuid="cr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent=getIntent();
        TextView textView=(TextView)findViewById(R.id.textView);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        TextView textView3=(TextView)findViewById(R.id.textView3);
        ImageView imageView=(ImageView) findViewById(R.id.imageView);
        textView.setText("Name:"+intent.getStringExtra(Nm));
        textView.setText("contact:"+intent.getStringExtra(ph));
        textView.setText("uniqueId"+intent.getStringExtra(uuid));
    }
}
