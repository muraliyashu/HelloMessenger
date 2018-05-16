package com.muraliyashu.hellomessenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class chat_room extends AppCompatActivity {

    private ListView chat_conversation;
    private EditText enter_text;
    private ImageView send;
    private String room_name;
    private String chat_number,my_number;
    private adapter_list adapter;
    private ArrayList<String> aList = new ArrayList<String>();
    private ArrayList<String> temp = new ArrayList<String>();
    private ArrayList<String> aryDate = new ArrayList<String>();
    private ArrayList<String> aryImages = new ArrayList<String>();
    private ArrayList<String> aryIDs = new ArrayList<String>();
    private ArrayList<String> messageSeenArray = new ArrayList<String>();
    private DatabaseReference root;
    private DatabaseReference rootChild;
    private DatabaseReference copyroot;
    private DatabaseReference copyrootChild;
    private String unique_key;
    private Bitmap avatar;
    private StorageReference mStorageRef;
    private ImageView back,profilepic;
    //int uniqueName;
    File sd = Environment.getExternalStorageDirectory();
    static String setImageName;
    //File destination = new File(sd, "/ChatApp/"+setImageName);
    private ImageView sendImage;
    private String packageName="";
    static boolean active = false;
    final int requestcode = 1, requestCode2 = 2;
    byte[] imageBytes;
    static String chatNumber = "";
    public static Activity fa;
    private TextView userName, userOnlineStatus;
    public static String myNumber,getIntentStatus,getIntentImage;
    private TabbedActivity tabObj;
    private String checkBackPressed = "";
    private boolean checkforSentMessage = false;

    @Override
    public void onStart() {
        chatNumber = chat_number;
        super.onStart();
        active = true;
        tabObj.updateStatus("1",my_number,true);
    }
    @Override
    public void onStop() {
        chatNumber="";
        super.onStop();
        active = false;
        if(!checkBackPressed.equals("yes"))
        {
            checkBackPressed="";
            tabObj.updateStatus("0",my_number,true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            fa = this;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_room);
            tabObj = new TabbedActivity();
            room_name = getIntent().getExtras().get("room_name").toString();
            chat_number = getIntent().getExtras().get("chat_number").toString();
            my_number = getIntent().getExtras().get("my_number").toString();

            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.custom_chat_actionbar);

            getSupportActionBar().getCustomView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBackPressed = "yes";
                    Intent intent = new Intent(chat_room.this,show_user_profile.class);
                    intent.putExtra("number", chat_number);
                    intent.putExtra("mynumber", my_number);
                    intent.putExtra("name", room_name);
                    startActivity(intent);
                }
            });

            back = (ImageView) findViewById(R.id.backbutton);

            profilepic = (ImageView) findViewById(R.id.profilepic);
            userName = (TextView) findViewById(R.id.userName);
            userOnlineStatus = (TextView) findViewById(R.id.userOnlineStatus);

            userName.setText(room_name);

            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBackPressed = "yes";
                    finish();
                }
            });


            DatabaseReference d = FirebaseDatabase.getInstance().getReference("profilepictures").child(chat_number);
            d.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot2) {
                    Iterator iterator2 = dataSnapshot2.getChildren().iterator();
                    String Unumber = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    String Ustatus = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    String Uimage = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    String Uname = ((String) ((DataSnapshot) iterator2.next()).getValue());
                    setActionBar(Uimage,Ustatus);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




            try
            {
                mStorageRef = FirebaseStorage.getInstance().getReference();}
            catch (Exception e)
            {
                String o = "k";
            }
            chat_conversation = (ListView) findViewById(R.id.conversation);
            chat_conversation.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
            chat_conversation.setStackFromBottom(true);
            send = (ImageView) findViewById(R.id.addchat);
            enter_text = (EditText) findViewById(R.id.editchat);
            sendImage = (ImageView)findViewById(R.id.sendImage);

            room_name = getIntent().getExtras().get("room_name").toString();
            chat_number = getIntent().getExtras().get("chat_number").toString();
            my_number = getIntent().getExtras().get("my_number").toString();
            myNumber=my_number;chatNumber=chat_number;
            room_name = room_name.substring(0, 1).toUpperCase() + room_name.substring(1).toLowerCase();
            setTitle(room_name);

            root = FirebaseDatabase.getInstance().getReference().child(my_number);
            rootChild = root.child(chat_number);
            copyroot = FirebaseDatabase.getInstance().getReference().child(chat_number);
            copyrootChild = copyroot.child(my_number);

            sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tabObj.updateStatus("0",my_number,true);
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



            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                            if(enter_text.getText().toString().trim().length()==0)
                            {
                                Toast.makeText(chat_room.this,"Text cannnot be empty",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final Calendar c = Calendar.getInstance();
                            final int mHour = c.get(Calendar.HOUR_OF_DAY);
                            final int mMinute = c.get(Calendar.MINUTE);
                            final int mYear = c.get(Calendar.YEAR);
                            final int mMonth = c.get(Calendar.MONTH);
                            final int mDay = c.get(Calendar.DAY_OF_MONTH);
                            String date = mDay+"-"+(mMonth+1)+"-"+mYear;
                            int copyHour;
                            copyHour=mHour;
                            String format;
                            if (copyHour == 0) {
                                copyHour += 12;
                                format = "AM";
                            } else if (copyHour == 12) {
                                format = "PM";
                            } else if (copyHour > 12) {
                                copyHour -= 12;
                                format = "PM";
                            } else {
                                format = "AM";
                            }
                            String hourappend = String.valueOf(copyHour);
                            String minuteappend = String.valueOf(mMinute);

                            if (hourappend.length() == 1) {
                                hourappend = "0"+hourappend;
                            }
                            if (minuteappend.length() == 1) {
                                minuteappend = "0"+minuteappend;
                            }

                            String getTime = hourappend+ ": "+minuteappend+" "+format;

                            unique_key = rootChild.push().getKey();
                            message messageObject = new message(enter_text.getText().toString()+"!@#$%"+date+" / "+getTime,my_number+"!@#$%"+"sent",unique_key);
                            rootChild.child(unique_key).setValue(messageObject);



                            message copyMessageObject = new message(enter_text.getText().toString()+"!@#$%"+date+" / "+getTime,my_number+"!@#$%"+"sent",unique_key);
                            copyrootChild.child(unique_key).setValue(copyMessageObject);

                            SendNotification notify = new SendNotification(chat_room.this);
                            String copy = enter_text.getText().toString();
                            enter_text.setText("");
                            String sendingString = chat_number + "$%^" + my_number + "$%^" + copy;
                            try {
                                notify.execute(sendingString, chat_number);
                            } catch (Exception e) {

                            }
                    } catch (Exception e) {
                        Toast.makeText(chat_room.this, "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            /*rootChild.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    append_chat_conversation(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            rootChild.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //last_seen_update(dataSnapshot);

                    try {
                        Iterator i = dataSnapshot.getChildren().iterator();
                        while (i.hasNext()) {
                            String receivedMessage = (String) ((DataSnapshot) i.next()).getValue();
                            String uniqueID = (String) ((DataSnapshot) i.next()).getValue();
                            String receivedUserName = (String) ((DataSnapshot) i.next()).getValue();
                            String[] splitNumberReceived = receivedUserName.split(Pattern.quote("!@#$%"));
                            String splitNumber = splitNumberReceived[0];
                            String splitReceived = splitNumberReceived[1];

                            if (!splitNumber.equals(my_number)) {
                                final Calendar c = Calendar.getInstance();
                                final int mHour = c.get(Calendar.HOUR_OF_DAY);
                                final int mMinute = c.get(Calendar.MINUTE);
                                final int mYear = c.get(Calendar.YEAR);
                                final int mMonth = c.get(Calendar.MONTH);
                                final int mDay = c.get(Calendar.DAY_OF_MONTH);
                                String date = mDay + "-" + (mMonth + 1) + "-" + mYear;
                                int copyHour;
                                copyHour = mHour;
                                String format;
                                if (copyHour == 0) {
                                    copyHour += 12;
                                    format = "AM";
                                } else if (copyHour == 12) {
                                    format = "PM";
                                } else if (copyHour > 12) {
                                    copyHour -= 12;
                                    format = "PM";
                                } else {
                                    format = "AM";
                                }
                                String hourappend = String.valueOf(copyHour);
                                String minuteappend = String.valueOf(mMinute);

                                if (hourappend.length() == 1) {
                                    hourappend = "0" + hourappend;
                                }
                                if (minuteappend.length() == 1) {
                                    minuteappend = "0" + minuteappend;
                                }
                                String getTime = hourappend + ": " + minuteappend + " " + format;
                                if (splitReceived.equals("sent")) {
                                    message messageObject = new message(receivedMessage, splitNumber + "!@#$%" + date + " / " + getTime, uniqueID);
                                    rootChild.child(uniqueID).setValue(messageObject);
                                    copyrootChild.child(uniqueID).setValue(messageObject);
                                    checkforSentMessage = true;
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(chat_room.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
                    }

                    append_chat_conversation(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //last_seen_update(dataSnapshot);

                    try {
                        Iterator i = dataSnapshot.getChildren().iterator();
                        while (i.hasNext()) {
                            String receivedMessage = (String) ((DataSnapshot) i.next()).getValue();
                            String uniqueID = (String) ((DataSnapshot) i.next()).getValue();
                            String receivedUserName = (String) ((DataSnapshot) i.next()).getValue();
                            String[] splitNumberReceived = receivedUserName.split(Pattern.quote("!@#$%"));
                            String splitNumber = splitNumberReceived[0];
                            String splitReceived = splitNumberReceived[1];

                            if (!splitNumber.equals(my_number)) {
                                final Calendar c = Calendar.getInstance();
                                final int mHour = c.get(Calendar.HOUR_OF_DAY);
                                final int mMinute = c.get(Calendar.MINUTE);
                                final int mYear = c.get(Calendar.YEAR);
                                final int mMonth = c.get(Calendar.MONTH);
                                final int mDay = c.get(Calendar.DAY_OF_MONTH);
                                String date = mDay + "-" + (mMonth + 1) + "-" + mYear;
                                int copyHour;
                                copyHour = mHour;
                                String format;
                                if (copyHour == 0) {
                                    copyHour += 12;
                                    format = "AM";
                                } else if (copyHour == 12) {
                                    format = "PM";
                                } else if (copyHour > 12) {
                                    copyHour -= 12;
                                    format = "PM";
                                } else {
                                    format = "AM";
                                }
                                String hourappend = String.valueOf(copyHour);
                                String minuteappend = String.valueOf(mMinute);

                                if (hourappend.length() == 1) {
                                    hourappend = "0" + hourappend;
                                }
                                if (minuteappend.length() == 1) {
                                    minuteappend = "0" + minuteappend;
                                }
                                String getTime = hourappend + ": " + minuteappend + " " + format;
                                if (splitReceived.equals("sent")) {
                                    message messageObject = new message(receivedMessage, splitNumber + "!@#$%" + date + " / " + getTime, uniqueID);
                                    rootChild.child(uniqueID).setValue(messageObject);
                                    copyrootChild.child(uniqueID).setValue(messageObject);
                                    checkforSentMessage = true;
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(chat_room.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
                    }
                    append_chat_conversation(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    adapter.notifyDataSetChanged();
                    myDBRoot(myNumber);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            chat_conversation.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final int position = i;
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    AlertDialog.Builder builder = new AlertDialog.Builder(chat_room.this);

                    builder.setTitle("Delete Message?");
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            deleteMessage(aryIDs.get(position),position);
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(chat_room.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }
    private void setActionBar(String Image, String Online)
    {
        if(Online.equals("1"))
        {
            userOnlineStatus.setText("Online");
            Glide.with(getApplicationContext()).load(Image).into(profilepic);
        }
        else
        {
            userOnlineStatus.setText("Offline");
            Glide.with(getApplicationContext()).load(Image).into(profilepic);
        }
    }

    private void append_chat_conversation(DataSnapshot dataSnapshot)
    {
        try
        {
                String userName = "";
                Iterator i = dataSnapshot.getChildren().iterator();
            if(!checkforSentMessage)
            {
                while (i.hasNext())
                {
                    String receivedMessage = (String) ((DataSnapshot) i.next()).getValue();
                    String uniqueID = (String) ((DataSnapshot) i.next()).getValue();
                    aryIDs.add(uniqueID);
                    String receivedUserName = (String) ((DataSnapshot) i.next()).getValue();
                    String[] splitMessage = receivedMessage.split(Pattern.quote("!@#$%"));
                    String splitMsg = splitMessage[0];
                    String splitDate = splitMessage[1];
                    aryDate.add(splitDate);

                    String[] splitNumberReceived = receivedUserName.split(Pattern.quote("!@#$%"));
                    String splitNumber = splitNumberReceived[0];
                    String splitReceived = splitNumberReceived[1];

                    if (splitNumber.equals(my_number))
                    {
                        messageSeenArray.add(splitReceived);
                        userName = "me";
                        if(splitMsg.startsWith("https://firebasestorage.googleapis.com"))
                        {
                            aList.add("0");
                            String[] imgMessage = splitMsg.split(Pattern.quote("^&"));
                            String imgMsg = imgMessage[0];
                            String imgName = imgMessage[1];

                            File image = new File(Environment.getExternalStorageDirectory() + "/ChatApp/"+imgName+".png");
                            if (!image.exists())
                            {
                                aryImages.add(imgName);
                            }
                            else
                            {
                                aryImages.add(image.getPath().toString());
                            }
                        }
                        else
                        {
                            aList.add(splitMsg);
                            aryImages.add("0");
                        }
                    }
                    else
                    {
                        messageSeenArray.add(splitReceived);
                        userName = room_name;
                        if(splitMsg.startsWith("https://firebasestorage.googleapis.com"))
                        {
                            aList.add("0");
                            String[] imgMessage = splitMsg.split(Pattern.quote("^&"));
                            String imgMsg = imgMessage[0];
                            String imgName = imgMessage[1];

                            File image = new File(Environment.getExternalStorageDirectory() + "/ChatApp/"+imgName+".png");
                            if (!image.exists())
                            {
                                aryImages.add(imgName);
                            }
                            else
                            {
                                aryImages.add(image.getPath().toString());
                            }
                        }
                        else
                        {
                            aList.add(splitMsg);
                            aryImages.add("0");
                        }
                    }
                    temp.add(userName);
                    //adapter.notifyDataSetChanged();
                }
            }
            else
            {
                checkforSentMessage=false;
            }

                if (adapter == null)
                {
                    adapter = new adapter_list(chat_room.this,aList,temp,aryDate,aryImages,aryIDs,myNumber,chatNumber,messageSeenArray);
                } else
                {
                    //((BaseAdapter) chat_conversation.getAdapter()).notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }

                chat_conversation.setAdapter(adapter);
        }
        catch (Exception e)
        {
            Toast.makeText(chat_room.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }

    private void last_seen_update(DataSnapshot dataSnapshot)
    {
        try {
            Iterator i = dataSnapshot.getChildren().iterator();
            while (i.hasNext()) {
                String receivedMessage = (String) ((DataSnapshot) i.next()).getValue();
                String uniqueID = (String) ((DataSnapshot) i.next()).getValue();
                String receivedUserName = (String) ((DataSnapshot) i.next()).getValue();
                String[] splitNumberReceived = receivedUserName.split(Pattern.quote("!@#$%"));
                String splitNumber = splitNumberReceived[0];
                String splitReceived = splitNumberReceived[1];

                if (splitNumber.equals(my_number)) {
                    final Calendar c = Calendar.getInstance();
                    final int mHour = c.get(Calendar.HOUR_OF_DAY);
                    final int mMinute = c.get(Calendar.MINUTE);
                    final int mYear = c.get(Calendar.YEAR);
                    final int mMonth = c.get(Calendar.MONTH);
                    final int mDay = c.get(Calendar.DAY_OF_MONTH);
                    String date = mDay + "-" + (mMonth + 1) + "-" + mYear;
                    int copyHour;
                    copyHour = mHour;
                    String format;
                    if (copyHour == 0) {
                        copyHour += 12;
                        format = "AM";
                    } else if (copyHour == 12) {
                        format = "PM";
                    } else if (copyHour > 12) {
                        copyHour -= 12;
                        format = "PM";
                    } else {
                        format = "AM";
                    }
                    String hourappend = String.valueOf(copyHour);
                    String minuteappend = String.valueOf(mMinute);

                    if (hourappend.length() == 1) {
                        hourappend = "0" + hourappend;
                    }
                    if (minuteappend.length() == 1) {
                        minuteappend = "0" + minuteappend;
                    }
                    String getTime = hourappend + ": " + minuteappend + " " + format;
                    if (splitReceived.equals("sent")) {
                        message messageObject = new message(receivedMessage, splitNumber + "!@#$%" + date + " / " + getTime, uniqueID);
                        rootChild.child(uniqueID).setValue(messageObject);
                        copyrootChild.child(uniqueID).setValue(messageObject);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(chat_room.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        tabObj.updateStatus("1",my_number,true);
        if (resultCode == 0)
        {
            Toast.makeText(chat_room.this, "Cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == 1) {

            try
            {
                Uri imageUri = data.getData();
                performCrop(imageUri);
            }
            catch(Exception e)
            {

            }
        }
        else if (requestCode == 2) {

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Sending image");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Bundle extras = data.getExtras();

                try {
                    File imagePath = new File(sd, "/ChatApp/"+setImageName+".png");
                    Uri uri = Uri.fromFile(imagePath);

                    StorageReference riversRef = mStorageRef.child("images/"+setImageName+".png");

                    riversRef.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                Map<String, Object> map = new HashMap<String, Object>();
                                unique_key = rootChild.push().getKey();
                                root.updateChildren(map);

                                final Calendar c = Calendar.getInstance();
                                final int mHour = c.get(Calendar.HOUR_OF_DAY);
                                final int mMinute = c.get(Calendar.MINUTE);
                                final int mYear = c.get(Calendar.YEAR);
                                final int mMonth = c.get(Calendar.MONTH);
                                final int mDay = c.get(Calendar.DAY_OF_MONTH);
                                String date = mDay+"-"+(mMonth+1)+"-"+mYear;
                                int copyHour;
                                copyHour=mHour;
                                String format;
                                if (copyHour == 0) {
                                    copyHour += 12;
                                    format = "AM";
                                } else if (copyHour == 12) {
                                    format = "PM";
                                } else if (copyHour > 12) {
                                    copyHour -= 12;
                                    format = "PM";
                                } else {
                                    format = "AM";
                                }
                                String hourappend = String.valueOf(copyHour);
                                String minuteappend = String.valueOf(mMinute);

                                if (hourappend.length() == 1) {
                                    hourappend = "0"+hourappend;
                                }
                                if (minuteappend.length() == 1) {
                                    minuteappend = "0"+minuteappend;
                                }

                                String getTime = hourappend+ ": "+minuteappend+" "+format;

                                unique_key = rootChild.push().getKey();
                                message imageObject = new message(downloadUrl+"^&"+setImageName+"!@#$%"+date+" / "+getTime,my_number+"!@#$%"+"sent",unique_key);
                                rootChild.child(unique_key).setValue(imageObject);

                                copyroot = FirebaseDatabase.getInstance().getReference().child(chat_number);
                                copyrootChild = copyroot.child(my_number);

                                unique_key = copyrootChild.push().getKey();
                                message copyImageObject = new message(downloadUrl+"^&"+setImageName+"!@#$%"+date+" / "+getTime,my_number+"!@#$%"+"sent",unique_key);
                                copyrootChild.child(unique_key).setValue(copyImageObject);

                                SendNotification notify = new SendNotification(chat_room.this);
                                String sendingString = chat_number + "$%^" + my_number + "$%^" + "Image Received";
                                try {
                                    notify.execute(sendingString, chat_number);
                                } catch (Exception e) {

                                }

                                dialog.dismiss();
                                Toast.makeText(chat_room.this,"Image sent succesfully",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                dialog.dismiss();
                                Toast.makeText(chat_room.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Sending "+(int)progress+"%");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        else if(requestCode == 3)
        {
            aList.clear();temp.clear();aryDate.clear();aryImages.clear();aryIDs.clear();
            aList.addAll(data.getStringArrayListExtra("messages"));
            temp.addAll(data.getStringArrayListExtra("names"));
            aryDate.addAll(data.getStringArrayListExtra("dates"));
            aryImages.addAll(data.getStringArrayListExtra("images"));
            aryIDs.addAll(data.getStringArrayListExtra("id"));
            adapter.notifyDataSetChanged();
            //deleteMessage(data.getStringExtra("id"),Integer.getInteger(data.getStringExtra("position")));
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.setPackage(packageName);
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            //cropIntent.putExtra("aspectX", 1);
            //cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            //cropIntent.putExtra("outputX", 800);
            //cropIntent.putExtra("outputY", 800);
            // retrieve data on return
            //cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            //startActivityForResult(cropIntent, requestCode2);

            setImageName = String.valueOf((int)System.currentTimeMillis());
            File destination = new File(sd, "/ChatApp/"+setImageName+".png");
            //if (destination.exists()) destination.delete();
            {
                //File f = new File(destination,"/temporary_holder.jpg");
                try {
                    destination.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                Uri uri = Uri.fromFile(destination);

                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(cropIntent, requestCode2);
            }
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void deleteMessage(String messageID, int position)
    {
        DatabaseReference drMessage = rootChild.child(messageID);
        drMessage.removeValue();
        aList.remove(position);
        temp.remove(position);
        aryDate.remove(position);
        aryImages.remove(position);
        aryIDs.remove(position);
        //adapter.notifyDataSetChanged();
        Toast.makeText(this,"Deleted Successfully",Toast.LENGTH_LONG).show();
    }
    public void myDBRoot(String newString)
    {
        final String myRoot = newString;
        root = FirebaseDatabase.getInstance().getReference();
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(!snapshot.hasChild(myRoot))
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(myRoot, "");
                    root.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        checkBackPressed = "yes";
    }
}






