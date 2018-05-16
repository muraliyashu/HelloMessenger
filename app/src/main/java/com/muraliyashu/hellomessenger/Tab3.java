package com.muraliyashu.hellomessenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Tab3 extends Fragment{

    private ListView chatroomlist;
    private statusAdapter statusadapter;
    private DatabaseReference root;// = FirebaseDatabase.getInstance().getReference().getRoot();
    private ArrayList<String> aList = new ArrayList<String>();
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();
    private ArrayList<String> namesForStatus = new ArrayList<String>();
    private ArrayList<String> statusDetails = new ArrayList<String>();
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ArrayList<String> muraliTest = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private String numberData;
    private String newString="";
    private ImageView offlineimage;
    private TextView offlinetext;
    public static String usingApp = "";
    private AlertDialog b;
    public boolean dialogLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3online, container, false);
        try
        {
            //ShowProgressDialog(getActivity());
            final sqlite_database db = new sqlite_database(getActivity());
            chatroomlist = (ListView) rootView.findViewById(R.id.statusList);
            offlineimage = (ImageView) rootView.findViewById(R.id.offlineimage);
            offlinetext = (TextView) rootView.findViewById(R.id.offlinetext);

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
            statusadapter = new statusAdapter(getActivity(),namesForStatus,statusDetails,imagePaths);
            chatroomlist.setAdapter(statusadapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                chatroomlist.setNestedScrollingEnabled(true);
            }

            DatabaseReference rootChildren = FirebaseDatabase.getInstance().getReference().child("profilepictures");
            rootChildren.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        //final Set<String> tempNames = new HashSet<String>();
                        final ArrayList<String> tempNames = new ArrayList<String>();
                        final ArrayList<String> tempStatus = new ArrayList<String>();
                        final ArrayList<String> tempPath = new ArrayList<String>();
                        //final Set<String> tempStatus = new HashSet<String>();
                        //final Set<String> tempPath = new HashSet<String>();

                        offlinetext.setVisibility(View.VISIBLE);
                        offlineimage.setVisibility(View.VISIBLE);
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
                                    while(iterator2.hasNext())
                                    {
                                        String Unumber = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                        String Ustatus = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                        String Uurl = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                        String Uname = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                        if (numbers.contains(Unumber))
                                        {
                                            if(Ustatus.equals("1"))
                                            {
                                                offlinetext.setVisibility(View.GONE);
                                                offlineimage.setVisibility(View.GONE);
                                                if(!tempPath.contains(Uurl))
                                                {
                                                    int position = numbers.indexOf(Unumber);
                                                    tempStatus.add(Ustatus);
                                                    tempNames.add(displayNames.get(position));
                                                    tempPath.add(Uurl);
                                                    // tempNumber.add(Unumber);
                                                }
                                            }
                                        }
                                        imagePaths.clear();
                                        namesForStatus.clear();
                                        statusDetails.clear();

                                        imagePaths.addAll(tempPath);
                                        statusDetails.addAll(tempStatus);
                                        namesForStatus.addAll(tempNames);
                                        if(dialogLoaded)
                                        {
                                           // HideProgressDialog();
                                        }
                                        statusadapter.notifyDataSetChanged();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "First Screen", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            chatroomlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    usingApp = "yes";
                    Intent intent = new Intent(getActivity(), chat_room.class);
                    intent.putExtra("room_name", ((TextView) view.findViewById(R.id.onlineName)).getText().toString());
                    int j = displayNames.indexOf(namesForStatus.get(i));
                    intent.putExtra("chat_number", numbers.get(j));
                    intent.putExtra("my_number", newString);
                    startActivity(intent);
                }
            });
        }
        catch(Exception e)
        {
            String u = e.getMessage();
        }
        return rootView;
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