package com.apptask.together;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Users;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.apptask.together.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private TextView username;
    private TextView emailId;
    private AppBarConfiguration mAppBarConfiguration;
     ActivityHomeBinding binding;
    private SharedPreferences sharedPref;
    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;
    private FirebaseDatabase database;
    private FirebaseUser user;
   // private FirebaseAuth.AuthStateListener authListener;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private  GoogleSignInClient mGoogleSignInClient;
    private FirebaseStorage storage;
    de.hdodenhof.circleimageview.CircleImageView image;
    FloatingActionButton img_btn;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      binding = ActivityHomeBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());
        loadingDialog=new LoadingDialog(HomeActivity.this);
        setSupportActionBar(binding.appBarHome.toolbar);



//        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        auth = FirebaseAuth.getInstance();
        storage =FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        username =headerView.findViewById(R.id.user_name);
        img_btn=headerView.findViewById(R.id.fab_btn);
        image=(de.hdodenhof.circleimageview.CircleImageView )headerView.findViewById(R.id.p_image);
        emailId=headerView.findViewById(R.id.user_email);
        navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        user = auth.getCurrentUser();
        authStateListener =new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                if(user!=null){

                    database.getReference().child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            Log.d("TAG", "onDataChange: "+users.getProfilepic());

                            Picasso.get()
                                    .load(users.getProfilepic())
                                    .resize(400,400)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_icons8_account)
                                    .into(image);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });


                }
            }
        };


        if(user!=null){

            String name = user.getDisplayName();
            String email = user.getEmail();

            if(!name.isEmpty() && !email.isEmpty()){
                username.setText(name);
                emailId.setText(email);
            }

            Log.d("TAG", "onCreate: "+name);
            Log.d("TAG", "onCreate: "+email);
        }


//        authListener=new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user==null){
//
//                    startActivity(new Intent(HomeActivity.this,Splash_screenActivity.class));
//                    finish();
//                }
//                else{
//                    String userId = user.getUid();
//                    String userEmail = user.getEmail();
//                    sharedPref = getPreferences(MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("firebasekey", userId);
//                    editor.commit();
//                }
//            }
//        };

        img_btn.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
//         Intent galleryIntent = new Intent();
//         galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//         galleryIntent.setType("image/*");
//         startActivityForResult(galleryIntent,Gallery_pick);

           ImagePicker.with(HomeActivity.this)
                   .galleryOnly()
                   .crop()
                               //Crop image(Optional), Check Customization for more option
                   .compress(1024)			//Final image size will be less than 1 MB(Optional)
                   .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                   .start();

       }
   });


        database.getReference().child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
             Users users = snapshot.getValue(Users.class);
                Log.d("TAG", "onDataChange: "+users.getProfilepic());
                Picasso.get()
                        .load(users.getProfilepic())
                        .resize(400,400)
                        .centerCrop()
                        .placeholder(R.drawable.ic_icons8_account)
                        .into(image);
                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {


        super.onStart();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri file = data.getData();
        if (data.getData()!=null) {
            image.setImageURI(file);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference reference = storage.getReference().child("profile pictures")
                    .child(user.getUid());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(user.getUid())
                                    .child("profilepic").setValue(uri.toString());


                        }
                    });
                }
            });
        }



//        else{
//
//
//
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:

                auth.signOut();
                mGoogleSignInClient.signOut();
                loadingDialog.startLoadingDialog();
                if(auth.getCurrentUser()==null)
                {  loadingDialog.dismissDialog();
                    startActivity(new Intent(HomeActivity.this,Splash_screenActivity.class));     }
                else{loadingDialog.dismissDialog();}
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        return true;
    }
}