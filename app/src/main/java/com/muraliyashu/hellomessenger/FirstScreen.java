package com.muraliyashu.hellomessenger;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import static com.muraliyashu.hellomessenger.sqlite_database.context;

public class FirstScreen extends AppCompatActivity
{
    private ListView chatroomlist;
    private ArrayAdapter adapter;
    private DatabaseReference root;// = FirebaseDatabase.getInstance().getReference().getRoot();
    private ArrayList<String> aList = new ArrayList<String>();
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();
    private ArrayList<String> notiNumbers = new ArrayList<String>();
    private ArrayList<String> notiDisplayNames = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private String getMobileNumber="";
    private String contactNames,contactNumbers;
    final sqlite_database db = new sqlite_database(FirstScreen.this);
    private String newString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.screen_first);

            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c2dfe5")));

            chatroomlist = (ListView) findViewById(R.id.chatroomlist);
            firebaseAuth = FirebaseAuth.getInstance();
            String token = FirebaseInstanceId.getInstance().getToken();

            //Toast.makeText(FirstScreen.this, token, Toast.LENGTH_SHORT).show();
            boolean yesorno = false;
            Bundle extras = getIntent().getExtras();
            db.open();

            Cursor curs = db.query("SELECT MOBILE FROM NUMBER");
            while (curs.moveToNext())
            {
                newString = curs.getString(0);
            }
            curs.close();

            try
            {
                String bundleNumber = extras.getString("chatNumber");
                if (bundleNumber != null)
                {
                    yesorno = true;
                }
            }
            catch (Exception e)
            {
                String error = e.getMessage();
            }
            try
            {
                Cursor loadJsonContacts = db.query("SELECT NUMBER,NAME FROM CONTACTS");
                while (loadJsonContacts.moveToNext())
                {
                    contactNumbers = loadJsonContacts.getString(0);
                    contactNames = loadJsonContacts.getString(1);
                }
                loadJsonContacts.close();
            }
            catch(Exception e)
            {
                String u = e.getMessage();
            }

            if (yesorno) {
                final String myNumber = extras.getString("chatNumber");
                final String chatNumber = extras.getString("myNumber");
                try{
                    notiNumbers = getContactNumbers();
                    notiDisplayNames = getContactNames();
                    int index = notiNumbers.indexOf(chatNumber);
                    String roomName = notiDisplayNames.get(index);
                    openingChat(roomName, chatNumber, myNumber);
                }
                catch(Exception e)
                {
                    Toast.makeText(FirstScreen.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                root = FirebaseDatabase.getInstance().getReference();
                root.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(newString))
                        {
                            //root = FirebaseDatabase.getInstance().getReference().child(newString);
                        }
                        else
                        {
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
            Intent custom = new Intent("com.muraliyashu.hellomessenger.contactsLoader");
            custom.putExtra("number",newString);
            sendBroadcast(custom);

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, aList);
            chatroomlist.setAdapter(adapter);

            DatabaseReference tempRoot = FirebaseDatabase.getInstance().getReference().child("contacts-"+newString);
            tempRoot.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    try {
                        Iterator i = dataSnapshot.getChildren().iterator();
                        while (i.hasNext()) {
                            String contactName = (String) ((DataSnapshot) i.next()).getValue();
                            String contactNumber = (String) ((DataSnapshot) i.next()).getValue();
                            displayNames.add(contactName);
                            numbers.add(contactNumber);
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context,"First Screen",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
                    try{
                        Iterator i = dataSnapshot.getChildren().iterator();
                        while (i.hasNext())
                        {
                            String contactName = (String) ((DataSnapshot) i.next()).getValue();
                            String contactNumber = (String) ((DataSnapshot) i.next()).getValue();
                            displayNames.add(contactName);
                            numbers.add(contactNumber);
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context,"First Screen",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(!yesorno)
            {
                root.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        try {
                            Set<String> set = new HashSet<String>();
                            Iterator i = dataSnapshot.getChildren().iterator();
                            while (i.hasNext()) {
                                String user = ((DataSnapshot) i.next()).getKey().toString();
                                //numbers = getContactNumbers();
                                //displayNames = getContactNames();
                                if (numbers.contains(user)) {
                                    //if(!user.equals(newString))
                                    {
                                        int position = numbers.indexOf(user);
                                        set.add(displayNames.get(position));
                                    }
                                }
                            }
                            aList.clear();
                            aList.addAll(set);
                            adapter.notifyDataSetChanged();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(context,"First Screen",Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(FirstScreen.this, chat_room.class);
                    intent.putExtra("room_name", ((TextView) view).getText().toString());
                    int j = displayNames.indexOf(aList.get(i));
                    intent.putExtra("chat_number", numbers.get(j));
                    intent.putExtra("my_number", newString);
                    startActivity(intent);
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(FirstScreen.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                try {
                    firebaseAuth.signOut();
                    startActivity(new Intent(FirstScreen.this, MainActivity.class));
                    Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                catch (Exception e)
                {
                    Toast.makeText(FirstScreen.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }
    public void check(String token,String getNumber)
    {

        storeToken store = new storeToken(FirstScreen.this);
        try
        {
            store.execute(token, getNumber);
        } catch (Exception e)
        {
            Toast.makeText(FirstScreen.this,"Something went wrong, Please try after sometime",Toast.LENGTH_SHORT).show();
        }
    }
    private void openingChat(String roomName, String chatNumber, String myNumber) {

        try {

            Intent intent = new Intent(FirstScreen.this, chat_room.class);
            intent.putExtra("room_name", roomName);
            intent.putExtra("chat_number", chatNumber);
            intent.putExtra("my_number", myNumber);
            finish();
            if(chat_room.active)
            {
                chat_room.fa.finish();
            }
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(FirstScreen.this, "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
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
}