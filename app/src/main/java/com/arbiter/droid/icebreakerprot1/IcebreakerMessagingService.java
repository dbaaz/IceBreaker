package com.arbiter.droid.icebreakerprot1;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class IcebreakerMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Icebreak",0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("firebaseinstanceid",token);
        edit.commit();
    }
    @Override
    public void onMessageReceived(RemoteMessage remotemessage)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.bubble_circle)
                .setContentTitle(remotemessage.getNotification().getTitle())
                .setContentText(remotemessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Map<String, String> data = remotemessage.getData();
        if(data.containsKey("n_type")) {
            if(data.get("n_type").equals("chat")) {
                String venname = data.get("venname");
                String sender = data.get("sender");
                Intent pend = new Intent(this, ChatActivity.class);
                pend.putExtra("venname", venname);
                pend.putExtra("sender", sender);
                pend.putExtra("groupChat", "no");
                PendingIntent i = PendingIntent.getActivity(this, 0, pend, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(i);
            }
            else
            {
                PendingIntent i = PendingIntent.getActivity(this,0,new Intent(this,IndexActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(i);
            }
        }
        mBuilder.setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
    }
}
