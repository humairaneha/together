package com.apptask.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Users;
import com.apptask.together.databinding.ActivitySignupBinding;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.apptask.together.R.color.black;

public class SignupActivity extends AppCompatActivity {
// private androidx.appcompat.widget.Toolbar toolbar;
    ActivitySignupBinding binding;
    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;
    private FirebaseAuth.AuthStateListener authListener;
    FirebaseDatabase database;
    GoogleSignInClient  mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        loadingDialog=new LoadingDialog(SignupActivity.this);
        database=FirebaseDatabase.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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
                auth.createUserWithEmailAndPassword
                        (binding.emailEditText.getText().toString(),binding.passwordEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            auth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(SignupActivity.this, "User created Successfully,Please Verify your email to continue", Toast.LENGTH_LONG).show();
                                               // binding.errorMessage.setText("User created Successfully,Please Verify your email to continue");
                                                Log.d("TAG", "Email sent.");
                                            }
                                        }
                                    });

                            Users user = new Users(binding.usernameEditText.getText().toString(),
                                                  binding.emailEditText.getText().toString(),
                                                  binding.passwordEditText.getText().toString());
                            String id = Objects.requireNonNull(task.getResult()).getUser().getUid();
                            user.setUserId(id);

                            database.getReference().child("Users").child(id).setValue(user);
                               UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(binding.usernameEditText.getText().toString())
                                    .build();

                            auth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User display name updated.");
                                            }
                                        }
                                    });


                       loadingDialog.dismissDialog();

                       finish();
                       startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                        }
                        else{
                            loadingDialog.dismissDialog();
                            binding.errorMessage.setText(task.getException().getMessage());
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





    int RC_SIGN_IN = 75;
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

                            DatabaseReference userRef = database.getReference().child("users");

                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                    if(!snapshot.hasChild(user.getUid())){

                                        Users users = new Users();
                                        users.setUserId(user.getUid());
                                        users.setEmail(user.getEmail());
                                        users.setUsername(user.getDisplayName());
                                        users.setProfilepic(user.getPhotoUrl().toString());
                                        database.getReference().child("Users").child(user.getUid()).setValue(users);
                                        loadingDialog.dismissDialog();
                                        finish();
                                        startActivity(new Intent(SignupActivity.this,HomeActivity.class));
                                    }

                                    else{

                                        loadingDialog.dismissDialog();
                                        finish();
                                        startActivity(new Intent(SignupActivity.this,HomeActivity.class));
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