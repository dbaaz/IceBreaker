package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.arbiter.droid.icebreakerprot1.Common.getCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;
import static com.arbiter.droid.icebreakerprot1.Common.getDate;
import static com.arbiter.droid.icebreakerprot1.Common.randomString;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;

public class ChatActivity extends AppCompatActivity {
    String receiver;
    String sender;
    String isGroup;
    TextView senderLabel;
    public ChatActivity()
    {
        this.isGroup="no";
    }
    @Subscribe
    public void start(ChallengeActivityStartEvent event) {
        Log.v("myapp","Event triggered");
        Intent starter = new Intent(getApplicationContext(), ChallengeViewActivity.class);
        starter.putExtra("challengenode",event.challengeNode);
        startActivity(starter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
        setCurrentUser(getSharedPreferences("Icebreak",0).getString("saved_name",""));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //EventBus.getDefault().register(this);
        senderLabel = findViewById(R.id.messageSender);
        receiver = getIntent().getStringExtra("venname");
        sender=getIntent().getStringExtra("sender");
        isGroup="no";
        isGroup=getIntent().getStringExtra("groupChat");
        MessageInput inputView = findViewById(R.id.inputView);
        //Button post = findViewById(R.id.button4);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPref = this.getSharedPreferences("Icebreak",0);
        //final EditText postmsg = findViewById(R.id.editText);
        //final EditText chatlog = findViewById(R.id.editText2);
        final String name = sharedPref.getString("saved_name","");
        if(isGroup.equals("yes")) {
            ImageLoader imageLoader = new ImageLoader() {
                @Override
                public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                    GlideApp.with(getApplicationContext()).load(url).into(imageView);
                }
            };
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("pubs");
            final DatabaseReference node[] = {null};

            MessageHolders holdersConfig = new MessageHolders();
            holdersConfig.setIncomingTextLayout(R.layout.item_custom_incoming_text_message);
            holdersConfig.setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message);
            holdersConfig.setOutcomingTextConfig(CustomOutcomingTextMessageViewHolder.class,R.layout.item_custom_outcoming_text_message);
            holdersConfig.setIncomingTextConfig(CustomIncomingTextMessageViewHolder.class,R.layout.item_custom_incoming_text_message);
            final MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(name,holdersConfig,imageLoader);
            MessagesList messagesList=findViewById(R.id.messagesList);
            messagesList.setAdapter(adapter);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    while(iterator.hasNext())
                    {
                        DataSnapshot next = iterator.next();
                        if(next.child("name").getValue().toString().equals(sender))
                        {
                            node[0]=next.getRef();
                            setGroupNode(next.getRef(),adapter);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            inputView.setInputListener(new MessageInput.InputListener() {
                @Override
                public boolean onSubmit(CharSequence input) {
                    DatabaseReference temp = node[0].child("chat").push();
                    temp.child("sender").setValue(name);
                    temp.child("text").setValue(input.toString());
                    temp.child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                    temp.child("challenge").setValue("null");
                    temp.child("challengeid").setValue("null");
                    temp.child("challengetype").setValue("null");
                    temp.child("challengenode").setValue("null");
                    return true;
                }
            });

            inputView.setAttachmentsListener(new MessageInput.AttachmentsListener() {
                @Override
                public void onAddAttachments() {
                    startActivityForResult(new Intent(getBaseContext(),ChallengeListActivity.class),1);
                }
            });
        }
        else
        {
            final DatabaseReference[] node = {null};
            inputView.setInputListener(new MessageInput.InputListener() {
                @Override
                public boolean onSubmit(CharSequence input) {
                    DatabaseReference temp = node[0].push();
                    return postMessage(temp,input);
                }
            });
            inputView.setAttachmentsListener(new MessageInput.AttachmentsListener() {
                @Override
                public void onAddAttachments() {
                    startActivityForResult(new Intent(getBaseContext(),ChallengeListActivity.class),1);
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
                node[0] = mDatabase.child("user_chats").child(randomString(25));
                setNode(node[0]);
            }

        }
     }
     void setNode(DatabaseReference ref)
     {
         ref.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                 Iterator<DataSnapshot> iterator = children.iterator();
                 ArrayList<ArrayList<Object>> chatList = new ArrayList<>();
                 ImageLoader imageLoader = new ImageLoader() {
                     @Override
                     public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                         GlideApp.with(getBaseContext()).load(url).into(imageView);
                     }
                 };
                 final byte CONTENT_TYPE_CHALLENGE=1;
                 MessageHolders.ContentChecker<Message> contentChecker = new MessageHolders.ContentChecker<Message>() {
                     @Override
                     public boolean hasContentFor(Message message, byte type) {
                         switch (type) {
                             case CONTENT_TYPE_CHALLENGE:
                                 return !message.getChallenge().equals("null");
                         }
                         return false;
                     }
                 };
                 MessageHolders holdersConfig = new MessageHolders().registerContentType(
                         CONTENT_TYPE_CHALLENGE,
                         CustomIncomingChallengeMessageViewHolder.class,
                         R.layout.custom_incoming_challenge_message,
                         CustomOutcomingChallengeMessageViewHolder.class,
                         R.layout.custom_outcoming_challenge_message,
                         contentChecker);
                 MessagesListAdapter<Message> adapter = new MessagesListAdapter<>(sender,holdersConfig,imageLoader);
                 MessagesList messagesList=findViewById(R.id.messagesList);
                 messagesList.setAdapter(adapter);
                 while (iterator.hasNext())
                 {
                     DataSnapshot tmp = iterator.next();
                     ArrayList<Object> tmpList = new ArrayList<>();
                     tmpList.add(tmp.child("sender").getValue());
                     tmpList.add(tmp.child("text").getValue());
                     tmpList.add(tmp.child("timestamp").getValue());
                     tmpList.add(tmp.child("challenge").getValue());
                     tmpList.add(tmp.child("challengetype").getValue());
                     tmpList.add(tmp.child("challengenode").getValue());
                     chatList.add(tmpList);
                 }
                 /*try
                 {
                     Collections.sort(chatList, new Comparator<ArrayList<Object>>() {
                         @Override
                         public int compare(ArrayList<Object> o1, ArrayList<Object> o2) {
                             return Long.compare(Long.parseLong(o1.get(2).toString()),Long.parseLong(o2.get(2).toString()));
                         }
                     });}catch (NullPointerException e)
                 {

                 }*/
                 String text="";
                 try{
                     adapter.clear();
                     for(ArrayList chatItem : chatList)
                     {
                         //text += chatItem.get(0).toString() + ": " +chatItem.get(1).toString() + "\n";
                         //Log.v("myapp",chatItem.get(3).toString());
                         adapter.addToStart(new Message(chatItem.get(2).toString(),chatItem.get(1).toString(),new Author(chatItem.get(0).toString(),chatItem.get(0).toString()),getDate(Long.parseLong(chatItem.get(2).toString())),chatItem.get(3).toString(),chatItem.get(4).toString(),chatItem.get(5).toString()),false);
                     }}catch (NullPointerException e)
                 {

                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
     void setGroupNode(DatabaseReference ref,final MessagesListAdapter<Message> adapter)
     {
         ref.child("chat").addValueEventListener(new ValueEventListener() {
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
                    /*try
                    {
                    Collections.sort(chatList, new Comparator<ArrayList<Object>>() {
                        @Override
                        public int compare(ArrayList<Object> o1, ArrayList<Object> o2) {
                            return Long.compare(Long.parseLong(o1.get(2).toString()),Long.parseLong(o2.get(2).toString()));
                        }
                    });}catch (NullPointerException e)
                    {

                    }*/
                 String text="";
                 try{
                     adapter.clear();
                     for(ArrayList chatItem : chatList)
                     {
                         adapter.addToStart(new Message(chatItem.get(0).toString(),chatItem.get(1).toString(),new Author(chatItem.get(0).toString(),chatItem.get(0).toString()),getDate(Long.parseLong(chatItem.get(2).toString())),null,null,null),false);
                     }}catch (NullPointerException e)
                 {

                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            finish();
            startActivity(new Intent(this, IndexActivity.class));
        } else {
            super.onBackPressed();
        }
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String challenge = data.getStringExtra("RESULT_STRING");
                ((MessageInput)findViewById(R.id.inputView)).getInputEditText().setText("challenge://"+challenge);
                ((MessageInput)findViewById(R.id.inputView)).getButton().performClick();
            }
        }
    }
    boolean postMessage(final DatabaseReference temp, CharSequence input)
    {
        String key = temp.getParent().getKey();
        getDatabaseReference().child("user_chats").child(key).child("participants").child("1").setValue(sender);
        getDatabaseReference().child("user_chats").child(key).child("participants").child("2").setValue(receiver);
        if(input.toString().contains("challenge://"))
        {
            final String challenge = input.toString().substring(input.toString().lastIndexOf('/') + 1);
            getDatabaseReference().child("challenges").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int[] result = {0};
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot next = iterator.next();
                        String from = next.child("from").getValue().toString();
                        String to = next.child("to").getValue().toString();
                        String accepted = next.child("accepted").getValue().toString();
                        if (from.equals(getCurrentUser()) && to.equals(receiver) && accepted.equals("no"))
                            result[0] = 1;
                        else if (from.equals(getCurrentUser()) && to.equals(receiver) && accepted.equals("yes"))
                            result[0] = 2;
                        else if (from.equals(receiver) && to.equals(getCurrentUser()) && accepted.equals("yes"))
                            result[0] = 2;
                    }
                    if (result[0] == 0 || result[0] == 2) {
                        DatabaseReference tmp = getDatabaseReference().child("challenges").push();
                        tmp.child("from").setValue(getCurrentUser());
                        tmp.child("to").setValue(receiver);
                        tmp.child("accepted").setValue("no");
                        tmp.child("type").setValue(challenge);
                        temp.child("challenge").setValue("yes");
                        temp.child("challengetype").setValue(challenge);
                        temp.child("challengenode").setValue(tmp.getKey());
                        temp.child("text").setValue(getCurrentUser()+" has sent you a "+challenge+" Challenge!");
                        temp.child("sender").setValue(sender);
                        temp.child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));

                    } else if (result[0] == 1)
                        Toast.makeText(ChatActivity.this, "You've already challenged this user", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            temp.child("text").setValue(input.toString());
            temp.child("challenge").setValue("null");
            temp.child("challengetype").setValue("null");
            temp.child("challengenode").setValue("null");
            temp.child("sender").setValue(sender);
            temp.child("timestamp").setValue(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));

        }
        return true;
    }
}
class Message implements IMessage, MessageContentType
{
    String id;
    String text;
    Author author;
    Date createdAt;
    String challenge;
    String challengetype;
    String challengenode;

    Message(String id, String text,Author author,Date createdAt,String challenge,String challengetype,String challengenode)
    {
        this.id=id;
        this.text=text;
        this.author=author;
        this.createdAt=createdAt;
        this.challenge=challenge;
        this.challengetype=challengetype;
        this.challengenode=challengenode;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public String getChallenge() { return challenge; }
    public String getChallengetype(){return challengetype;}
    public String getChallengeNode(){return challengenode;}
}

class Author implements IUser
{
    String id;
    String name;
    String avatar;
    Author(String id,String name)
    {
        this.id=id;
        this.name=name;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}
class CustomOutcomingTextMessageViewHolder extends MessageHolders.OutcomingTextMessageViewHolder<Message>
{
    protected TextView senderTextView;
    CustomOutcomingTextMessageViewHolder(View itemView, Object payload)
    {
        super(itemView,payload);
        senderTextView = itemView.findViewById(R.id.messageSender);
    }
    @Override
    public void onBind(Message message)
    {
        super.onBind(message);
        senderTextView.setText(message.id);
    }
}
class CustomIncomingTextMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message> {
    protected TextView sender;
    CustomIncomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        sender = itemView.findViewById(R.id.messageSender);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        sender.setText(message.id);
    }
}
class CustomOutcomingChallengeMessageViewHolder extends MessageHolders.OutcomingTextMessageViewHolder<Message>
{
    protected TextView challengeText;
    protected Button challengeButton;

    public CustomOutcomingChallengeMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        challengeText = itemView.findViewById(R.id.messageText);
        challengeButton = itemView.findViewById(R.id.checkChallenge);
    }
    @Override
    public void onBind(Message message) {
        super.onBind(message);
        challengeText.setText("You have sent a "+message.getChallengetype()+" challenge");
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ChallengeActivityStartEvent(message.getChallengeNode()));
            }
        });
    }
}
class CustomIncomingChallengeMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message>
{
    protected TextView challengeText;
    protected Button challengeButton;
    public CustomIncomingChallengeMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        challengeText = itemView.findViewById(R.id.messageText);
        challengeButton = itemView.findViewById(R.id.checkChallenge);
    }
    @Override
    public void onBind(Message message) {
        super.onBind(message);
        challengeText.setText(message.author.name+" has sent you a "+message.getChallengetype()+" challenge");
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ChallengeActivityStartEvent(message.getChallengeNode()));
            }
        });
    }
}