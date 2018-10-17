package com.example.gautam.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private boolean doubleclicksimultaneously=false;
    private ArrayAdapter<String> arrayAdapter;
    final private int PERMISSION_REQUEST_CODE = 124;
   private String currentUserName;
   private String currentuserphoneno;
   private ArrayList<String> contactlist=new ArrayList<>(); //total mobile contacts
   private ArrayList<String> appUsers_name=new ArrayList<>();// Android Chat users   appUsers_name
   private ArrayList<String> appUsers_no=new ArrayList<>();//contact no of Android Chat users
    private ArrayList<String>appUsers_Id=new ArrayList<>();
    private ArrayList<String> user_friendlist=new ArrayList<>();//current users friendlist
    private ArrayList<String> user_friendlist_no=new ArrayList<>();//contact no current users friendlist
    private ArrayList<String> user_friends_Id=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        fbAuth=FirebaseAuth.getInstance();
        setTitle("Friend List");

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
                dbRef.child("userlist").child(ss).child("name").setValue(intent.getStringExtra(u_name));
                dbRef.child("userlist").child(ss).child("userId").setValue(currentUserUid);
            }
        }
        user_friendlist.clear();
        listView=(ListView) findViewById(R.id.chatlist);
         arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,user_friendlist);
        listView.setAdapter(arrayAdapter);
       // getCallingActivity().getClassName();
        dbRef2=database.getReference("/users/"+currentUserUid);

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent1=new Intent(MainActivity.this,ChattingActivity.class);
                intent1.putExtra(ChattingActivity.user_name,currentUserName);
               intent1.putExtra(ChattingActivity.user_phone_no,currentuserphoneno);
               intent1.putExtra(ChattingActivity.user_Id,currentUserUid);
                intent1.putExtra(ChattingActivity.friend_name,user_friendlist.get(position));
               intent1.putExtra(ChattingActivity.friend_phone_no,user_friendlist_no.get(position));
               intent1.putExtra(ChattingActivity.friend_Id,user_friends_Id.get(position));
                startActivity(intent1);
           }
       });
    }

    @Override
    protected void onStart() {
        super.onStart();
           dbRef2.addListenerForSingleValueEvent(queryValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
       dbRef2.removeEventListener(queryValueListener);
       // dbRef1.removeEventListener(childEventListener);
    }
    @Override
    public void onBackPressed() {
        if(doubleclicksimultaneously)
        {super.onBackPressed();
            return;
        }
        doubleclicksimultaneously=true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleclicksimultaneously = false;
            }
        }, 2000);
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
            case R.id.profile :  {
                                    Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                                            intent.putExtra(ProfileActivity.Nm,currentUserName);
                                            intent.putExtra(ProfileActivity.ph,currentuserphoneno);
                                            intent.putExtra(ProfileActivity.uuid,currentUserUid);
                                            startActivity(intent);

                                   } break;

            case R.id.refresh1 :{
                                    appUsers_no.clear();
                                    appUsers_name.clear();
                                    appUsers_Id.clear();
                                    dbRef=database.getReference("/userlist");
                                     dbRef.addListenerForSingleValueEvent(new ValueEventListener()
                                     {
                                  @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                  Iterable<DataSnapshot>	snapshotIterator	=	dataSnapshot.getChildren();
                                 Iterator<DataSnapshot> iterator	=	snapshotIterator.iterator();

                                 while	(iterator.hasNext())	{           //we need to get the current no of user from here using DataSnapshot;
                                  DataSnapshot	next	=	(DataSnapshot)	iterator.next();
                                 appUsers_no.add(next.getKey());
                                 appUsers_name.add(next.child("name").getValue(String.class));
                                 appUsers_Id.add(next.child("userId").getValue(String.class));
                                       }
                                  AccessContact();
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                 });
                                }
                                break;

            case R.id.sign_out :    {
                                         fbAuth.signOut();
                                         Intent intent=new Intent(MainActivity.this,Authentication.class);
                                         startActivity(intent);

                                     }
        }
        return super.onOptionsItemSelected(item);
    }
    private ValueEventListener  queryValueListener= new ValueEventListener() {

        @Override

            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                currentuserphoneno=dataSnapshot.child("phone").getValue(String.class);
                currentUserName=dataSnapshot.child("name").getValue(String.class);
                if (dataSnapshot.child("friends").exists()) {
                    user_friendlist.clear();
                    user_friendlist_no.clear();
                    user_friends_Id.clear();
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.child("friends").getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                    while (iterator.hasNext()) {           //we need to get the current no of user from here using DataSnapshot;
                        DataSnapshot next = (DataSnapshot) iterator.next();
                        user_friendlist.add(next.child("name").getValue(String.class));
                        user_friendlist_no.add(next.child("phone").getValue(String.class));
                        user_friends_Id.add(next.child("userId").getValue(String.class));
                    }
                    arrayAdapter.notifyDataSetChanged();


                } else {

                    appUsers_name.clear();
                    appUsers_no.clear();
                    appUsers_Id.clear();
                    dbRef=dbRef.child("userlist");
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();

                            for (DataSnapshot next : snapshotIterator) {

                                appUsers_no.add(next.getKey());
                                appUsers_name.add(next.child("name").getValue(String.class));
                                appUsers_Id.add(next.child("userId").getValue(String.class));


                            }
                          //  String sss="abcd="+appUsers_no.size();
                            //Toast.makeText(getApplicationContext(),appUsers_no.get(0), Toast.LENGTH_LONG).show();
                            AccessContact();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "cancelled_1", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

        }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
           Toast.makeText(getApplicationContext(), "cancelled_2", Toast.LENGTH_SHORT).show();
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


    private void displayContacts()
    {
        contactlist.clear();
       user_friendlist.clear();
       user_friendlist_no.clear();
       user_friends_Id.clear();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

             database.getReference("/users/"+currentUserUid+"/friends").removeValue();
          if (cur.getCount() > 0)
          {
            while (cur.moveToNext())
            {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                      Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                            while (pCur.moveToNext())
                         {
                           String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                           if(phoneNo.length()==13)
                            contactlist.add(phoneNo.substring(3));
                            if(phoneNo.length()==11)
                            contactlist.add(phoneNo.substring(1));
                            if(phoneNo.length()==10)
                            contactlist.add(phoneNo);
                         }
                       pCur.close();
                }
             }
             }

            for(int i=0;i<appUsers_no.size();i++)
           {  if(!(appUsers_no.get(i).equals(currentuserphoneno)))
            for(int j=0;j<contactlist.size();j++) //for finding the no of friend using chatting app in contact list;
            {
               if((contactlist.get(j)).equals(appUsers_no.get(i)))
               {
                   user_friendlist.add(appUsers_name.get(i));
                   user_friendlist_no.add(appUsers_no.get(i));
                   user_friends_Id.add(appUsers_Id.get(i));
                    break;
               }

            }
            arrayAdapter.notifyDataSetChanged();
           }

        DatabaseReference newChildReff =database.getReference().child("users").child(currentUserUid).child("friends");

        for(int i=0;i<user_friendlist_no.size();i++)
          {   DatabaseReference dsss;
            dsss = newChildReff.push();
            dsss.child("name").setValue(user_friendlist.get(i));
            dsss.child("phone").setValue(user_friendlist_no.get(i));
            dsss.child("userId").setValue(user_friends_Id.get(i));
           }
       cur.close();
        dbRef=database.getReference();
    }

}
