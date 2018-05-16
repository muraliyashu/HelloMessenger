package com.muraliyashu.hellomessenger;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class load_contacts_activity extends AppCompatActivity {

    String getNumber;
    static Activity a;
    private DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_contacts_activity);
        a=this;
        Bundle extras = getIntent().getExtras();
        getNumber = (String)extras.get("number");

        root = FirebaseDatabase.getInstance().getReference();
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {

                if (snapshot.hasChild("contacts-"+getNumber))
                {
                    LoadContacts load = new LoadContacts(load_contacts_activity.this);
                    load.execute(getNumber);
                }
                else
                {
                    LoadContacts load = new LoadContacts(load_contacts_activity.this);
                    load.execute(getNumber);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
    }
}
