package com.muraliyashu.hellomessenger;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TabbedActivity extends AppCompatActivity
{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private String newString;
    private Set<String> murali = new HashSet<String>();
    private List<imageURL> aList = new ArrayList<imageURL>();
    private boolean updateOnlineStatus = false;
    private String usingApp = "";
    private Tab1 tab1Obj;
    private Tab3 tab3Obj;

    @Override
    public void onStart() {
        super.onStart();
        updateStatus("1",newString,true);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(usingApp.equals("yes"))
        {
            usingApp = "";
            String i = "1";
            //updateStatus("0",newString,true);
        }
        else if(tab1Obj.usingApp.equals("yes"))
        {
            tab1Obj.usingApp = "";
            String i = "1";
            //updateStatus("0",newString,true);
        }
        else if(tab3Obj.usingApp.equals("yes"))
        {
            tab3Obj.usingApp = "";
            String i = "1";
        }
        else
        {
            updateStatus("0",newString,true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        final sqlite_database db = new sqlite_database(TabbedActivity.this);
        db.open();
        firebaseAuth = FirebaseAuth.getInstance();
        Cursor curs = db.query("SELECT MOBILE FROM NUMBER");
        while (curs.moveToNext()) {
            newString = curs.getString(0);
        }
        curs.close();

        try{
            Bundle extras = getIntent().getExtras();
            displayNames = extras.getStringArrayList("names");
            numbers = extras.getStringArrayList("numbers");
        }
        catch (Exception e)
        {

        }

        Intent custom = new Intent("com.muraliyashu.hellomessenger.contactsLoader");
        custom.putExtra("number", newString);
        TabbedActivity.this.sendBroadcast(custom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usingApp = "yes";
                Intent i = new Intent(TabbedActivity.this,show_contacts_page.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            updateStatus("0",newString,true);
            firebaseAuth.signOut();
            usingApp = "yes";
            startActivity(new Intent(TabbedActivity.this, MainActivity.class));
            Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
            finish();

            return true;
        }
        if (id == R.id.action_profile) {
            usingApp = "yes";
            Intent intent = new Intent(TabbedActivity.this,user_profile.class);
            intent.putExtra("number", newString);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position)
            {
                case 0:
                    tab1Obj = new Tab1();
                    return tab1Obj;
                /*case 2:
                    Tab2 tab2Obj = new Tab2();
                    return tab2Obj;*/
                case 1:
                    tab3Obj = new Tab3();
                    return tab3Obj;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CHATS";
                /*case 1:
                    return "CONTACTS";*/
                case 1:
                    return "ONLINE";
            }
            return null;
        }
    }
    public void updateStatus(String status, String mobile, boolean updateOnce)
    {
        updateOnlineStatus = updateOnce;
        final String Zero = status;
        final String getNumber = mobile;
        final DatabaseReference root1 = FirebaseDatabase.getInstance().getReference().child("profilepictures").child(getNumber);
        root1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator j = dataSnapshot.getChildren().iterator();
                while (j.hasNext()) {
                    String a = ((String) ((DataSnapshot) j.next()).getValue());
                    String b = ((String) ((DataSnapshot) j.next()).getValue());
                    String c = ((String) ((DataSnapshot) j.next()).getValue());
                    String d = ((String) ((DataSnapshot) j.next()).getValue());
                    String i = "55";
                    if(updateOnlineStatus)
                    {
                        imageURL saveLatestDetails = new imageURL(a, Zero, c, d);
                        root1.setValue(saveLatestDetails);
                        updateOnlineStatus = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //DatabaseReference refObj = FirebaseDatabase.getInstance().getReference().child("onlineStatus");
        //onlineStatus obj = new onlineStatus(status,loadNumber);
        //refObj.child(loadNumber).setValue(obj);
    }
}
