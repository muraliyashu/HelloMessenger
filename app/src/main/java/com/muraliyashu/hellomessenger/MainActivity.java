package com.muraliyashu.hellomessenger;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText edit;
    private EditText mobileNumber;
    private Boolean checkContactsPermission = false;
    private FirebaseAuth firebaseAuth;
    private Boolean checkHarwarePermission = false;
    private DatabaseReference root;
    private static String mobilenumber = "";
    private String mVerificationId;
    private AlertDialog mAlertDialog;
    final sqlite_database db = new sqlite_database(MainActivity.this);
    File sd = Environment.getExternalStorageDirectory();
    private static String android_id;
    public static Activity fa;
    private AlertDialog alert11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.open();

        fa=this;
        android_id = Settings.Secure.getString(MainActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);

        /*getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.gear);
        getSupportActionBar().setDisplayUseLogoEnabled(true);*/
        if (Build.VERSION.SDK_INT >= 23)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        try
        {
            buttonRegister = (Button)findViewById(R.id.buttonregister);
            mobileNumber = (EditText)findViewById(R.id.mobilenumber);
            buttonRegister.setOnClickListener(this);

            mobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onClick(buttonRegister);
                        return true;
                    }
                    return false;
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view)
    {
        if(checkForFolder())
        {
                try {
                    firebaseAuth = FirebaseAuth.getInstance();
                    if (view == buttonRegister) {
                        if (TextUtils.isEmpty(mobileNumber.getText().toString().trim())) {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage("Please enter your phone number.");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            //Toast.makeText(MainActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mobileNumber.getText().toString().trim().length() < 10) {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage("Please enter valid phone number.");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            //Toast.makeText(MainActivity.this, "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mobilenumber = mobileNumber.getText().toString().trim();

                        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.number_confirmation, null);
                        TextView editButton = dialogView.findViewById(R.id.editNumber);
                        TextView okButton = dialogView.findViewById(R.id.okNumber);
                        TextView confirm_number = dialogView.findViewById(R.id.confirm_number);
                        confirm_number.setText("+91 "+mobilenumber);
                        builder1.setCancelable(false);
                        builder1.setView(dialogView);
                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert11.dismiss();
                            }
                        });
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alert11.dismiss();

                                if (Build.VERSION.SDK_INT >= 23)
                                {
                                    checkHarwarePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                                    if (!checkHarwarePermission)
                                    {
                                        checkForFolder();
                                        Toast.makeText(MainActivity.this, "Please grant permission to storage, and try again.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    else
                                    {
                                        if (folder())
                                        {
                                            checkContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
                                            if (!checkContactsPermission)
                                            {
                                                EnableRuntimePermission();
                                                Toast.makeText(MainActivity.this, "Please grant permission to access contacts, and try again.", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            else
                                            {
                                                verifyCode(mobilenumber);
                                                // startActivity(send);
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    folder();
                                    verifyCode(mobilenumber);
                                }
                            }
                        });
                        alert11 = builder1.create();
                        alert11.show();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }
    }
    public Boolean EnableRuntimePermission() {
        Boolean yesorno = false;
        Boolean checkPermission = false;
        if (Build.VERSION.SDK_INT >= 23)
        {
            checkPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        }
        if (!checkPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            return yesorno;
        } else {
            yesorno = true;
            return yesorno;
        }
    }

    private void verifyCode(final String getNumber)
    {
        Intent verify = new Intent(MainActivity.this,verifyNumber.class);
        verify.putExtra("number", getNumber);
        startActivity(verify);
    }
    public boolean checkForFolder()
    {
        Boolean yesorno = false;
        Boolean checkPermission = false;
        if (Build.VERSION.SDK_INT >= 23)
        {
            checkPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        if(!checkPermission)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return yesorno;
        }
        else
        {
            yesorno = true;
            return yesorno;
        }
    }
    public boolean folder()
    {
        File folder = new File(Environment.getExternalStorageDirectory() + "/ChatApp");
        boolean success = false;
        try {
            if (!folder.exists()) {
                folder.mkdir();
                Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.profilepic);
                File file = new File(sd, "/ChatApp/default.png");
                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
                success = true;
                //Toast.makeText(MainActivity.this, "Folder created", Toast.LENGTH_LONG).show();
            } else {
                success = true;
                //Toast.makeText(MainActivity.this, "Folder already exists", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            //Toast.makeText(MainActivity.this, "Folder not created", Toast.LENGTH_LONG).show();
        }
        return success;
    }
}