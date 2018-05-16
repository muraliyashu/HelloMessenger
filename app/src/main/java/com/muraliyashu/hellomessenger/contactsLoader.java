package com.muraliyashu.hellomessenger;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.muraliyashu.hellomessenger.R.id.mobilenumber;
import static com.muraliyashu.hellomessenger.sqlite_database.context;

/**
 * Created by MuraliYashu on 9/6/2017.
 */

public class contactsLoader extends BroadcastReceiver
{
    sqlite_database db;
    public ArrayList<String> asyncNumbers = new ArrayList<String>();
    public ArrayList<String> ayncDisplayNames = new ArrayList<String>();
    private String newString;
    private String unique_key;
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            db = new sqlite_database(context);
            db.open();
            MyObserver myObserver = new MyObserver(new Handler());
            ContentResolver cr = context.getContentResolver();
            cr.registerContentObserver(ContactsContract.Contacts.CONTENT_URI,false,myObserver);
        }
        catch(Exception e)
        {
            Toast.makeText(context,"storeToken",Toast.LENGTH_SHORT).show();
            String getMessage = e.getMessage();
        }
    }
    class MyObserver extends ContentObserver {
        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri)
        {
            try{
                Cursor cursLog = db.query("SELECT MOBILE FROM NUMBER");
                while (cursLog.moveToNext()) {
                    newString = cursLog.getString(0);
                }
                cursLog.close();
                db.DeleteAll("CONTACTSARRAY");
                if(newString.length()>9)
                {
                    DatabaseReference drContacts = FirebaseDatabase.getInstance().getReference().child("contacts-"+newString);
                    drContacts.removeValue();

                    drContacts = FirebaseDatabase.getInstance().getReference();
                    drContacts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild("contacts-"+mobilenumber))
                            {
                                //root = FirebaseDatabase.getInstance().getReference().child(newString);
                                //LoadContacts load = new LoadContacts(context);
                                //load.execute("exists");
                            }
                            else
                            {
                                DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("contacts-"+newString);

                                ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
                                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                                int checkID =  cursor.getColumnIndex("_id");
                                if (cursor.moveToFirst()) {
                                    do {
                                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                                            while (pCur.moveToNext()) {
                                                String DisplayNames = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                                String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                contactNumber = contactNumber.replaceAll(" ", "");
                                                contactNumber = contactNumber.replaceAll("-", "");

                                                ContentValues value1 = new ContentValues();
                                                if (contactNumber.length() == 10) {
                                                    if (!asyncNumbers.contains(contactNumber)) {
                                                        asyncNumbers.add(contactNumber);
                                                        ayncDisplayNames.add(DisplayNames);
                                                        contacts addContact = new contacts(DisplayNames, contactNumber);
                                                        unique_key = root.push().getKey();
                                                        root.child(unique_key).setValue(addContact);

                                                        value1.put("CONTACTNAME",DisplayNames);
                                                        value1.put("CONTACTNUMBER",contactNumber);
                                                        db.Insert(value1, "CONTACTSARRAY");
                                                    }
                                                } else if (contactNumber.length() == 13) {
                                                    contactNumber = contactNumber.substring(3);
                                                    if (!asyncNumbers.contains(contactNumber)) {
                                                        asyncNumbers.add(contactNumber);
                                                        ayncDisplayNames.add(DisplayNames);
                                                        contacts addContact1 = new contacts(DisplayNames, contactNumber);
                                                        unique_key = root.push().getKey();
                                                        root.child(unique_key).setValue(addContact1);

                                                        value1.put("CONTACTNAME",DisplayNames);
                                                        value1.put("CONTACTNUMBER",contactNumber);
                                                        db.Insert(value1, "CONTACTSARRAY");
                                                    }
                                                }
                                            }
                                            pCur.close();
                                        }
                                    } while (cursor.moveToNext());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                try
                {
                    JSONObject jsonumbers = new JSONObject();
                    jsonumbers.put("uniqueNumbers", new JSONArray(asyncNumbers));
                    String arrayListNumbers = jsonumbers.toString();

                    JSONObject jsonNames = new JSONObject();
                    jsonNames.put("uniqueNames", new JSONArray(ayncDisplayNames));
                    String arrayListNames = jsonNames.toString();

                    ContentValues valuesDOB = new ContentValues();
                    valuesDOB.put("NUMBER",arrayListNumbers);
                    valuesDOB.put("NAME",arrayListNames);
                    db.DeleteAll("CONTACTS");
                    db.Insert(valuesDOB, "CONTACTS");

                    Toast.makeText(context,"fired",Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e)
                {
                    String error = e.getMessage();
                }
                catch (Exception e)
                {
                    String error = e.getMessage();
                }


                /*
                ContentResolver cr = context.getContentResolver();
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if(cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                String DisplayNames = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contactNumber = contactNumber.replaceAll(" ", "");
                                contactNumber = contactNumber.replaceAll("-", "");

                                if (contactNumber.length() == 10) {
                                    if(!asyncNumbers.contains(contactNumber))
                                    {
                                        asyncNumbers.add(contactNumber);
                                        ayncDisplayNames.add(DisplayNames);
                                    }
                                }
                                else if (contactNumber.length() == 13)
                                {
                                    contactNumber = contactNumber.substring(3);
                                    if(!asyncNumbers.contains(contactNumber)) {
                                        asyncNumbers.add(contactNumber);
                                        ayncDisplayNames.add(DisplayNames);
                                    }
                                }
                            }
                            pCur.close();
                        }

                    } while (cursor.moveToNext());
                }
                try
                {
                    JSONObject jsonumbers = new JSONObject();
                    jsonumbers.put("uniqueNumbers", new JSONArray(asyncNumbers));
                    String arrayListNumbers = jsonumbers.toString();

                    JSONObject jsonNames = new JSONObject();
                    jsonNames.put("uniqueNames", new JSONArray(ayncDisplayNames));
                    String arrayListNames = jsonNames.toString();

                    ContentValues valuesDOB = new ContentValues();
                    valuesDOB.put("NUMBER",arrayListNumbers);
                    valuesDOB.put("NAME",arrayListNames);
                    db.DeleteAll("CONTACTS");
                    db.Insert(valuesDOB, "CONTACTS");
                    db.close();
                    Toast.makeText(context,"fired",Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e)
                {
                    String error = e.getMessage();
                }
                catch (Exception e)
                {
                    String error = e.getMessage();
                }*/
            }
            catch (Exception e)
            {
                Toast.makeText(context,"storeToken",Toast.LENGTH_SHORT).show();
                String error = e.getMessage();
            }
        }
    }
}
