package com.muraliyashu.hellomessenger;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class initialProfile extends AppCompatActivity {

    private String getNumber;
    private ImageView profilepic;
    final int requestcode = 1, requestCode2 = 2;
    File sd = Environment.getExternalStorageDirectory();
    String packageName;
    private EditText profileName;
    private TextView charsLeft;
    private Button next;
    private StorageReference mStorageRef;
    private DatabaseReference root;
    private DatabaseReference rootChild;
    private String getFullName;
    private boolean imageChosen = false;
    File destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_profile);
        Bundle extras = getIntent().getExtras();
        getNumber = (String)extras.get("number");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        profilepic = (ImageView) findViewById(R.id.profilepic);
        profileName = (EditText) findViewById(R.id.profilename);
        next = (Button) findViewById(R.id.next);
        charsLeft = (TextView) findViewById(R.id.charsLeft);
        Glide.with(initialProfile.this).load(R.drawable.profilepic).into(profilepic);
        charsLeft.setVisibility(View.GONE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (TextUtils.isEmpty(profileName.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), "Status cannnot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                try
                {
                    File newFile;
                    getFullName = profileName.getText().toString();
                    getFullName = getFullName.replaceAll(" ","!@#$");
                    if(imageChosen)
                    {
                        newFile = new File(sd, "/ChatApp/"+getNumber+".png");
                        destination.renameTo(newFile);
                    }
                    else
                    {
                        destination = new File(sd, "/ChatApp/"+getNumber+".png");
                        if (destination.exists()) destination.delete();
                        try
                        {
                            FileOutputStream out = new FileOutputStream(destination);
                            Bitmap bm = BitmapFactory.decodeResource( getResources(), R.drawable.profilepic);
                            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        newFile = destination;
                    }

                    StorageReference riversRef = mStorageRef.child("profilepictures/"+getNumber+".png");
                    Uri uri = Uri.fromFile(newFile);

                    riversRef.putFile(uri)

                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("profilepictures");
                                    imageURL url = new imageURL(getNumber,"1",taskSnapshot.getDownloadUrl().toString(),getFullName);
                                    root1.child(getNumber).setValue(url);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    // ...
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                    //dialog.setMessage("Loading... "+(int)progress+"%");
                                }
                            });

                    DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("profiles");
                    profiles profileObj = new profiles(getFullName,getNumber);
                    root1.child(getNumber).setValue(profileObj);
                    Intent intent = new Intent(initialProfile.this, load_contacts_activity.class);
                    intent.putExtra("number",getNumber);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count)
            {
                charsLeft.setVisibility(View.VISIBLE);
                int getLength = profileName.getText().toString().length();
                if(getLength>0)
                {
                    charsLeft.setText(String.valueOf(140-getLength));
                }
                else
                {
                    charsLeft.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                shareIntent.setType("image/*");
                ResolveInfo defaultLauncher =getPackageManager().resolveActivity(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                String defaultLauncherStr = defaultLauncher.activityInfo.packageName;
                List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
                try{
                    if (!resInfo.isEmpty()) {
                        for (ResolveInfo resolveInfo : resInfo) {
                            String classname = resolveInfo.activityInfo.name;
                            ApplicationInfo abcd = resolveInfo.activityInfo.applicationInfo;
                            if (classname.toLowerCase().contains("gallery".toLowerCase()))
                            {
                                packageName = resolveInfo.activityInfo.packageName;
                                shareIntent.setPackage(packageName);
                            } else
                            {

                            }
                        }
                        startActivityForResult(Intent.createChooser(shareIntent, "Select Picture"), requestcode);
                    }}
                catch(Exception e)
                {
                    String a = e.getMessage();
                    String ab = e.getMessage();
                    String abc = e.getMessage();
                }
            }
        });
    }
    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.setPackage(packageName);
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, requestCode2);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0)
        {
            return;
        }
        Uri imageUri = data.getData();
        if (requestCode == 1)
        {
            try
            {
                performCrop(imageUri);
            }
            catch(Exception e)
            {

            }
        }
        else if (requestCode == 2)
        {
            MainActivity obj = new MainActivity();
            obj.folder();
            destination = new File(sd, "/ChatApp/"+getNumber+System.currentTimeMillis()+".png");
            if (destination.exists()) destination.delete();
            try
            {
                FileOutputStream out = new FileOutputStream(destination);
                Bundle extras = data.getExtras();
                Bitmap avatar = extras.getParcelable("data");
                avatar.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                if (destination.exists())
                {
                    Glide.with(initialProfile.this).load(destination).into(profilepic);
                }
                imageChosen = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
