package com.apptask.together.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentBottomIdeaInputBinding;
import com.apptask.together.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomIdeaInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomIdeaInputFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomIdeaInputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomIdeaInputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomIdeaInputFragment newInstance(String param1, String param2) {
        BottomIdeaInputFragment fragment = new BottomIdeaInputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,ideaRef;
    private FragmentBottomIdeaInputBinding binding;
    private String currentDate,currentTime,postRandomName;
    private long countIdeas=0;
    private  LoadingDialog mLoadingDialog;
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
        binding = FragmentBottomIdeaInputBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mLoadingDialog=new LoadingDialog(getActivity());
        databaseReference =FirebaseDatabase.getInstance().getReference();
        ideaRef=FirebaseDatabase.getInstance().getReference().child("Ideas");
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = binding.editTextTextPersonName2.getText().toString().trim();
                String idea =binding.ideaInput.getText().toString().trim();

                if(!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(idea)){
                  mLoadingDialog.startLoadingDialog();
                    Calendar calDate = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM,yyyy");
                    currentDate = dateFormat.format(calDate.getTime());
                    Calendar calTime = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    currentTime = timeFormat.format(calTime.getTime());

                    postRandomName = currentDate+currentTime;


                FirebaseUser user = auth.getCurrentUser();

                if(user!=null) {


                  ideaRef.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                          if(snapshot.exists()){

                              countIdeas= snapshot.getChildrenCount();
                          }
                          else{
                              countIdeas=0;

                          }
                      }

                      @Override
                      public void onCancelled(@NonNull @NotNull DatabaseError error) {

                      }
                  });

                   database.getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                           if(snapshot.exists()) {


                                   Users users = snapshot.getValue(Users.class);
                                   Ideas ideas = new Ideas();
                                   ideas.setIdea(idea);
                                   ideas.setTitle(title);
                                   ideas.setUseremail(users.getEmail());
                                   ideas.setUsername(users.getUsername());
                                   ideas.setUserId(user.getUid());
                                   ideas.setProfilepic(users.getProfilepic());
                                   ideas.setDate(currentDate);
                                   ideas.setTime(currentTime);
                                   ideas.setPostId(user.getUid() + postRandomName);
                                   ideas.setCounter(countIdeas);

                                   databaseReference.child("Ideas").child(user.getUid() + postRandomName).setValue(ideas)
                                           .addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull @NotNull Exception e) {
                                                  Toast.makeText(getActivity(), "Saving Failed", Toast.LENGTH_SHORT).show();
                                                   dismiss();
                                               }
                                           })
                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void unused) {
                                                   mLoadingDialog.dismissDialog();
                                                  Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                                                   binding.editTextTextPersonName2.setText("");
                                                   binding.ideaInput.setText("");
                                                   dismiss();


                                               }


                                           });


                               }

                           }



                       @Override
                       public void onCancelled(@NonNull @NotNull DatabaseError error) {

                       }
                   });}



                }

                else{
                    Toast.makeText(getActivity(), "Please fill the Title And Idea fields to continue", Toast.LENGTH_SHORT).show();
                }
                }




            });
        return root;

        }




    @Override public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}