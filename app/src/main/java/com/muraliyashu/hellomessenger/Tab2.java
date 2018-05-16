package com.muraliyashu.hellomessenger;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by MuraliYashu on 9/12/2017.
 */

public class Tab2 extends Fragment{

    private ListView chatroomlist;
    private ArrayAdapter adapter;
    private DatabaseReference root;// = FirebaseDatabase.getInstance().getReference().getRoot();
    private ArrayList<String> aList = new ArrayList<String>();
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();
    private ArrayList<String> notiNumbers = new ArrayList<String>();
    private ArrayList<String> notiDisplayNames = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private String newString="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2chat, container, false);
        try
        {
            final sqlite_database db = new sqlite_database(getActivity());
            chatroomlist = (ListView) rootView.findViewById(R.id.list);
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

            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, aList);
            chatroomlist.setAdapter(adapter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                chatroomlist.setNestedScrollingEnabled(true);
            }

                DatabaseReference rootChildren = FirebaseDatabase.getInstance().getReference();
                rootChildren.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Set<String> set = new HashSet<String>();
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
                                String user = ((DataSnapshot) i.next()).getKey().toString();
                                if (numbers.contains(user)) {
                                    int position = numbers.indexOf(user);
                                    set.add(displayNames.get(position));
                                }
                            }
                            aList.clear();
                            aList.addAll(set);
                            adapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(getActivity(), chat_room.class);
                    intent.putExtra("room_name", ((TextView) view).getText().toString());
                    int j = displayNames.indexOf(aList.get(i));
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
}