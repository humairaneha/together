package com.apptask.together.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apptask.together.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomTeamCreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomTeamCreateFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomTeamCreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomTeamCreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomTeamCreateFragment newInstance(String param1, String param2) {
        BottomTeamCreateFragment fragment = new BottomTeamCreateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
   private Button btn;
    private EditText text;

    private FirebaseDatabase database;
    private DatabaseReference teamRef,ref,friendRef;
    private FirebaseAuth auth;
    private FloatingActionButton button;
    private FirebaseUser user;
    private ArrayList<String>keyList;

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
        View view =inflater.inflate(R.layout.fragment_bottom_team_create, container, false);
        btn=view.findViewById(R.id.team_btn);
        text=view.findViewById(R.id.team_name);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        keyList=new ArrayList<>();
        ref=FirebaseDatabase.getInstance().getReference().child("Team");
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");
         button=view.findViewById(R.id.fabGroup);




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamName = text.getText().toString().trim();
                if(!TextUtils.isEmpty(teamName)){



                    friendRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                            keyList.add(user.getUid());
                                for(DataSnapshot snap:snapshot.getChildren())
                                {
                                    String key =snap.getKey();
                                    keyList.add(key);
                                }

                                for(String k:keyList){

                                    ref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            ref.child(teamName).child(k).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    { dismiss();}
                                                    //Toast.makeText(getContext(), "Team Created Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });



                                }




                            }

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });




//                    ref.child(teamName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            if(task.isSuccessful())
//                            {Toast.makeText(getContext(), "Team Inbox Created Successfully", Toast.LENGTH_SHORT).show();
//
//                                dismiss();
//                            }
//                        }
//                    });

                }
                else{

                    Toast.makeText(getContext(), "Please enter your team name to continue", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }
}