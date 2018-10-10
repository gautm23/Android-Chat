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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    public static String u_name="def";
    public static String p_no="gdbd";
    private Query query;
    private FirebaseAuth fbAuth;
    private String currentUserUid;
    private DatabaseReference dbRef;
    private DatabaseReference dbRef2;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private   ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    final private int PERMISSION_REQUEST_CODE = 124;
    private ArrayList<String>users=new ArrayList<>();
   private ArrayList<String> contactlist=new ArrayList<>();
   private ArrayList<String> contact_name=new ArrayList<>();
   private ArrayList<String> friendList=new ArrayList<>();
   private ArrayList<String> friend_contacts=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        fbAuth=FirebaseAuth.getInstance();
        setTitle("Friend List");
         listView=(ListView) findViewById(R.id.chatlist);
         currentUserUid = fbAuth.getCurrentUser().getUid();
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference();
        Intent intent=getIntent();
        String ss=intent.getStringExtra(Intent.EXTRA_TEXT);
        if(ss.equals("SIGNINUP"))
        {   ss=intent.getStringExtra(u_name);
            if(ss.equals(" "));
            else
            { //Toast.makeText(this,"cdsvrv",Toast.LENGTH_SHORT).show();
                dbRef.child("users").child(currentUserUid).child("name").setValue(intent.getStringExtra(u_name));
                ss=intent.getStringExtra(p_no);
               dbRef.child("users").child(currentUserUid).child("phone").setValue(ss);
                dbRef.child("userlist").child(ss).setValue(intent.getStringExtra(u_name));
            }
        }
         arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,contactlist);
        listView.setAdapter(arrayAdapter);
       // getCallingActivity().getClassName();
        dbRef2=database.getReference("/users/"+currentUserUid+"/friends");
         query	=	dbRef2.orderByKey();
    }

    @Override
    protected void onStart() {
        super.onStart();
        query.addListenerForSingleValueEvent(queryValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbRef2.removeEventListener(queryValueListener);
       // dbRef1.removeEventListener(childEventListener);
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
            case R.id.refresh1 :{  AccessContact(); break;
            }
            case R.id.sign_out :{
                fbAuth.signOut();
                Intent intent=new Intent(MainActivity.this,Authentication.class);
                startActivity(intent);

            }
        }
        return super.onOptionsItemSelected(item);
    }
    private ValueEventListener  queryValueListener= new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(!dataSnapshot.exists())
            {
                AccessContact();
            }
            else
            {
                contactlist.clear();
                contact_name.clear();
                Iterable<DataSnapshot>	snapshotIterator	=	dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator	=	snapshotIterator.iterator();

                while	(iterator.hasNext())	{

                    DataSnapshot	next	=	(DataSnapshot)	iterator.next();
                    contactlist.add(next.getValue().toString());

                }
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void AccessContact()
    {

        String message = "You need to grant access to Manifest.permission.READ_CONTACTS";
        if (!hasRuntimePermission(getApplicationContext(), Manifest.permission.READ_CONTACTS))
        {
            requestRuntimePermission(MainActivity.this,
                    Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE);
            AccessContact();
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
        contactlist.clear();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
            database.getReference("/users/"+currentUserUid+"/friends").removeValue();
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
                        if(phoneNo.length()==13)
                            contactlist.add(phoneNo.substring(3));
                        if(phoneNo.length()==11)
                            contactlist.add(phoneNo.substring(1));
                        if(phoneNo.length()==10)
                            contactlist.add(phoneNo);
                        if(phoneNo.length()>=10) {
                            contact_name.add(name);
                            DatabaseReference newChildRef = dbRef2.push();
                            String key = newChildRef.getKey();
                            dbRef2.child(key).child("name").setValue(name);
                            dbRef2.child(key).child("contact_no").setValue(phoneNo);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        }
                    pCur.close();
                }
            }
        }
    }
}
