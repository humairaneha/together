package com.apptask.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Users;
import com.apptask.together.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
   // private androidx.appcompat.widget.Toolbar toolbar;
    ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;
    private SharedPreferences sharedPref;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        loadingDialog=new LoadingDialog(LoginActivity.this);
        setContentView(binding.getRoot());
     auth=FirebaseAuth.getInstance();
     database=FirebaseDatabase.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        toolbar=findViewById(R.id.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.toolbar.setNavigationContentDescription(R.string.toolbar_description_login);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.emailButton1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      loadingDialog.startLoadingDialog();
        auth.signInWithEmailAndPassword(binding.emailEditText.getText().toString(),
                binding.passwordEditText.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           if(auth.getCurrentUser().isEmailVerified()) {
                               // database.getReference().child("Users").child(user.getUid()).setValue(users);
                               loadingDialog.dismissDialog();
                               finish();
                               startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                           }
                           else{
                               loadingDialog.dismissDialog();
                               binding.textView2.setText("Please Verify your email");
                           }
                        }
                        else {
                            loadingDialog.dismissDialog();
                            binding.textView2.setText(task.getException().getMessage());
                        }
                    }
                });
    }


});

        binding.googleSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

   int RC_SIGN_IN = 76;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        loadingDialog.startLoadingDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = auth.getCurrentUser();
                           final  String uid = auth.getCurrentUser().getUid();
                            Log.d("TAG", "onComplete: "+uid);

                            DatabaseReference userRef = database.getReference().child("Users");

                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    // Users users = snapshot.getValue(Users.class);
                                    if(!snapshot.hasChild(user.getUid())){

                                        Users users = new Users();
                                        users.setUserId(user.getUid());
                                        users.setEmail(user.getEmail());
                                        users.setUsername(user.getDisplayName());
                                        users.setProfilepic(user.getPhotoUrl().toString());
                                        database.getReference().child("Users").child(user.getUid()).setValue(users);
                                        loadingDialog.dismissDialog();
                                        finish();
                                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                    }
                                    else{

                                        loadingDialog.dismissDialog();
                                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });




                            //updateUI(user);
                        } else {
                            loadingDialog.dismissDialog();
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });

    }

}