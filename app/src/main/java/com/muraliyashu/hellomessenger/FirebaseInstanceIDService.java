package com.muraliyashu.hellomessenger;

import android.database.Cursor;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MuraliYashu on 8/5/2017.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    String getMobileNumber="";
    @Override
    public void onTokenRefresh()
    {
        final sqlite_database db = new sqlite_database(this);
        db.open();

        Cursor cursLog = db.query("SELECT MOBILE FROM NUMBER");
        while (cursLog.moveToNext())
        {
            getMobileNumber = cursLog.getString(0);
            String token = FirebaseInstanceId.getInstance().getToken();
            FirstScreen n = new FirstScreen();
            n.check(token,getMobileNumber);
        }
        cursLog.close();
        db.close();
    }
}
