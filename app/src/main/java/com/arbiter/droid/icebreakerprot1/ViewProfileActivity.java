package com.arbiter.droid.icebreakerprot1;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.arbiter.droid.icebreakerprot1.Common.getCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;
import static com.arbiter.droid.icebreakerprot1.Common.getScreenHeight;
import static com.arbiter.droid.icebreakerprot1.Common.getScreenWidth;
import static com.arbiter.droid.icebreakerprot1.Common.image_viewer_mode;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;


public class ViewProfileActivity extends AppCompatActivity {

    FragmentInterface fragmentInterface;
    ShimmerFrameLayout shimmerFrameLayout;
    String pingNode;
    @Override
    protected void onStart(){
        super.onStart();
        setCurrentUser(getSharedPreferences("Icebreak",0).getString("saved_name",""));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_view_profile);
        final String name;
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        this.setTitle(name=getIntent().getExtras().getString("name"));
        DatabaseReference fd = getDatabaseReference().child("users").child(name);
        final DatabaseReference fd2 = getDatabaseReference().child("pings");
        if(getIntent().hasExtra("pingnode")) {
            pingNode = getIntent().getExtras().get("pingnode").toString();
            FloatingActionButton fab = findViewById(R.id.fab);
            Button acceptButton = findViewById(R.id.button9);
            Button rejectButton = findViewById(R.id.button12);
            acceptButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
            acceptButton.setOnClickListener(v->{
                getDatabaseReference().child("pings").child(pingNode).child("accepted").setValue("yes");
                Intent i = new Intent(this,ViewProfileActivity.class);
                i.putExtra("name",name);
                startActivity(i);
                finish();
            });
            rejectButton.setOnClickListener(v->{
                getDatabaseReference().child("pings").child(pingNode).removeValue();
                Intent i = new Intent(this,ViewProfileActivity.class);
                i.putExtra("name",name);
                startActivity(i);
                finish();
            });
            View fragment = findViewById(R.id.fragment3);
            fragment.getLayoutParams().height= (getScreenHeight()/2);
            fragment.requestLayout();
            fab.setVisibility(View.GONE);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView bioTextView = findViewById(R.id.textView4);
        final TextView genTextView = findViewById(R.id.genderTextView);
        final TextView interTextView = findViewById(R.id.interestedTextView);
        final TextView ageTextView = findViewById(R.id.ageText);
        setSupportActionBar(toolbar);
        fd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] tmp = dataSnapshot.child("dob").getValue().toString().split("/");
                int[] dob = new int[3];
                for(int i=0;i<3;i++)
                    dob[i]=Integer.parseInt(tmp[i]);
                int age = Period.between(LocalDate.of(dob[2],dob[1],dob[0]),LocalDate.now()).getYears();
                bioTextView.setText(dataSnapshot.child("bio").getValue().toString());
                genTextView.setText("Gender: "+dataSnapshot.child("gender").getValue().toString());
                interTextView.setText("Interested In: "+dataSnapshot.child("interested").getValue().toString());
                ageTextView.setText("Age:"+age);
                Bundle b = new Bundle();
                image_viewer_mode=3;
                b.putString("target_user",getTitle().toString());
                fragmentInterface.onFragmentInteract(b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            final int[] result={0};
            fd2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = children.iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot next = iterator.next();
                        String from = next.child("from").getValue().toString();
                        String to = next.child("to").getValue().toString();
                        String accepted = next.child("accepted").getValue().toString();
                        if (from.equals(getCurrentUser()) && to.equals(name) && accepted.equals("no"))
                            result[0]=1;
                        else if(from.equals(getCurrentUser()) && to.equals(name) && accepted.equals("yes"))
                            result[0]=2;
                        else if(from.equals(name) && to.equals(getCurrentUser()) && accepted.equals("yes"))
                            result[0]=2;
                    }
                    if(result[0]==0) {
                        DatabaseReference tmp = fd2.push();
                        tmp.child("from").setValue(getCurrentUser());
                        tmp.child("to").setValue(name);
                        tmp.child("accepted").setValue("no");
                    }
                    else if(result[0]==1)
                        Toast.makeText(ViewProfileActivity.this, "You've already pinged this user", Toast.LENGTH_SHORT).show();
                    else
                    {
                        Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                        i.putExtra("venname",getTitle());
                        i.putExtra("groupChat","no");
                        i.putExtra("sender",getCurrentUser());
                        startActivity(i);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
        final ImageView imageView2 = findViewById(R.id.picture);
        imageView2.getLayoutParams().height= (int) (getScreenWidth()/2.4F);
        imageView2.getLayoutParams().width= (int) (getScreenWidth()/2.4F);
        imageView2.requestLayout();
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().build();
        final ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        try {
            getDatabaseReference().child("users").child(name).child("prof_img_url").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url;
                    if(dataSnapshot.exists()) {
                        url = dataSnapshot.getValue().toString();
                        GlideApp.with(getBaseContext()).load(url).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                (findViewById(R.id.interestedTextView)).setVisibility(View.VISIBLE);
                                (findViewById(R.id.textView4)).setVisibility(View.VISIBLE);
                                (findViewById(R.id.genderTextView)).setVisibility(View.VISIBLE);
                                (findViewById(R.id.ageText)).setVisibility(View.VISIBLE);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                return false;
                            }
                        }).apply(RequestOptions.circleCropTransform()).into(imageView2);
                    }
                    else
                    {
                        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
                        (findViewById(R.id.interestedTextView)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.textView4)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.genderTextView)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.ageText)).setVisibility(View.VISIBLE);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public interface FragmentInterface
    {
        void onFragmentInteract(Bundle bundle);
    }
    public void setOnFragmentInteract(ViewProfileActivity.FragmentInterface fragmentInterface)
    {
        this.fragmentInterface = fragmentInterface;
    }
}
