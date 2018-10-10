package com.example.gautam.chattingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication extends AppCompatActivity {

   public  FirebaseAuth fbAuth;
    private Button signinbutton;
    private Button signupbutton;
    private	FirebaseAuth.AuthStateListener	authListener;


    @Override
    protected	void	onCreate(Bundle	savedInstanceState)	{
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_authentication);
             Toolbar toolbar=(Toolbar) findViewById(R.id.auth_toolbar);
             setSupportActionBar(toolbar);
        fbAuth	=	FirebaseAuth.getInstance();

        authListener	=	new	FirebaseAuth.AuthStateListener()
        {
            @Override
            public	void	onAuthStateChanged(	FirebaseAuth	firebaseAuth)	{

                FirebaseUser user	=	firebaseAuth.getCurrentUser();

                if	(user	!=	null)
                {
                    Intent intent=new Intent(Authentication.this,MainActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT,"Authentication");
                    startActivity(intent);

                }
               /* else
                    {
                        Intent intent=new Intent(Authentication.this,SignIn_Signup.class);

                        startActivity(intent);
                    }*/
            }
        };



    }
    public void Sign_in(View view)
    {
        Intent intent=new Intent(Authentication.this,SignIn_Signup.class);
         intent.putExtra(SignIn_Signup.signinup,"sign_in");
        startActivity(intent);
    }
    public void  Sign_up(View view)
    {
        Intent intent=new Intent(Authentication.this,SignIn_Signup.class);
        intent.putExtra(SignIn_Signup.signinup,"sign_up");
        startActivity(intent);
    }
    @Override
    public	void	onStart()
    {
        super.onStart();
    fbAuth.addAuthStateListener(authListener);
    }
    @Override
    public	void	onStop()
    {
        super.onStop();
        if	(authListener	!=	null)
        {
            fbAuth.removeAuthStateListener(authListener);
        }
    }
}
