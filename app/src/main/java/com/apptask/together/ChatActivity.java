package com.apptask.together;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import com.apptask.together.Adapters.ChatAdapter;
import com.apptask.together.Models.Messages;
import com.apptask.together.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding mBinding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference messageRef, userRef;
    private String currentUserName;
    private String currentUserId;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String name = getIntent().getExtras().get("teamName").toString();
        Log.d("TAG", "onCreate: " + name);
        //mBinding.toolbar.setNavigationContentDescription(name);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setTitle(name);
        //getSupportActionBar().setIcon(R.drawable.ic_baseline_arrow_back_24);
        mBinding.toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                }
        );
        mBinding.scrollView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        },1000);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(name);

        currentUserId = user.getUid();
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        final ArrayList<Messages> messageList = new ArrayList<>();
        final ChatAdapter adapter = new ChatAdapter(messageList, this);
        mBinding.chatRecycler.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.chatRecycler.setLayoutManager(linearLayoutManager);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot snap:snapshot.getChildren()){

                    Messages messages =snap.getValue(Messages.class);
                    messageList.add(messages);


                }
                adapter.notifyDataSetChanged();
                mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                mBinding.scrollView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                },1000);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        mBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                mBinding.scrollView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                },1000);
                text = mBinding.typeMessage.getText().toString().trim();
                if(!TextUtils.isEmpty(text)){
                Messages messages = new Messages(currentUserName, text, currentUserId, new Date().getTime());
                messageRef.push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mBinding.typeMessage.setText("");
                        mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        mBinding.typeMessage.setText("");
                    }
                });

            }}
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        mBinding.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//        messageRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });


    }
}