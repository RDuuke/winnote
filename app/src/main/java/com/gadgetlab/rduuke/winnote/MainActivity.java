package com.gadgetlab.rduuke.winnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivy";
    private static final int RC_SING_IN = 10;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSingInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        mGoogleSingInClient = GoogleSignIn.getClient(this, gso);

        SignInButton SignInButton = findViewById(R.id.singinbutton);
        SignInButton.setSize(SignInButton.SIZE_STANDARD);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

    }

    @Override
    protected void onStart (){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (account != null && currentUser != null) {
           Intent HomeActivity = new Intent(getApplicationContext(), HomeActivity.class);
           startActivity(HomeActivity);

        }
    }


    private void singIn() {
        Intent singInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(singInIntent, RC_SING_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SING_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sing in failed", e);
            }

        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account)
    {
        Log.d(TAG, "FirebaseAuthWithGoogle " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference ref = database.getReference("users");
                            Log.w(TAG, "id " + account.getId());
                            ref.child(account.getId()).child("name").setValue(account.getDisplayName());
                            Intent HomeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(HomeActivity);
                        } else{
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }
}