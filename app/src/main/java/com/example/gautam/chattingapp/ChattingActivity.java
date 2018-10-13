package com.example.gautam.chattingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {
    public static String user_name="abc";
    public static String user_phone_no="def";
    public static String user_Id="ghi";
    public static String friend_name="jkl";
    public static String friend_phone_no="mno";
    public static String friend_Id="pqr";
    private String chatname;
    private String chatnumber;
    private String chatid;
    private String friendname;
    private String friendnumber;
    private String friendid;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    private Button button;
    private EditText editText;
    private ArrayList<String> listItems	=	new	ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Toolbar toolbar=(Toolbar)findViewById(R.id.chatting_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        button=(Button)findViewById(R.id.button3);
        editText=(EditText)findViewById(R.id.editText4);
        chatname=intent.getStringExtra(user_name);
        chatnumber=intent.getStringExtra(user_phone_no);
        chatid=intent.getStringExtra(user_Id);
        friendname=intent.getStringExtra(friend_name);
        friendnumber=intent.getStringExtra(friend_phone_no);
        friendid=intent.getStringExtra(friend_Id);
        setTitle(friendname);
        ListView dataListView=(ListView)findViewById(R.id.list);
        adapter	=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        dataListView.setAdapter(adapter);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("/users/"+chatid+"/messages");
        databaseReference2=firebaseDatabase.getReference("/users/"+friendid+"/messages");
        databaseReference.addChildEventListener(childEventListener);
    }
    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String str2=dataSnapshot.child("message").getValue(String.class);
            adapter.add(str2);
           // adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    public void message(View view)
    {
        String string=editText.getText().toString();
        editText.setText("");
        if(string.length()>0)
        {
           DatabaseReference data=databaseReference.push();
           DatabaseReference data2=databaseReference2.push();
           String str2=chatname+":\n"+string;
           data.child("message").setValue(str2);
            data2.child("message").setValue(str2);
        }
        else{
            Toast.makeText(this,"Size of Message should be greater than zero",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(childEventListener);
    }
}
