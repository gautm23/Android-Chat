package com.example.gautam.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth fbAuth;
    FirebaseUser firebaseUser;
    ArrayList<String>users=new ArrayList<>();
    final private int PERMISSION_REQUEST_CODE = 124;
    ArrayList<String>contactlist=new ArrayList<>();
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
        AccessContact();
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,contactlist);
        listView.setAdapter(arrayAdapter);
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
            case R.id.refresh1 :{  AccessContact();
                displayContacts();
            }
            case R.id.sign_out :{
                fbAuth.signOut();
                Intent intent=new Intent(MainActivity.this,Authentication.class);
                startActivity(intent);

            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void AccessContact()
    {

        String message = "You need to grant access to Manifest.permission.READ_CONTACTS";
        if (!hasRuntimePermission(getApplicationContext(), Manifest.permission.READ_CONTACTS))
        {
            requestRuntimePermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE);
        } else
        {
            displayContacts();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,    String[] permissions, int[] grantResults)
    {         super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If this is our permission request result.
        if(requestCode==PERMISSION_REQUEST_CODE)
        {
            if(grantResults.length > 0)
            {                 // Construct result message.
                StringBuffer msgBuf = new StringBuffer();
                int grantResult = grantResults[0];
                if(grantResult==PackageManager.PERMISSION_GRANTED)
                {
                    displayContacts();
                }
                else
                {
                    msgBuf.append("You denied below permissions : ");
                    Toast.makeText(getApplicationContext(), msgBuf.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void displayContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactlist.add(phoneNo);
                        }
                    pCur.close();
                }
            }
        }
    }
}
