package com.arbiter.droid.icebreakerprot1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.arbiter.droid.icebreakerprot1.Common.databaseReference;
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
        //final boolean[] initCall = {true,true};

        final String text[] = new String[1];
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),"lol").setSmallIcon(R.drawable.ic_menu_send).setContentTitle("Icebreaker").setContentText("You may have new messages").setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if(isGroup.equals("yes")) {
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference temp = mDatabase.child("pubs").child(sender).child("chat").push();
                    temp.child("sender").setValue(name);
                    temp.child("text").setValue(postmsg.getText().toString());
                    temp.child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                    postmsg.setText("");
                }
            });
            mDatabase.child("pubs").child(sender).child("chat").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    ArrayList<ArrayList<Object>> chatList = new ArrayList<>();
                    while (iterator.hasNext())
                    {
                        DataSnapshot tmp = iterator.next();
                        ArrayList<Object> tmpList = new ArrayList<>();
                        tmpList.add(tmp.child("sender").getValue());
                        tmpList.add(tmp.child("text").getValue());
                        tmpList.add(tmp.child("timestamp").getValue());
                        chatList.add(tmpList);
                    }
                    try
                    {
                    Collections.sort(chatList, new Comparator<ArrayList<Object>>() {
                        @Override
                        public int compare(ArrayList<Object> o1, ArrayList<Object> o2) {
                            return Long.compare(Long.parseLong(o1.get(2).toString()),Long.parseLong(o2.get(2).toString()));
                        }
                    });}catch (NullPointerException e)
                    {

                    }
                    String text="";
                    try{
                    for(ArrayList chatItem : chatList)
                    {
                        text += chatItem.get(0).toString() + ": " +chatItem.get(1).toString() + "\n";
                    }}catch (NullPointerException e)
                    {

                    }
                    chatlog.setText(text);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            final DatabaseReference[] node = {null};

            /*try {
                done.await();
            }catch (InterruptedException e){

            }*/
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference temp = node[0].push();
                    String key = temp.getParent().getKey();
                    databaseReference.child("user_chats").child(key).child("participants").child("1").setValue(sender);
                    databaseReference.child("user_chats").child(key).child("participants").child("2").setValue(receiver);
                    temp.child("sender").setValue(sender);
                    temp.child("text").setValue(postmsg.getText().toString());
                    temp.child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                    postmsg.setText("");

                }
            });
            mDatabase.child("user_chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Toast.makeText(ChatActivity.this, dataSnapshot.getChildrenCount()+"", Toast.LENGTH_SHORT).show();
                        if (snapshot.child("participants").exists()) {
                            String send = snapshot.child("participants").child("1").getValue().toString();
                            String recei = snapshot.child("participants").child("2").getValue().toString();
                            if ((send.equals(sender) && recei.equals(receiver)) || (send.equals(receiver) && recei.equals(sender))) {
                                node[0] = mDatabase.child("user_chats").child(snapshot.getKey());
                                Log.v("myapp",node[0]+" 1");
                                setNode(node[0]);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if(node[0]==null) {
                Toast.makeText(this, "lulwa", Toast.LENGTH_SHORT).show();
                node[0] = mDatabase.child("user_chats").child(randomString(15));
                setNode(node[0]);
            }

        }
     }
     void setNode(DatabaseReference ref)
     {
         ref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Toast.makeText(ChatActivity.this, "lul", Toast.LENGTH_SHORT).show();
                 Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                 Iterator<DataSnapshot> iterator = children.iterator();
                 ArrayList<ArrayList<Object>> chatList = new ArrayList<>();
                 while (iterator.hasNext())
                 {
                     DataSnapshot tmp = iterator.next();
                     ArrayList<Object> tmpList = new ArrayList<>();
                     tmpList.add(tmp.child("sender").getValue());
                     tmpList.add(tmp.child("text").getValue());
                     tmpList.add(tmp.child("timestamp").getValue());
                     chatList.add(tmpList);
                 }
                 try
                 {
                     Collections.sort(chatList, new Comparator<ArrayList<Object>>() {
                         @Override
                         public int compare(ArrayList<Object> o1, ArrayList<Object> o2) {
                             return Long.compare(Long.parseLong(o1.get(2).toString()),Long.parseLong(o2.get(2).toString()));
                         }
                     });}catch (NullPointerException e)
                 {

                 }
                 String text="";
                 try{
                     for(ArrayList chatItem : chatList)
                     {
                         text += chatItem.get(0).toString() + ": " +chatItem.get(1).toString() + "\n";
                     }}catch (NullPointerException e)
                 {

                 }
                 EditText chatlog = findViewById(R.id.editText2);
                 chatlog.setText(text);
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
}
