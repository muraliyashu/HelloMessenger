package com.muraliyashu.hellomessenger;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class notification extends BroadcastReceiver
{
	String getMessage, newString;
	private String contactNames,contactNumbers,sender;
	private ArrayList<String> numbers = new ArrayList<String>();
	private ArrayList<String> displayNames = new ArrayList<String>();
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			int getCurrentTime = (int)System.currentTimeMillis();
			getMessage = intent.getStringExtra("data");
			String[] splitted = getMessage.split(Pattern.quote("$%^"));
			String chatNumber = splitted[0];
			String myNumber = splitted[1];
			String message = splitted[2];
			//if(chat_room.active)
			{
				if(chat_room.chatNumber.equals(myNumber) && chat_room.myNumber.equals(chatNumber))
				{
					Toast.makeText(context,"Checking",Toast.LENGTH_SHORT).show();
				}
				//else
				{
					try {
						final sqlite_database db = new sqlite_database(context);
						db.open();
						Cursor curs = db.query("SELECT NUMBER,NAME FROM CONTACTS");
						while (curs.moveToNext())
						{
							contactNumbers = curs.getString(0);
							contactNames = curs.getString(1);
						}
						curs.close();
						JSONObject json1 = new JSONObject(contactNumbers);
						JSONArray jArray1 = json1.optJSONArray("uniqueNumbers");
						for (int k = 0; k < jArray1.length(); k++) {
							if(myNumber.equals(jArray1.optString(k).toString()))
							{
								JSONObject json = new JSONObject(contactNames);
								JSONArray jArray = json.optJSONArray("uniqueNames");
								sender = jArray.optString(k);
							}
						}
					}
					catch (JSONException e)
					{
						String getException = e.getMessage();
					}

					NotificationCompat.Builder builder =
							new NotificationCompat.Builder(context)
									.setSmallIcon(R.drawable.message)
									.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
											R.drawable.chat))
									.setContentTitle("Hello Messenger")
									.setContentText(sender+": "+message)
									.setAutoCancel(true);

					Intent notificationIntent = new Intent(context, FirstScreen.class);
					notificationIntent.putExtra("chatNumber", chatNumber);
					notificationIntent.putExtra("myNumber", myNumber);
					PendingIntent contentIntent = PendingIntent.getActivity(context, getCurrentTime, notificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(contentIntent);

					// Add as notification
					NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

					Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					AudioManager manager1 = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
					if (manager1.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
						builder.setVibrate(new long[]{1000, 1000, 1000});
						builder.setSound(alarmSound);
						builder.setLights(Color.RED, 3000, 3000);
					}

					else if (manager1.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
						builder.setVibrate(new long[]{1000, 1000, 1000});
						builder.setLights(Color.RED, 3000, 3000);
					}
					else if (manager1.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
						builder
								.setLights(Color.RED, 3000, 3000);
					}
					//if (isAppRunning(context, "com.muraliyashu.hellomessenger"))
					{
						// App is running
						Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
					}// else
					{
						// App is not running
						manager.notify(getCurrentTime, builder.build());
					}
				}
			}
		}
		catch(Exception e)
		{
			String getMessage=e.getMessage();
		}
	}
	public static boolean isAppRunning(final Context context, final String packageName)
	{
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		if (procInfos != null)
		{
			for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
				if (processInfo.processName.equals(packageName)) {
					return true;
				}
			}
		}
		return false;
	}
}
