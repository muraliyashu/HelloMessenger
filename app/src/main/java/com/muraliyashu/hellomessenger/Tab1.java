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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by MuraliYashu on 9/12/2017.
 */

public class Tab1 extends Fragment{

    private ListView chatroomlist;
    private chat_adapter Cadapter;
    private DatabaseReference root;// = FirebaseDatabase.getInstance().getReference().getRoot();
    private ArrayList<String> aList = new ArrayList<String>();
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();
    private ArrayList<String> notiNumbers = new ArrayList<String>();
    private ArrayList<String> notiDisplayNames = new ArrayList<String>();
    final ArrayList<String> tempStatus = new ArrayList<String>();
    private AlertDialog b;
    public boolean dialogLoaded = false;
    private ArrayList<String> newDisplayNames = new ArrayList<String>();
    private ArrayList<String> newImagePaths = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private String getMobileNumber="";
    private String contactNames,contactNumbers;
    private String newString="";
    private ImageView no_chats;
    private TextView no_chatstext;
    public static String usingApp = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1contacts, container, false);
        try
        {
            ShowProgressDialog(getActivity());
            final sqlite_database db = new sqlite_database(getActivity());
            no_chats = (ImageView) rootView.findViewById(R.id.no_chats);
            no_chatstext = (TextView) rootView.findViewById(R.id.no_chatstext);
            chatroomlist = (ListView) rootView.findViewById(R.id.chatroomlist);
            firebaseAuth = FirebaseAuth.getInstance();
            String token = FirebaseInstanceId.getInstance().getToken();
            //Toast.makeText(FirstScreen.this, token, Toast.LENGTH_SHORT).show();
            boolean yesorno = false;
            Bundle extras = getActivity().getIntent().getExtras();
            db.open();

            Cursor curs = db.query("SELECT MOBILE FROM NUMBER");
            while (curs.moveToNext()) {
                newString = curs.getString(0);
            }
            curs.close();

            try {
                String bundleNumber = extras.getString("chatNumber");
                if (bundleNumber != null) {
                    yesorno = true;
                }
            } catch (Exception e) {
                String error = e.getMessage();
            }
            try {
                Cursor loadJsonContacts = db.query("SELECT NUMBER,NAME FROM CONTACTS");
                while (loadJsonContacts.moveToNext()) {
                    contactNumbers = loadJsonContacts.getString(0);
                    contactNames = loadJsonContacts.getString(1);
                }
                loadJsonContacts.close();
            } catch (Exception e) {
                String u = e.getMessage();
            }

            if (yesorno) {
                final String myNumber = extras.getString("chatNumber");
                final String chatNumber = extras.getString("myNumber");
                try {
                    Cursor cursorObject = db.query("SELECT CONTACTNAME,CONTACTNUMBER FROM CONTACTSARRAY");
                    while (cursorObject.moveToNext())
                    {
                        String number = cursorObject.getString(1);
                        notiNumbers.add(number);
                        String name = cursorObject.getString(0);
                        notiDisplayNames.add(name);
                    }
                    cursorObject.close();

                    //notiNumbers = getContactNumbers();
                    //notiDisplayNames = getContactNames();

                    int index = notiNumbers.indexOf(chatNumber);
                    String roomName = notiDisplayNames.get(index);
                    openingChat(roomName, chatNumber, myNumber);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
                }
            } else {
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
                check(token, newString);
            }


            Cadapter = new chat_adapter(getActivity(),newDisplayNames,newImagePaths);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                chatroomlist.setNestedScrollingEnabled(true);
            }
            chatroomlist.setAdapter(Cadapter);

            if (!yesorno)
            {
                //numbers = getContactNumbers();
                //displayNames = getContactNames();


                DatabaseReference rootChildren = FirebaseDatabase.getInstance().getReference().child(newString);
                rootChildren.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            no_chatstext.setVisibility(View.VISIBLE);
                            no_chats.setVisibility(View.VISIBLE);
                            final Set<String> set = new HashSet<String>();
                            //final Set<String> namesSet = new HashSet<String>();
                            //final Set<String> imageSet = new HashSet<String>();
                            final ArrayList<String> namesSet = new ArrayList<String>();
                            final ArrayList<String> imageSet = new ArrayList<String>();
                            Iterator i = dataSnapshot.getChildren().iterator();
                            namesSet.clear();
                            imageSet.clear();
                            numbers = getContactNumbers();
                            displayNames = getContactNames();
                            /*Cursor cursorObject = db.query("SELECT CONTACTNAME,CONTACTNUMBER FROM CONTACTSARRAY");
                            while (cursorObject.moveToNext())
                            {
                                String number = cursorObject.getString(1);
                                numbers.add(number);
                                String name = cursorObject.getString(0);
                                displayNames.add(name);
                            }
                            cursorObject.close();*/
                            if(!i.hasNext())
                            {
                                if(dialogLoaded)
                                {
                                    HideProgressDialog();
                                }
                            }
                            while (i.hasNext())
                            {
                                String user = ((DataSnapshot) i.next()).getKey().toString();
                                //numbers = getContactNumbers();
                                //displayNames = getContactNames();
                                //if (numbers.contains(user))
                                {
                                    //if(!user.equals(newString))
                                    {
                                        //int position = numbers.indexOf(user);
                                        set.add(user);

                                        DatabaseReference rootChildren = FirebaseDatabase.getInstance().getReference().child("profilepictures");
                                        rootChildren.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try {
                                                    Iterator i = dataSnapshot.getChildren().iterator();
                                                    while (i.hasNext())
                                                    {
                                                        String readingNumber = (String) ((DataSnapshot) i.next()).getKey().toString();
                                                        DatabaseReference d = FirebaseDatabase.getInstance().getReference("profilepictures").child(readingNumber);
                                                        d.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                                Iterator iterator2 = dataSnapshot2.getChildren().iterator();
                                                                String Unumber = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                                                String Ustatus = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                                                String Uurl = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                                                String Uname = ((String) ((DataSnapshot) iterator2.next()).getValue());
                                                                if (set.contains(Unumber))
                                                                {
                                                                    int position = numbers.indexOf(Unumber);
                                                                    if(!imageSet.contains(Uurl))
                                                                    {
                                                                        no_chatstext.setVisibility(View.GONE);
                                                                        no_chats.setVisibility(View.GONE);
                                                                        namesSet.add(displayNames.get(position));
                                                                        imageSet.add(Uurl);
                                                                        tempStatus.add(Ustatus);
                                                                    }
                                                                }
                                                                newDisplayNames.clear();
                                                                newImagePaths.clear();
                                                                newDisplayNames.addAll(namesSet);
                                                                newImagePaths.addAll(imageSet);
                                                                if(dialogLoaded)
                                                                {
                                                                    HideProgressDialog();
                                                                }
                                                                Cadapter.notifyDataSetChanged();
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
                                    }
                                }
                            }
                            //aList.clear();
                            //aList.addAll(set);
                            //Cadapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "First Screen", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            chatroomlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    usingApp = "yes";
                    Intent intent = new Intent(getActivity(), chat_room.class);
                    intent.putExtra("room_name", ((TextView) view.findViewById(R.id.onlineName)).getText().toString());
                    int j = displayNames.indexOf(newDisplayNames.get(i));
                    intent.putExtra("status",tempStatus.get(i));
                    intent.putExtra("imagePath",newImagePaths.get(i));
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
    public void check(String token,String getNumber)
    {

        storeToken store = new storeToken(getActivity());
        try
        {
            store.execute(token, getNumber);
        } catch (Exception e)
        {
            Toast.makeText(getActivity(),"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }
    private void openingChat(String roomName, String chatNumber, String myNumber) {

        try {

            Intent intent = new Intent(getActivity(), chat_room.class);
            intent.putExtra("room_name", roomName);
            intent.putExtra("chat_number", chatNumber);
            intent.putExtra("my_number", myNumber);
            getActivity().finish();
            if(chat_room.active)
            {
                chat_room.fa.finish();
            }
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
        }
    }
    public ArrayList getContactNumbers()
    {
        try {
            JSONObject json1 = new JSONObject(contactNumbers);
            JSONArray jArray1 = json1.optJSONArray("uniqueNumbers");
            for (int k = 0; k < jArray1.length(); k++) {
                String str_num = jArray1.optString(k);
                numbers.add(str_num);
            }
        }
        catch (JSONException e)
        {
            String getException = e.getMessage();
        }
        catch (Exception e)
        {
            String getException = e.getMessage();
        }
        return numbers;
    }
    public ArrayList getContactNames()
    {
        try {
            JSONObject json = new JSONObject(contactNames);
            JSONArray jArray = json.optJSONArray("uniqueNames");
            for (int j = 0; j < jArray.length(); j++) {
                String str_name = jArray.optString(j);
                displayNames.add(str_name);
            }
        }
        catch (JSONException e)
        {
            String getException = e.getMessage();
        }
        catch (Exception e)
        {
            String getException = e.getMessage();
        }
        return displayNames;
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