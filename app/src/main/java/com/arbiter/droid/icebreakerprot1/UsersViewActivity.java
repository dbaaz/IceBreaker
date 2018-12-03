package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class UsersViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_view);
        final ListView lv = findViewById(R.id.listViewmine);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> userArray = new ArrayList<>();
        final ArrayAdapter adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,userArray);
        final SharedPreferences sharedPreferences = this.getSharedPreferences("Icebreak",0);
        final int mode = getIntent().getIntExtra("mode",-1);
        if(mode==0){
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArray.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                String name;
                while (iterator.hasNext()) {
                    userArray.add(iterator.next().child("name").getValue().toString());
                    if (sharedPreferences.getString("saved_name", "").equals(userArray.get(userArray.size() - 1)))
                        userArray.remove(userArray.size() - 1);
                }
                adap.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });}
        else if(mode==1)
        {
            this.setTitle("Your Messages");
            /*final DatabaseReference childr = mDatabase.child("users").child(sharedPreferences.getString("saved_name", "")).child("runningchats");

            childr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    while(iterator.hasNext())
                    {
                        userArray.add(iterator.next().getValue().toString());
                    }
                    adap.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
            DatabaseReference databaseReference = mDatabase.child("user_chats");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    while (iterator.hasNext())
                    {
                        String currUser = sharedPreferences.getString("saved_name","");
                        String chatUser;
                        DataSnapshot next = iterator.next();
                        if((next.child("participants").child("1").getValue().toString()).equals(currUser))
                        {
                            userArray.add(next.child("participants").child("2").getValue().toString());
                        }
                        else if((chatUser=next.child("participants").child("2").getValue().toString()).equals(currUser))
                        {
                            userArray.add(next.child("participants").child("1").getValue().toString());
                        }
                        adap.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if(mode==2)
        {
            this.setTitle("Your Pings");
            final DatabaseReference childr = mDatabase.child("pings");
            childr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    while(iterator.hasNext())
                    {
                        DataSnapshot next = iterator.next();
                        if(next.child("to").getValue().equals(sharedPreferences.getString("saved_name",""))&&next.child("accepted").getValue().equals("no"))
                        {
                            userArray.add(next.child("from").getValue().toString());
                        }
                        adap.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(UsersViewActivity.this, ((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                if(mode==0) {
                    Intent i = new Intent(view.getContext(), ViewProfileActivity.class);
                    i.putExtra("name",((TextView)view).getText());
                    startActivity(i);
                }
                else if(mode==1)
                {
                    Intent i = new Intent(view.getContext(), ChatActivity.class);
                    i.putExtra("sender", sharedPreferences.getString("saved_name", ""));
                    i.putExtra("venname", ((TextView) view).getText());
                    i.putExtra("groupChat", "no");
                    startActivity(i);
                }
                else if(mode==2)
                {
                    final DatabaseReference childr = mDatabase.child("pings");
                    childr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator = children.iterator();
                            String name="";
                            while(iterator.hasNext())
                            {
                                DataSnapshot next = iterator.next();

                                if(next.child("to").getValue().toString().equals(sharedPreferences.getString("saved_name",""))&&next.child("accepted").getValue().toString().equals("no"))
                                {
                                    String s = next.getKey().toString();
                                    childr.child(s).child("accepted").setValue("yes");
                                    name = next.child("from").getValue().toString();

                                }
                                adap.notifyDataSetChanged();
                            }
                            Intent i = new Intent(getApplicationContext(), ViewProfileActivity.class);
                            i.putExtra("name",name);
                            startActivity(i);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        lv.setAdapter(adap);
    }
}
