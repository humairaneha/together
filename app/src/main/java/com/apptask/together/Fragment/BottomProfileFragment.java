package com.apptask.together.Fragment;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomProfileFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomProfileFragment newInstance(String param1, String param2) {
        BottomProfileFragment fragment = new BottomProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private de.hdodenhof.circleimageview.CircleImageView mCircleImageView;
    private TextView UserName,UserEmail;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef,friendReqRef,friendRef;
    private Button invite,cancel_invite;
    private String current_state,sendUserId,receiverUserId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View view=inflater.inflate(R.layout.fragment_bottom_profile, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef=database.getReference().child("Users");
        friendReqRef=database.getReference().child("FriendRequest");
        friendRef=database.getReference().child("Friends");
         mCircleImageView=view.findViewById(R.id.user_profilePic);
         UserName=view.findViewById(R.id.username_profile);
         UserEmail=view.findViewById(R.id.useremail_profile);
         invite=view.findViewById(R.id.invite_btn);
         cancel_invite=view.findViewById(R.id.cancel_invitation);
         current_state ="not_friends";
        Bundle bundle = this.getArguments();

         String uid = bundle.getString("Key");
         receiverUserId=uid;
         userRef.child(uid).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 if(snapshot.exists()){


                        if(snapshot.hasChild("profilepic")){
                         String url = snapshot.child("profilepic").getValue().toString();

                         Picasso.get().load(url).placeholder(R.drawable.ic_icons8_account)
                                 .into(mCircleImageView);}
                         String username = snapshot.child("username").getValue().toString();
                         Log.d("TAG", "onDataChange: "+username);
                         UserName.setText(username);
                         String email = snapshot.child("email").getValue().toString();
                         UserEmail.setText(email);

                        friendReqRef.child(sendUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                if(snapshot.hasChild(receiverUserId)){

                                    String req_type=snapshot.child(receiverUserId).child("request_type")
                                            .getValue().toString();

                                    if(req_type.equals("sent")){
                                        current_state="request_sent";
                                        invite.setText("Cancel Invitation");
                                        cancel_invite.setVisibility(view.INVISIBLE);
                                        cancel_invite.setEnabled(false);

                                    }
                                    else if(req_type.equals("recieved")){

                                        current_state="request_recieved";
                                        invite.setEnabled(true);
                                        invite.setText("Accept Invitation");
                                        cancel_invite.setVisibility(view.VISIBLE);
                                        cancel_invite.setEnabled(true);

                                        cancel_invite.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                friendReqRef.child(sendUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        friendReqRef.child(receiverUserId).child(sendUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                current_state="not_friends";
                                                                invite.setEnabled(true);
                                                                invite.setText("Invite");
                                                                cancel_invite.setVisibility(view.INVISIBLE);
                                                                cancel_invite.setEnabled(false);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });


                                    }

                                }

                                else{

                                    friendRef.child(sendUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                            if(snapshot.hasChild(receiverUserId)){

                                                current_state="friends";
                                                invite.setEnabled(true);
                                                invite.setText("Remove");
                                                cancel_invite.setVisibility(view.INVISIBLE);
                                                cancel_invite.setEnabled(false);


                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });






                 }
             }

             @Override
             public void onCancelled(@NonNull @NotNull DatabaseError error) {

             }
         });

        cancel_invite.setVisibility(view.INVISIBLE);
        cancel_invite.setEnabled(false);
        sendUserId=auth.getCurrentUser().getUid();
        if(!sendUserId.equals(receiverUserId)){


        invite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                invite.setEnabled(false);

                if(current_state.equals("not_friends")){

                    friendReqRef.child(sendUserId).child(receiverUserId).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                      if(task.isSuccessful()){  friendReqRef.child(receiverUserId).child(sendUserId).child("request_type")
                              .setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull @NotNull Task<Void> task) {
                                      if(task.isSuccessful()){
                                          invite.setEnabled(true);
                                          current_state="request_sent";
                                          invite.setText("Cancel Invitation");
                                          cancel_invite.setVisibility(view.INVISIBLE);
                                          cancel_invite.setEnabled(false);


                                      }
                                  }
                              });}

                        }
                    });


                }

                if(current_state.equals("request_sent")){

                    friendReqRef.child(sendUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            friendReqRef.child(receiverUserId).child(sendUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    current_state="not_friends";
                                    invite.setEnabled(true);
                                    invite.setText("Invite");
                                    cancel_invite.setVisibility(view.INVISIBLE);
                                    cancel_invite.setEnabled(false);
                                }
                            });
                        }
                    });


                }

                if(current_state.equals("request_recieved")){

                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat Format = new SimpleDateFormat("dd-MMM-yyyy");
                    String date = Format.format(calendar.getTime());

                    friendRef.child(sendUserId).child(receiverUserId).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            friendRef.child(receiverUserId).child(sendUserId).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                          if(task.isSuccessful()){

                                              friendReqRef.child(sendUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                  @Override
                                                  public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                      if(task.isSuccessful()){   friendReqRef.child(receiverUserId).child(sendUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                              if(task.isSuccessful()){ current_state="friends";
                                                                  invite.setEnabled(true);
                                                                  invite.setText("Remove");
                                                                  cancel_invite.setVisibility(view.INVISIBLE);
                                                                  cancel_invite.setEnabled(false);}

                                                          }
                                                      });}

                                                  }
                                              });
                                          }




                                }
                            });
                        }
                    });






                }


                if(current_state.equals("friends")){

                    friendRef.child(sendUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){ friendRef.child(receiverUserId).child(sendUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        current_state="not_friends";
                                        invite.setEnabled(true);
                                        invite.setText("Invite");
                                        cancel_invite.setVisibility(view.INVISIBLE);
                                        cancel_invite.setEnabled(false);
                                    }

                                }
                            });}

                        }
                    });



                }


            }
        });

        }
        else{
            cancel_invite.setVisibility(view.INVISIBLE);
            invite.setVisibility(view.INVISIBLE);
        }



        return view ;
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }


}
