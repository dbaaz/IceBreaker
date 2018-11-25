package com.arbiter.droid.icebreakerprot1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends Activity implements View.OnClickListener {
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("Icebreak",0);

        if(sharedPref.contains("saved_name"))
        {

            startActivity(new Intent(this,IndexActivity.class));
            finish();
        }
        else {
            setContentView(R.layout.activity_login);
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Button goog = findViewById(R.id.button2);
            goog.setOnClickListener(this);
        }
    }
    protected void onStart(Bundle test)
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }
    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button2:
                    signIn();
                    break;

            }

    }
    private void signIn() {
        RC_SIGN_IN=1;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            //Toast.makeText(this, "Welcome "+account.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent profCreateInt = new Intent(this,CreateProfileActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("accdet",account);
            profCreateInt.putExtra("accdetailbundle",b);
            startActivity(profCreateInt);
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            Log.v("myapp",e.getMessage());
            e.printStackTrace();
            //updateUI(null);
        }
    }
}
