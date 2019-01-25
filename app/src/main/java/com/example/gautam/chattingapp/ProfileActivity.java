package com.example.gautam.chattingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {
    public static String Nm="rf";
    public static String ph="dc";
    public static String uuid="cr";
    public static String PROFILE_PHOTO="USER";
    private Bitmap bmp ;
    private Bitmap bitmap;
    private ImageView imageView;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent=getIntent();
        TextView textView=(TextView)findViewById(R.id.textView);
        TextView textView2=(TextView)findViewById(R.id.textView2);
        TextView textView3=(TextView)findViewById(R.id.textView3);
        imageView =(ImageView) findViewById(R.id.image);
        preferences = getSharedPreferences("abcde", Context.MODE_PRIVATE);
        String user_pic=preferences.getString(PROFILE_PHOTO, "__");
        if(user_pic.equals("__"))
        {
        imageView.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString(PROFILE_PHOTO,"__");
            editor.apply();
        }
        else
        {   bitmap = BitmapFactory.decodeFile(user_pic);
            imageView.setImageBitmap(bitmap);
        }
        textView.setText(intent.getStringExtra(Nm));
        textView2.setText("contact: "+intent.getStringExtra(ph));
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
        if (resultCode == RESULT_OK) {

            if (requestCode == 1 && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
               // String picturePath = cursor.getString(columnIndex);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString(PROFILE_PHOTO,cursor.getString(columnIndex));
                editor.apply();
                Bitmap realImage = BitmapFactory.decodeFile(cursor.getString(columnIndex));
                cursor.close();
                imageView.setImageBitmap(realImage);
               /* try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageView.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }*/

            }
            else {
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
            }


    }
    private void AccessGallery()
    {

        String message = "You need to grant access to Manifest.permission.READ_EXTERNAL_STORAGE";
        if (!hasRuntimePermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)||!hasRuntimePermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            requestRuntimePermission(ProfileActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1111);
        } else
        {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
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
    private void requestRuntimePermission(Activity activity, String [] runtimePermission, int requestCode)
    {
        ActivityCompat.requestPermissions(activity, runtimePermission, requestCode);
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
