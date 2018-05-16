package com.muraliyashu.hellomessenger;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

public class user_profile extends AppCompatActivity {

    private String getNumber;
    private ImageView profilepicture, editStatus;
    private TextView statusID, numberID;
    final int requestcode = 1, requestCode2 = 2;
    File sd = Environment.getExternalStorageDirectory();
    String packageName, Uname, Uurl;
    private String checkBackPressed = "";
    private ProgressBar rotate;
    File destination;
    private StorageReference mStorageRef;
    private TabbedActivity tabObj;
    private boolean changeProfilePic = false;

    @Override
    public void onStart() {

        super.onStart();
        tabObj.updateStatus("1",getNumber,true);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(!checkBackPressed.equals("yes"))
        {
            tabObj.updateStatus("0",getNumber,true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        tabObj = new TabbedActivity();
        profilepicture = (ImageView)findViewById(R.id.profilepic);
        editStatus = (ImageView)findViewById(R.id.edit_status);
        statusID = (TextView)findViewById(R.id.statusID);
        numberID = (TextView)findViewById(R.id.numberID);
        rotate = (ProgressBar)findViewById(R.id.rotate);
        rotate.setVisibility(View.GONE);
        try {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            Bundle extras = getIntent().getExtras();
            getNumber = (String) extras.get("number");
            DatabaseReference d = FirebaseDatabase.getInstance().getReference("profilepictures").child(getNumber);
            d.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot2) {
                    Iterator iterator2 = dataSnapshot2.getChildren().iterator();
                    String Unumber = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    String Ustatus = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    Uurl = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    Uname = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    loadImage(Uurl, Uname, Unumber);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            profilepicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabObj.updateStatus("0",getNumber,true);
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
                            checkBackPressed = "yes";
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
            editStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater factory = LayoutInflater.from(user_profile.this);
                    final View deleteDialogView = factory.inflate(R.layout.change_status, null);
                    final AlertDialog deleteDialog = new AlertDialog.Builder(user_profile.this).create();
                    deleteDialog.setTitle("Update new status");
                    deleteDialog.setView(deleteDialogView);
                    final EditText status = (EditText)deleteDialogView.findViewById(R.id.oldStatus);
                    final TextView charsLeft = (TextView) deleteDialogView.findViewById(R.id.charsLeft);

                    status.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count)
                        {
                            charsLeft.setVisibility(View.VISIBLE);
                            int getLength = status.getText().toString().length();
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

                    status.setText(Uname);
                    deleteDialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(TextUtils.isEmpty(status.getText().toString().trim()))
                            {
                                Toast.makeText(user_profile.this,"Status cannot be empty",Toast.LENGTH_LONG).show();
                                return;
                            }
                            String newStatus = status.getText().toString();
                            status.setText(newStatus);
                            deleteDialog.dismiss();
                            DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("profilepictures");
                            imageURL url = new imageURL(getNumber,"1",Uurl,newStatus);
                            root1.child(getNumber).setValue(url);
                        }
                    });
                    deleteDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });

                    deleteDialog.show();
                }
            });
        }
        catch (Exception e)
        {
            String getError = e.getMessage();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                checkBackPressed = "yes";
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    void loadImage(String URL, String UNAME, String UNUMBER)
    {
        try
        {
            if(!changeProfilePic)
            {
                Glide.with(getApplicationContext()).load(URL).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        rotate.setVisibility(View.GONE);
                        return false;
                    }
                }).into(profilepicture);
                statusID.setText(UNAME);
                numberID.setText("+91 "+UNUMBER);
            }
        }
        catch (Exception e)
        {
            String getError = e.getMessage();
        }
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
        tabObj.updateStatus("1",getNumber,true);
        if (resultCode == 0)
        {
            rotate.setVisibility(View.GONE);
            return;
        }
        Uri imageUri = data.getData();
        if (requestCode == 1)
        {
            try
            {
                rotate.setVisibility(View.VISIBLE);
                performCrop(imageUri);
            }
            catch(Exception e)
            {

            }
        }
        else if (requestCode == 2)
        {
            changeProfilePic = true;
            rotate.setVisibility(View.VISIBLE);
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
                    //Glide.with(getApplicationContext()).load(destination).into(profilepicture);
                    File newFile = new File(sd, "/ChatApp/"+getNumber+".png");
                    destination.renameTo(newFile);
                    StorageReference riversRef = mStorageRef.child("profilepictures/"+getNumber+".png");
                    Uri uri = Uri.fromFile(newFile);

                    riversRef.putFile(uri)

                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("profilepictures");
                                    imageURL url = new imageURL(getNumber,"1",taskSnapshot.getDownloadUrl().toString(),Uname);
                                    root1.child(getNumber).setValue(url);
                                    changeProfilePic = false;
                                    //rotate.setVisibility(View.GONE);
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
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        checkBackPressed = "yes";
    }
}
