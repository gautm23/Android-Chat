package com.example.gautam.chattingapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class SignIn_Signup extends AppCompatActivity {
    public  static String signinup="abc";
    private static final String TAG = "PhoneAuth";
    private EditText username;
    private EditText phonenumber;
    private EditText code;
    private Button sendButton;
    private Button verifyButton;
    private String phoneNumber;
    private String phoneVerificationId;
    private FirebaseDatabase database;
    private ArrayList<String>active_users_list=new ArrayList<>();
    private DatabaseReference dbRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private  Intent intent;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in__signup);
         username=(EditText) findViewById(R.id.editText);
        phonenumber = (EditText) findViewById(R.id.editText2);
        code = (EditText) findViewById(R.id.editText3);
        verifyButton = (Button) findViewById(R.id.button2);
        sendButton = (Button) findViewById(R.id.button);
        database=FirebaseDatabase.getInstance();
        dbRef=database.getReference("/userlist");
       intent=getIntent();

        if(intent.getStringExtra(signinup).equals("sign_in")) {
            username.setVisibility(View.GONE);
        }

        verifyButton.setEnabled(false);

        fbAuth = FirebaseAuth.getInstance();
    }

    public void sendme(View view) {
        phoneNumber = phonenumber.getText().toString();
        dbRef.addListenerForSingleValueEvent(queryChildEventListener);
       // Toast.makeText(getApplicationContext(),"abccccc", Toast
         //       .LENGTH_SHORT).show();

    }

    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        phoneVerificationId = verificationId;
                        verifyButton.setEnabled(true);
                    }
                };
    }

    public void verifier(View view) {

        String coded = code.getText().toString();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, coded);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignIn_Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verifyButton.setEnabled(false);
                            FirebaseUser user = task.getResult().getUser();
                           Intent intent=new Intent(SignIn_Signup.this,MainActivity.class);
                           finishAffinity();
                            if(username.getText().toString().length()>0)
                            { intent.putExtra(MainActivity.u_name,username.getText().toString());}
                            else
                            {intent.putExtra(MainActivity.u_name," ");}
                            intent.putExtra(Intent.EXTRA_TEXT,"SIGNINUP");
                            intent.putExtra(MainActivity.p_no,phoneNumber.substring(3));
                           startActivity(intent);


                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
   private ValueEventListener queryChildEventListener=new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
           Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
             active_users_list.clear();
           while (iterator.hasNext()) {           //we need to get the current no of user from here using DataSnapshot;
               DataSnapshot next = (DataSnapshot) iterator.next();
               active_users_list.add(next.getKey());
           }
           if(intent.getStringExtra(signinup).equals("sign_in")) {
               int abc=0;
               for(int i=0;i<active_users_list.size();i++)
               {
                   if((active_users_list.get(i)).equals(phoneNumber.substring(3)))
                   {
                       abc=1;
                       break;


                   }
               }
               if(abc==0)
               {
                   Toast.makeText(getApplicationContext(), "User doesn't exists please\n try signup !!", Toast
                           .LENGTH_SHORT).show();
               }
               else{
                   // Toast.makeText(getApplicationContext(), "cancelled_1", Toast.LENGTH_SHORT).show();
                   setUpVerificatonCallbacks();
                   PhoneAuthProvider.getInstance().verifyPhoneNumber(
                           phoneNumber,        // Phone number to verify
                           60,                 // Timeout duration
                           TimeUnit.SECONDS,   // Unit of timeout
                           SignIn_Signup.this,               // Activity (for callback binding)
                           verificationCallbacks);
               }
           }
           else {
               int abc=0;
               for (int i = 0; i < active_users_list.size(); i++)
               {
                   if((active_users_list.get(i)).equals(phoneNumber.substring(3)))
                   {
                       Toast.makeText(getApplicationContext(), "User already exists please \ntry signin !!", Toast
                               .LENGTH_SHORT).show();
                       abc=1;
                       break;

                   }
               }
               if(abc==0)
               {
                   setUpVerificatonCallbacks();
                   PhoneAuthProvider.getInstance().verifyPhoneNumber(
                           phoneNumber,        // Phone number to verify
                           60,                 // Timeout duration
                           TimeUnit.SECONDS,   // Unit of timeout
                           SignIn_Signup.this,               // Activity (for callback binding)
                           verificationCallbacks);
                   //verifyButton.setEnabled(true);
               }
           }

       }

       @Override
       public void onCancelled(@NonNull DatabaseError databaseError) {

       }
   };

    @Override
    protected void onStop() {
        super.onStop();
    }
}
