package com.arbiter.droid.icebreakerprot1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static com.arbiter.droid.icebreakerprot1.Common.getCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;
import static com.arbiter.droid.icebreakerprot1.Common.getPreference;
import static com.arbiter.droid.icebreakerprot1.Common.setCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.setDefaultPreferences;

public class IndexActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FusedLocationProviderClient mFusedLocationClient;
    EditText searchBox;
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Icebreak";
            String description = "Icebreaker notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    void setPerChipEvent()
    {
        final String[] critNames = {"alcohol","food","vegonly","drinksonly","dance","hookah","night"};
        final HashMap<String,Boolean> currentCriteria = new HashMap<>();
        final ChipGroup chipGroup = findViewById(R.id.chipgroup);
        final int childCount = chipGroup.getChildCount();
        final Chip[] chips = new Chip[childCount];
        final boolean[] checkedState = new boolean[childCount];
        for(int i=0;i<childCount;i++) {
            chips[i]=(Chip)chipGroup.getChildAt(i);
            chips[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i=0;i<childCount;i++) {
                        checkedState[i] = chips[i].isChecked();
                        currentCriteria.put(critNames[i],checkedState[i]);
                    }
                    EventBus.getDefault().post(new VenueCriteriaEvent(searchBox.getText().toString(),currentCriteria));
                }
            });
        }
    }
    HashMap<String,Boolean> getChipState()
    {
        final String[] critNames = {"alcohol","food","vegonly","drinksonly","dance","hookah","night"};
        final ChipGroup chipGroup = findViewById(R.id.chipgroup);
        HashMap<String,Boolean> tmp = new HashMap<>();
        for(int i=0;i<chipGroup.getChildCount();i++)
        {
            boolean checked = ((Chip) chipGroup.getChildAt(i)).isChecked();
            tmp.put(critNames[i],checked);
        }
        return tmp;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        setDefaultPreferences(this.getSharedPreferences("Icebreak",0));
        setPerChipEvent();
        ((Chip)findViewById(R.id.chip32)).setChecked(true);
        ((Chip)findViewById(R.id.chip)).setChecked(true);
        searchBox = findViewById(R.id.search_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);
        createNotificationChannel();
        setCurrentUser(getPreference("saved_name"));
        if (getSharedPreferences("Icebreak", 0).getString("firebaseinstanceid", "").equals("")) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful())
                        return;
                    getDatabaseReference().child("users").child(getCurrentUser()).child("firebaseinstanceid").setValue(task.getResult().getToken());
                }
            });
        }
        else
        {
            getDatabaseReference().child("users").child(getCurrentUser()).child("firebaseinstanceid").setValue(getPreference("firebaseinstanceid"));
        }
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        FloatingActionButton fab2 = findViewById(R.id.floatingActionButton3);
        fab.setOnClickListener(v -> {
            //Intent i = new Intent(v.getContext(),UsersViewActivity.class);
            Intent i = new Intent(v.getContext(),UsersViewRecyclerActivity.class);
            i.putExtra("mode",1);
            startActivity(i);
        });
        fab2.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(),UsersViewActivity.class);
            i.putExtra("mode",2);
            startActivity(i);
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventBus.getDefault().post(new VenueCriteriaEvent(searchBox.getText().toString(),getChipState()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Subscribe
    public void stopShimmer(ShimmerDisableEvent event)
    {
        (findViewById(R.id.shimmer_view_container_venue)).setVisibility(View.GONE);
        ((ShimmerFrameLayout)findViewById(R.id.shimmer_view_container_venue)).stopShimmer();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        if (id == R.id.pubmenuitem) {
            if (!(this instanceof IndexActivity)) {
                Intent inten = new Intent(this, IndexActivity.class);
                startActivity(inten);
            }
        } else if (id == R.id.profmenuitem) {
            Intent inten = new Intent(this, CreateProfileActivity.class);
            inten.putExtra("editmode","yes");
            startActivity(inten);
        }
        else if(id == R.id.menuPictures)
        {
            startActivity(new Intent(this,ImageListActivity.class));
        }
        else if(id == R.id.fb_placeholder)
        {
            try {
                AccessToken.getCurrentAccessToken().toString();
                startActivity(new Intent(this, FacebookAlbumListActivity.class));
            }
            catch (Exception e)
            {
                Toast.makeText(this, "You need to be logged into Facebook", Toast.LENGTH_SHORT).show();
            }
        }
        else if(id == R.id.logout_btn)
        {
            try {
                LoginManager.getInstance().logOut();
            }catch (Exception e){}
            SharedPreferences.Editor editor = getSharedPreferences("Icebreak", 0).edit();
            editor.remove("saved_name");
            editor.commit();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        else if(id == R.id.qrbutton)
        {
            startActivity(new Intent(this,QRScanActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
