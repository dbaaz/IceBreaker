package com.arbiter.droid.icebreakerprot1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arbiter.droid.icebreakerprot1.Common.getCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;
import static com.arbiter.droid.icebreakerprot1.Common.getScreenWidth;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;

public class ChallengeViewActivity extends AppCompatActivity {
    @BindViews({R.id.textView9,R.id.textView11,R.id.textView12}) List<TextView> textViews;
    @BindViews({R.id.button3,R.id.button6,R.id.button4}) List<Button> buttons;
    @BindViews({R.id.imageView2,R.id.imageView3}) List<ImageView> imageViews;
    @BindView(R.id.imageView4) ImageView challengeIcon;
    String challengenode;
    @Override
    protected void onStart(){
        super.onStart();
        setCurrentUser(getSharedPreferences("Icebreak",0).getString("saved_name",""));
    }
    @OnClick(R.id.button3)
    public void acceptChallenge(){
        getDatabaseReference().child("challenges").child(challengenode).child("accepted").setValue("yes");
        buttons.get(0).setVisibility(View.INVISIBLE);
        buttons.get(2).setVisibility(View.INVISIBLE);
    }
    @OnClick(R.id.button6)
    public void cancelChallenge(){
        getDatabaseReference().child("challenges").child(challengenode).child("accepted").setValue("cancelled");
        buttons.get(1).setVisibility(View.INVISIBLE);
    }
    @OnClick(R.id.button4)
    public void rejectChallenge(){
        getDatabaseReference().child("challenges").child(challengenode).child("accepted").setValue("rejected");
        buttons.get(0).setVisibility(View.INVISIBLE);
        buttons.get(2).setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_view);
        ButterKnife.bind(this);
        for(ImageView imageView:imageViews){
            imageView.getLayoutParams().height= (int) (getScreenWidth()/2.4F);
            imageView.getLayoutParams().width= (int) (getScreenWidth()/2.4F);
            imageView.requestLayout();
        }
        challengenode=getIntent().getStringExtra("challengenode");
        getDatabaseReference().child("challenges").child(challengenode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String challengerName = dataSnapshot.child("from").getValue().toString();
                String challengedName = dataSnapshot.child("to").getValue().toString();
                String challengeType = dataSnapshot.child("type").getValue().toString();
                String challengeAccepted = dataSnapshot.child("accepted").getValue().toString();
                getDatabaseReference().child("users").child(challengerName).child("prof_img_url").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String prof_img_url = dataSnapshot.getValue().toString();
                        Picasso.get().load(prof_img_url).into(imageViews.get(0));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                getDatabaseReference().child("users").child(challengedName).child("prof_img_url").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String prof_img_url = dataSnapshot.getValue().toString();
                        Picasso.get().load(prof_img_url).into(imageViews.get(1));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                for(TextView textView:textViews)
                    textView.setVisibility(View.VISIBLE);
                if (challengerName.equals(getCurrentUser())) {
                    textViews.get(0).setText(challengedName);
                    switch (challengeAccepted) {
                        case "no":
                            textViews.get(1).setText("has yet to accept your");
                            textViews.get(2).setText(challengeType + " Challenge");
                            buttons.get(1).setVisibility(View.VISIBLE);
                            break;
                        case "yes":
                            textViews.get(1).setText("has accepted your");
                            textViews.get(2).setText(challengeType + " Challenge");
                            buttons.get(1).setVisibility(View.INVISIBLE);
                            break;
                        case "rejected":
                            textViews.get(1).setText("has rejected your");
                            textViews.get(2).setText(challengeType + " Challenge");
                            buttons.get(1).setVisibility(View.INVISIBLE);
                            break;
                        case "cancelled":
                            textViews.get(0).setVisibility(View.INVISIBLE);
                            textViews.get(1).setVisibility(View.INVISIBLE);
                            textViews.get(2).setText("Challenge Cancelled");
                    }
                }
                else {
                    buttons.get(1).setVisibility(View.INVISIBLE);
                    textViews.get(0).setText(challengerName);
                    switch (challengeAccepted) {
                        case "no":
                            textViews.get(1).setText("has challenged you to a");
                            textViews.get(2).setText(challengeType + " Challenge");
                            buttons.get(0).setVisibility(View.VISIBLE);
                            buttons.get(2).setVisibility(View.VISIBLE);
                            break;
                        case "yes":
                            textViews.get(1).setText("and you are ready to do the");
                            textViews.get(2).setText(challengeType + " Challenge");
                            break;
                        case "rejected":
                            textViews.get(0).setVisibility(View.INVISIBLE);
                            textViews.get(1).setVisibility(View.INVISIBLE);
                            textViews.get(2).setText("You've rejected the challenge");
                            break;
                        case "cancelled":
                            textViews.get(2).setVisibility(View.INVISIBLE);
                            textViews.get(1).setText("has cancelled the challenge");
                            buttons.get(0).setVisibility(View.INVISIBLE);
                            buttons.get(2).setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
