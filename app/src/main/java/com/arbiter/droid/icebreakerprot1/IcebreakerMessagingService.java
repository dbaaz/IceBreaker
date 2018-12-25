package com.arbiter.droid.icebreakerprot1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

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
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Map<String, String> data = remotemessage.getData();
        if(data.containsKey("n_type")) {
            if(data.get("n_type").equals("chat")) {
                String venname = data.get("sender");
                String sender = data.get("receiver");
                String message = data.get("body");
                mBuilder.setContentTitle(venname);
                mBuilder.setContentText(message);
                Intent pend = new Intent(this, ChatActivity.class);
                pend.putExtra("venname", venname);
                pend.putExtra("sender", sender);
                pend.putExtra("groupChat", "no");
                PendingIntent i = PendingIntent.getActivity(this, 0, pend, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(i);
            }
            else if(data.get("n_type").equals("ping_received"))
            {
                String sender = data.get("sender_name");
                String pingnode = data.get("pingnode");
                mBuilder.setContentTitle("Ping Request!");
                mBuilder.setContentText(sender+" wants to break ice with you!");
                Intent pend = new Intent(this,ViewProfileActivity.class);
                pend.putExtra("name",sender);
                pend.putExtra("pingnode",pingnode);
                PendingIntent i = PendingIntent.getActivity(this,0,pend,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(i);
            }
            else
            {
                String receiver = data.get("receiver_name");
                mBuilder.setContentTitle("Ping Accepted!");
                mBuilder.setContentText(receiver+" has accepted your ping!");
                Intent pend = new Intent(this,ViewProfileActivity.class);
                pend.putExtra("name",receiver);
                PendingIntent i = PendingIntent.getActivity(this,0,pend,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(i);
            }
        }
        mBuilder.setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, mBuilder.build());
        createNotificationChannel();
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Icebreak";
            String description = "Icebreaker messaging channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
