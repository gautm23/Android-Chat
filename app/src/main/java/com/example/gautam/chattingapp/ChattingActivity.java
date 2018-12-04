package com.example.gautam.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
   // ArrayAdapter<String> adapter;
    private recycle adapter;
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
        listItems.clear();
        setTitle(friendname);
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycler2);
        adapter=new recycle(listItems);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("/users/"+chatid+"/messages/"+friendnumber);
        databaseReference2=firebaseDatabase.getReference("/users/"+friendid+"/messages/"+chatnumber);
        databaseReference.addChildEventListener(childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.call : {
                        AccessContact();
            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }
    private void AccessContact()
    {

        String message = "You need to grant access to Manifest.permission.READ_CONTACTS";
        if (!hasRuntimePermission(getApplicationContext(), Manifest.permission.CALL_PHONE))
        {
            requestRuntimePermission(ChattingActivity.this, Manifest.permission.CALL_PHONE, 1111);
        } else
        {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+91"+friendnumber));
            startActivity(callIntent);
        }
    }
    private boolean hasRuntimePermission(Context context, String runtimePermission)     {
        boolean ret = false;
        //Get current android os version.
        int currentAndroidVersion = Build.VERSION.SDK_INT;

        // Build.VERSION_CODES.M's value is 23.
        if(currentAndroidVersion > 22)
        {
            // Only android version 23+ need to check runtime permission.
            if(ContextCompat.checkSelfPermission(context, runtimePermission) == PackageManager.PERMISSION_GRANTED)
                ret = true;
        }
        else
        {
            ret = true;
        }
        return ret;
    }
    private void requestRuntimePermission(Activity activity, String runtimePermission, int requestCode)
    {
        ActivityCompat.requestPermissions(activity, new String[]{runtimePermission}, requestCode);
    }

    public void onRequestPermissionsResult(int requestCode,    String[] permissions, int[] grantResults)
    {         super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If this is our permission request result.
        if(requestCode==1111)
        {
            if(grantResults.length > 0)
            {                 // Construct result message.
               // StringBuffer msgBuf = new StringBuffer();
                int grantResult = grantResults[0];
                if(grantResult==PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(),"You denied Manifest.permission.CALL_PHONE permissions", Toast.LENGTH_LONG).show();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:91"+friendnumber));
                    startActivity(callIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You denied Manifest.permission.CALL_PHONE permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String str2=dataSnapshot.child("message").getValue(String.class);
          listItems.add(str2);
        //  adapter.notifyDataSetChanged();
          adapter.notifyItemInserted(listItems.size());
            // adapter.add(str2);
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
