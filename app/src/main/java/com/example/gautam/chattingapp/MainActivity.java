package com.example.gautam.chattingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fbAuth;
    FirebaseUser firebaseUser;
    ArrayList<String>users=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        fbAuth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
       // if(intent.getStringExtra(Intent.EXTRA_TEXT).equals(" "))
      //  {
           // setTitle();
           setTitle("Chat List");
      //  }
      //  else;
           // setTitle(Intent.EXTRA_TEXT);
        ListView listView=(ListView) findViewById(R.id.chatlist);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,,);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.refresh1 :{

            }
            case R.id.sign_out :{
                fbAuth.signOut();
                Intent intent=new Intent(MainActivity.this,Authentication.class);
                startActivity(intent);

            }
        }
        return super.onOptionsItemSelected(item);
    }
}
