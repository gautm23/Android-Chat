package com.example.gautam.chattingapp;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class SignIn_Signup extends AppCompatActivity {

    private static final String TAG = "PhoneAuth";

    private EditText username;
    private EditText phonenumber;
    private EditText code;
    private Button sendButton;
    private Button verifyButton;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in__signup);
        Toolbar toolbar=(Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
         username=(EditText) findViewById(R.id.editText);
        phonenumber = (EditText) findViewById(R.id.editText2);
        code = (EditText) findViewById(R.id.editText3);
        verifyButton = (Button) findViewById(R.id.button2);
        sendButton = (Button) findViewById(R.id.button);
        Intent intent=getIntent();

        if(intent.getStringExtra(Intent.EXTRA_TEXT).equals("sign_in")) {
            username.setVisibility(View.GONE);
            setTitle("Sign In");
        }
        else
        {
            setTitle("Sign Up");
        }
        verifyButton.setEnabled(false);


        fbAuth = FirebaseAuth.getInstance();


    }

    public void sendme(View view) {

        String phoneNumber = phonenumber.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                SignIn_Signup.this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        verifyButton.setEnabled(false);
                        Intent intent=new Intent(SignIn_Signup.this,MainActivity.class);
                        if(username.getText().toString().length()>0)
                            intent.putExtra(Intent.EXTRA_TEXT,username.getText().toString());
                        else
                            intent.putExtra(Intent.EXTRA_TEXT," ");
                        startActivity(intent);
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
                        resendToken = token;

                        verifyButton.setEnabled(true);
                       // sendButton.setEnabled(false);
                        //resendButton.setEnabled(true);
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
                            if(username.getText().toString().length()>0)
                                intent.putExtra(Intent.EXTRA_TEXT,username.getText().toString());
                            else
                                intent.putExtra(Intent.EXTRA_TEXT," ");
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

    public void resendCode(View view) {

        String phoneNumber = phonenumber.getText().toString();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    public void signOut(View view) {
        fbAuth.signOut();
       // statusText.setText("Signed Out");
        //signoutButton.setEnabled(false);
        sendButton.setEnabled(true);
    }

}
