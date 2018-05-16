
package com.muraliyashu.hellomessenger;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Iterator;

import static com.muraliyashu.hellomessenger.sqlite_database.context;

public class show_user_profile extends AppCompatActivity {

    private String getNumber, getName;
    private ImageView profilepicture;
    private TextView statusID, numberID, onlineStatusID, contactNameID;
    File sd = Environment.getExternalStorageDirectory();
    String Uname, Uurl, getmynumber;
    private TabbedActivity tabObj;
    private ProgressBar rotate;
    File destination;
    private StorageReference mStorageRef;
    private String checkBackPressed = "";


    @Override
    public void onStart() {
        super.onStart();
        tabObj.updateStatus("1",getmynumber,true);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(!checkBackPressed.equals("yes"))
        {
            tabObj.updateStatus("0",getmynumber,true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);
        tabObj = new TabbedActivity();
        profilepicture = (ImageView)findViewById(R.id.profilepic);
        statusID = (TextView)findViewById(R.id.statusID);
        numberID = (TextView)findViewById(R.id.numberID);
        onlineStatusID = (TextView)findViewById(R.id.onlineStatus);
        contactNameID = (TextView)findViewById(R.id.contactName);
        rotate = (ProgressBar)findViewById(R.id.rotate);
        rotate.setVisibility(View.GONE);
        try {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mStorageRef = FirebaseStorage.getInstance().getReference();
            Bundle extras = getIntent().getExtras();
            getNumber = (String) extras.get("number");
            getmynumber = (String) extras.get("mynumber");
            getName = (String) extras.get("name");
            DatabaseReference d = FirebaseDatabase.getInstance().getReference("profilepictures").child(getNumber);
            d.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot2) {
                    Iterator iterator2 = dataSnapshot2.getChildren().iterator();
                    String Unumber = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    String Ustatus = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    Uurl = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    Uname = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    loadImage(Uurl, Uname, Unumber, getName, Ustatus);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            profilepicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(show_user_profile.this);
                    builder1.setTitle("Download?")
                            .setMessage("Do you want to download "+getName+"'s profile picture?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    final ProgressDialog downloaddialog = new ProgressDialog(show_user_profile.this);
                                    downloaddialog.setTitle("Downloading image");
                                    downloaddialog.setCanceledOnTouchOutside(false);
                                    downloaddialog.show();
                                    StorageReference islandRef = mStorageRef.child("profilepictures/"+getNumber+".png");
                                    final File file = new File(Environment.getExternalStorageDirectory(), "/ChatApp/"+getNumber+".png");

                                    islandRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            // Local temp file has been created
                                            downloaddialog.dismiss();
                                            Toast.makeText(context,"Image downloaded successfully",Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception)
                                        {
                                            downloaddialog.dismiss();
                                            Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
                                            // Handle any errors
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                                                    downloaddialog.setMessage("Downloading "+(int)progress+"%");
                                                }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(R.drawable.download_profile_picture)
                            .show();
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
    void loadImage(String URL, String USTATUS, String UNUMBER, String UNAME, String UONLINE)
    {
        try
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
            if(UONLINE.equals("0"))
            {
                onlineStatusID.setText("Offline");
            }
            else
            {
                onlineStatusID.setText("Online");
            }
            statusID.setText(USTATUS);
            contactNameID.setText(UNAME);
            numberID.setText("+91 "+UNUMBER);
        }
        catch (Exception e)
        {
            String getError = e.getMessage();
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        checkBackPressed = "yes";
    }
}
