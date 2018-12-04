package com.example.gautam.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {
    public static String Nm="rf";
    public static String ph="dc";
    public static String uuid="cr";
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent=getIntent();
        TextView textView=(TextView)findViewById(R.id.textView);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        TextView textView3=(TextView)findViewById(R.id.textView3);
        imageView =(ImageView) findViewById(R.id.imageView);
        textView.setText("Name:"+intent.getStringExtra(Nm));
        textView2.setText("contact:"+intent.getStringExtra(ph));
        textView3.setText("uniqueId:"+intent.getStringExtra(uuid));

        imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AccessGallery();
            }});

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {

            if (requestCode == 1 && data != null) {
                Uri imageUri = data.getData();
                imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(imageUri);
            }
        }
    }
    private void AccessGallery()
    {

        String message = "You need to grant access to Manifest.permission.READ_CONTACTS";
        if (!hasRuntimePermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            requestRuntimePermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, 1111);
        } else
        {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("aspectX", 85);
            intent.putExtra("aspectY", 99);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 1);
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
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX", 256);
                    intent.putExtra("outputY", 256);
                    intent.putExtra("aspectX", 85);
                    intent.putExtra("aspectY", 99);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, 1);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"You denied Manifest.permission.READ_EXTERNAL_STORAGE permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
