
package com.muraliyashu.hellomessenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by MuraliYashu on 9/12/2017.
 */

public class show_contacts_page extends AppCompatActivity {

    private ListView chatroomlist;
    private contactsAdapter Cadapter;
    private DatabaseReference root;// = FirebaseDatabase.getInstance().getReference().getRoot();
    private ArrayList<String> aList = new ArrayList<String>();
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ArrayList<String> statusArray = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private String numberData;
    private String newString="";
    private TabbedActivity tabObj;
    private String checkBackPressed = "";
    private AlertDialog b;
    public boolean dialogLoaded = false;
    private ImageView no_contacts;
    private TextView no_contactstext;

    @Override
    public void onStart() {
        super.onStart();
        tabObj.updateStatus("1",newString,true);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(!checkBackPressed.equals("yes"))
        {
            tabObj.updateStatus("0",newString,true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_contacts_layout);
        no_contacts = (ImageView) findViewById(R.id.no_contacts);
        no_contactstext = (TextView) findViewById(R.id.no_contactstext);
        //ShowProgressDialog(show_contacts_page.this);
        tabObj = new TabbedActivity();
        setTitle("Select Contact");
        try
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final sqlite_database db = new sqlite_database(show_contacts_page.this);
            chatroomlist = (ListView)findViewById(R.id.statusList);

            firebaseAuth = FirebaseAuth.getInstance();
            db.open();

            Cursor curs = db.query("SELECT MOBILE FROM NUMBER");
            while (curs.moveToNext()) {
                newString = curs.getString(0);
            }
            curs.close();
            root = FirebaseDatabase.getInstance().getReference();
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(newString)) {
                        //root = FirebaseDatabase.getInstance().getReference().child(newString);
                    } else {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(newString, "");
                        root.updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            Cadapter = new contactsAdapter(show_contacts_page.this,names,imagePaths,statusArray);
            chatroomlist.setAdapter(Cadapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                chatroomlist.setNestedScrollingEnabled(true);
            }
            DatabaseReference rootChildren = FirebaseDatabase.getInstance().getReference().child("profilepictures");
            rootChildren.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        //final Set<String> tempNames = new HashSet<String>();
                        //final Set<String> tempPath = new HashSet<String>();
                        final ArrayList<String> tempNames = new ArrayList<String>();
                        final ArrayList<String> tempStatusArray = new ArrayList<String>();
                        final ArrayList<String> tempPath = new ArrayList<String>();
                        no_contactstext.setVisibility(View.VISIBLE);
                        no_contacts.setVisibility(View.VISIBLE);

                        Iterator i = dataSnapshot.getChildren().iterator();

                        Cursor cursorObject = db.query("SELECT CONTACTNAME,CONTACTNUMBER FROM CONTACTSARRAY");
                        while (cursorObject.moveToNext())
                        {
                            String number = cursorObject.getString(1);
                            numbers.add(number);
                            String name = cursorObject.getString(0);
                            displayNames.add(name);
                        }
                        cursorObject.close();
                        while (i.hasNext())
                        {
                            numberData = (String) ((DataSnapshot) i.next()).getKey().toString();
                            DatabaseReference d = FirebaseDatabase.getInstance().getReference("profilepictures").child(numberData);
                            d.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    Iterator iterator2 = dataSnapshot2.getChildren().iterator();
                                    while (iterator2.hasNext())
                                    {
                                    String Unumber = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                    String Ustatus = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                    String Uurl = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                    String Uname = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                    if (numbers.contains(Unumber))
                                    {
                                        if(!tempPath.contains(Uurl))
                                        {
                                            no_contacts.setVisibility(View.GONE);
                                            no_contactstext.setVisibility(View.GONE);
                                            //tempNumber.clear();
                                            //tempNames.clear();
                                            int position = numbers.indexOf(Unumber);
                                            tempNames.add(displayNames.get(position));
                                            tempPath.add(Uurl);
                                            tempStatusArray.add(Uname);
                                            //tempNumber.add(Unumber);
                                        }
                                    }
                                    imagePaths.clear();
                                    names.clear();
                                    statusArray.clear();
                                    imagePaths.addAll(tempPath);
                                    names.addAll(tempNames);
                                    statusArray.addAll(tempStatusArray);
                                    if(dialogLoaded)
                                    {
                                       // HideProgressDialog();
                                    }
                                    Cadapter.notifyDataSetChanged();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    } catch (Exception e) {
                        Toast.makeText(show_contacts_page.this, "First Screen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            chatroomlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    checkBackPressed = "yes";
                    Intent intent = new Intent(show_contacts_page.this, chat_room.class);
                    intent.putExtra("room_name", ((TextView) view.findViewById(R.id.onlineName)).getText().toString());
                    int j = displayNames.indexOf(names.get(i));
                    intent.putExtra("chat_number", numbers.get(j));
                    intent.putExtra("my_number", newString);
                    startActivity(intent);
                    finish();
                }
            });
        }
        catch(Exception e)
        {
            String u = e.getMessage();
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
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        checkBackPressed = "yes";
    }
    public void ShowProgressDialog(Context cntx) {
        dialogLoaded = true;
        AlertDialog.Builder dialogBuilder;
        dialogBuilder = new AlertDialog.Builder(cntx);
        LayoutInflater inflater = (LayoutInflater) cntx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        b = dialogBuilder.create();
        b.show();
    }

    public void HideProgressDialog()
    {
        dialogLoaded = false;
        b.dismiss();
    }
}