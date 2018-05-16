package com.muraliyashu.hellomessenger;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by MuraliYashu on 8/5/2017.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {

            String o = remoteMessage.getData().get("message");
            Intent intent = new Intent();
            intent.setAction("com.muraliyashu.hellomessenger.notification");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("data",o);
            sendBroadcast(intent);
        }
        catch(Exception e)
        {
            Intent i = new Intent(this,FirstScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("number",e.getMessage());
            startActivity(i);
            String a = e.getMessage();
            String b = e.getMessage();
        }
    }
}
