package com.muraliyashu.hellomessenger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MuraliYashu on 8/18/2017.
 */
public class LoadContacts extends AsyncTask<Object,Void,Object>
{
    public ArrayList<String> asyncNumbers = new ArrayList<String>();
    public ArrayList<String> asyncDisplayNames = new ArrayList<String>();
    sqlite_database db;
    Context context;
    private String unique_key, message;
    LoadContacts(Context ctxt)
    {
        context = ctxt;
        db = new sqlite_database(context);
        db.open();
    }
    @Override
    protected String doInBackground(Object... params)
    {
        message = (String) params[0];
        //if(!message.equals("exists"))
        {
            DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("contacts-"+message);
            ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.moveToFirst())
            {
                do
                {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
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
                                    asyncDisplayNames.add(DisplayNames);
                                    contacts addContact = new contacts(DisplayNames, contactNumber);
                                    unique_key = root.push().getKey();
                                    root.child(unique_key).setValue(addContact);

                                    value1.put("CONTACTNAME",DisplayNames);
                                    value1.put("CONTACTNUMBER",contactNumber);
                                    db.Insert(value1, "CONTACTSARRAY");
                                }
                            }
                            else if (contactNumber.length() == 13) {
                                contactNumber = contactNumber.substring(3);
                                if (!asyncNumbers.contains(contactNumber)) {
                                    asyncNumbers.add(contactNumber);
                                    asyncDisplayNames.add(DisplayNames);
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
                }
                while (cursor.moveToNext());
            }
        }


/*
        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
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
            db.Insert(valuesDOB, "CONTACTS");
            db.close();
        }
        catch (JSONException e)
        {
            String error = e.getMessage();
        }*/
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object result)
    {
        super.onPostExecute(result);
        //Intent intent = new Intent(context, FirstScreen.class);

        try
        {
            JSONObject jsonumbers = new JSONObject();
            jsonumbers.put("uniqueNumbers", new JSONArray(asyncNumbers));
            String arrayListNumbers = jsonumbers.toString();

            JSONObject jsonNames = new JSONObject();
            jsonNames.put("uniqueNames", new JSONArray(asyncDisplayNames));
            String arrayListNames = jsonNames.toString();

            ContentValues valuesDOB = new ContentValues();
            valuesDOB.put("NUMBER",arrayListNumbers);
            valuesDOB.put("NAME",arrayListNames);
            db.Insert(valuesDOB, "CONTACTS");
            db.close();
        }
        catch (JSONException e)
        {
            String error = e.getMessage();
        }

        Intent intent = new Intent(context, TabbedActivity.class);
        intent.putExtra("sendNumber",message);
        context.startActivity(intent);
        load_contacts_activity.a.finish();
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}


