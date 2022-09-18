package com.apptask.together.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentBottomIdeaEditBinding;
import com.apptask.together.databinding.FragmentBottomIdeaInputBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomIdeaEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomIdeaEditFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomIdeaEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomIdeaEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomIdeaEditFragment newInstance(String param1, String param2) {
        BottomIdeaEditFragment fragment = new BottomIdeaEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,postRef;
    private FragmentBottomIdeaEditBinding binding;
    private EditText ideatxt,titletxt;
    private Button btn,saveBtn;
    private LoadingDialog mLoadingDialog;


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
        binding = FragmentBottomIdeaEditBinding.inflate(inflater, container, false);
        View rootView =inflater.inflate(R.layout.fragment_bottom_idea_edit, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        btn = rootView.findViewById(R.id.delete_btn);
        saveBtn=rootView.findViewById(R.id.add_btn);
        ideatxt=  rootView.findViewById(R.id.idea_input);
        titletxt= rootView.findViewById(R.id.editTextTextPersonName2);
        mLoadingDialog=new LoadingDialog(getActivity());
        Bundle bundle = this.getArguments();
        String postKey = bundle.getString("Key");
        postRef=databaseReference.child("Ideas").child(postKey);

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               if(snapshot.exists()){final String idea = snapshot.child("idea").getValue().toString();
                   final  String title = snapshot.child("title").getValue().toString();

                   ideatxt.setText(idea);
                   String txt =binding.ideaInput.getText().toString();
                   ideatxt.setSelection(idea.length());
                   titletxt.setText(title);
                   titletxt.setSelection(title.length());
                   Log.d("TAG", "onDataChange: "+txt);}


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingDialog.startLoadingDialog();
                postRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mLoadingDialog.dismissDialog();
                        Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        ideatxt.setText("");
                        titletxt.setText("");
                        dismiss();



                    }
                });
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingDialog.startLoadingDialog();
               final String idea= ideatxt.getText().toString();
                final String title= titletxt.getText().toString();
                postRef.child("idea").setValue(idea);
                postRef.child("title").setValue(title).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mLoadingDialog.dismissDialog();
                       Toast.makeText(getActivity(),"Saved Successfully",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });


        return rootView;
    }


    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}