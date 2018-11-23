package com.arbiter.droid.icebreakerprot1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.arbiter.droid.icebreakerprot1.Common.randomString;

public class ChatActivity extends AppCompatActivity {
    String receiver;
    String sender;
    String isGroup;
    ChatActivity()
    {
        this.isGroup="no";
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "lol";
            String description = "lol";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("lol", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        receiver = getIntent().getStringExtra("venname");
        sender=getIntent().getStringExtra("sender");
        isGroup="no";
        isGroup=getIntent().getStringExtra("groupChat");
        Button post = findViewById(R.id.button4);
        createNotificationChannel();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPref = this.getSharedPreferences("Icebreak",0);
        final EditText postmsg = findViewById(R.id.editText);
        final EditText chatlog = findViewById(R.id.editText2);
        final String name = sharedPref.getString("saved_name","");
        final boolean[] initCall = {true,true};

        final String text[] = new String[1];
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),"lol").setSmallIcon(R.drawable.ic_menu_send).setContentTitle("Icebreaker").setContentText("You may have new messages").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if(isGroup.equals("yes")) {
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rndString = randomString(15);
                    initCall[1]=true;
                    mDatabase.child(sender).child("chat").child(rndString).child("sender").setValue(name);
                    mDatabase.child(sender).child("chat").child(rndString).child("text").setValue(postmsg.getText().toString());
                    mDatabase.child(sender).child("chat").child(rndString).child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                    mDatabase.child(sender).child("chat").child(rndString).child("receiver").setValue(receiver);
                    postmsg.setText("");
                }
            });
            mDatabase.child(sender).child("chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    text[0] = "";
                    Iterable<DataSnapshot> chat = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = chat.iterator();
                    GenericChatObject temp[] = new GenericChatObject[(int) dataSnapshot.getChildrenCount()];
                    Toast.makeText(ChatActivity.this, dataSnapshot.getChildrenCount() + "", Toast.LENGTH_SHORT).show();
                    int i = 0;
                    while (iterator.hasNext()) {
                        Iterable<DataSnapshot> children = iterator.next().getChildren();
                        Iterator<DataSnapshot> iterator1 = children.iterator();

                        while (iterator1.hasNext()) {
                            temp[i] = new GenericChatObject();
                            try {
                                temp[i].receiver = iterator1.next().getValue().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                temp[i].sender = iterator1.next().getValue().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                temp[i].message = iterator1.next().getValue().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                temp[i].timestamp = Long.parseLong(iterator1.next().getValue().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        i++;
                    }
                    for (int k = 0; k < temp.length; k++) {
                        for (int j = 1; j < temp.length; j++) {
                            if (temp[j - 1].timestamp > temp[j].timestamp) {
                                GenericChatObject tmp = temp[j - 1];
                                temp[j - 1] = temp[j];
                                temp[j] = tmp;
                            }
                        }
                    }
                    for (int k = 0; k < temp.length; k++) {
                        text[0] += temp[k].sender + ": " + temp[k].message + "\n";
                    }
                    chatlog.setText(text[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            final String dbName[]={sender+"|"+receiver};
            mDatabase.child(sender+"|"+receiver).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        dbName[0]=sender+"|"+receiver;
                    else
                        dbName[0]=receiver+"|"+sender;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mDatabase.child(receiver+"|"+sender).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    dbName[0]=receiver+"|"+sender;
                else
                    dbName[0]=sender+"|"+receiver;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase.child("users").child(sender).child("runningchats").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean flag=false;
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator = children.iterator();
                            while(iterator.hasNext())
                            {
                                if(iterator.next().getValue().toString().equals(receiver))
                                    flag=true;
                            }
                            if(!flag)
                            {
                                mDatabase.child("users").child(sender).child("runningchats").push().setValue(receiver);
                                mDatabase.child("users").child(receiver).child("runningchats").push().setValue(sender);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    String rndString = randomString(15);
                    initCall[1]=true;
                    mDatabase.child(dbName[0]).child("chat").child(rndString).child("sender").setValue(name);
                    mDatabase.child(dbName[0]).child("chat").child(rndString).child("text").setValue(postmsg.getText().toString());
                    mDatabase.child(dbName[0]).child("chat").child(rndString).child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                    mDatabase.child(dbName[0]).child("chat").child(rndString).child("receiver").setValue(receiver);
                    postmsg.setText("");
                }
            });
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDatabase.child(dbName[0]).child("chat").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            text[0] = "";
                            Iterable<DataSnapshot> chat = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator = chat.iterator();
                            GenericChatObject temp[] = new GenericChatObject[(int) dataSnapshot.getChildrenCount()];
                            Toast.makeText(ChatActivity.this, dataSnapshot.getChildrenCount() + "", Toast.LENGTH_SHORT).show();
                            int i = 0;
                            while (iterator.hasNext()) {
                                Iterable<DataSnapshot> children = iterator.next().getChildren();
                                Iterator<DataSnapshot> iterator1 = children.iterator();

                                while (iterator1.hasNext()) {
                                    temp[i] = new GenericChatObject();
                                    try {
                                        temp[i].receiver = iterator1.next().getValue().toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        temp[i].sender = iterator1.next().getValue().toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        temp[i].message = iterator1.next().getValue().toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        temp[i].timestamp = Long.parseLong(iterator1.next().getValue().toString());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                i++;
                            }
                            for (int k = 0; k < temp.length; k++) {
                                for (int j = 1; j < temp.length; j++) {
                                    if (temp[j - 1].timestamp > temp[j].timestamp) {
                                        GenericChatObject tmp = temp[j - 1];
                                        temp[j - 1] = temp[j];
                                        temp[j] = tmp;
                                    }
                                }
                            }
                            for (int k = 0; k < temp.length; k++) {
                                text[0] += temp[k].sender + ": " + temp[k].message + "\n";
                            }
                            chatlog.setText(text[0]);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            },1000);

        }
     }
}

class GenericChatObject
{
    String message;
    long timestamp;
    String sender;
    String receiver;
}