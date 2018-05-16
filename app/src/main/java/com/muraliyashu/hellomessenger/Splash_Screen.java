package com.muraliyashu.hellomessenger;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.muraliyashu.hellomessenger.generalValues.checkingConnection;


public class Splash_Screen extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;
    final sqlite_database db = new sqlite_database(Splash_Screen.this);
    long Delay = 2000;
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> displayNames = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        try {
            boolean connection = generalValues.checkingConnection(Splash_Screen.this);

            if(connection) {
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseDatabase.getInstance().getReference().keepSynced(true);
                db.open();
                if (firebaseAuth.getCurrentUser() != null) {
                    Cursor cursorObject = db.query("SELECT CONTACTNAME,CONTACTNUMBER FROM CONTACTSARRAY");
                    while (cursorObject.moveToNext()) {
                        String number = cursorObject.getString(1);
                        numbers.add(number);
                        String name = cursorObject.getString(0);
                        displayNames.add(name);
                    }
                    cursorObject.close();
                }

                db.close();
                Timer RunSplash = new Timer();
                TimerTask ShowSplash = new TimerTask() {
                    @Override
                    public void run() {
                        if (firebaseAuth.getCurrentUser() != null) {
                            //Intent mainIntent = new Intent(Splash_Screen.this, FirstScreen.class);
                            Intent mainIntent = new Intent(Splash_Screen.this, TabbedActivity.class);
                            mainIntent.putStringArrayListExtra("numbers", numbers);
                            mainIntent.putStringArrayListExtra("names", displayNames);
                            startActivity(mainIntent);
                            finish();
                            Log.d(TAG, "onCreate() Restoring previous state");
                        } else {
                            try {
                                Intent mainInten = new Intent(Splash_Screen.this, MainActivity.class);
                                startActivity(mainInten);
                                finish();
                            } catch (Exception e) {
                                String u = e.getMessage();
                            }
                        }
                    }
                };
                RunSplash.schedule(ShowSplash, Delay);
                progressBar.setProgress(0);
                progressBar.setMax(100);
            /*new CountDownTimer(Delay, 50)
            {
                public void onTick(long millisUntilFinished)
                {
                    int i = (int)millisUntilFinished / 50;
                    i = 100 - i;
                    progressBar.setProgress(i);
                }

                public void onFinish() {

                }
            }.start();
*/

                final int totalProgressTime = 100;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        int jumpTime = 0;

                        while (jumpTime < totalProgressTime) {
                            try {
                                sleep(100);
                                jumpTime += 5;
                                progressBar.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
            }
            else
            {
                Intent i = new Intent(Splash_Screen.this,NoInternetConnection.class);
                startActivity(i);
            }
        }
        catch(Exception e)
        {
            String getMessage = e.getMessage();
            if(!checkingConnection(Splash_Screen.this))
            {
                Intent i = new Intent(Splash_Screen.this,NoInternetConnection.class);
                startActivity(i);
            }
        }
    }
}