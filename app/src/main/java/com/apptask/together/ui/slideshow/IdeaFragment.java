package com.apptask.together.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Adapters.IdeaAdapter;
import com.apptask.together.Classes.RecyclerItemClickListener;
import com.apptask.together.Fragment.BottomIdeaEditFragment;
import com.apptask.together.Fragment.BottomIdeaInputFragment;
import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Tasks;
import com.apptask.together.databinding.FragmentIdeaBinding;
import com.apptask.together.databinding.FragmentIdeaBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.grpc.LoadBalancer;

public class  IdeaFragment extends Fragment {

    private IdeaViewModel mIdeaViewModel;
    private FragmentIdeaBinding binding;
    private RecyclerView ideaRecyclerView;
    private ArrayList<Ideas>ideaList;
    private DatabaseReference idearef,userRef,friendRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private LoadingDialog mLoadingDialog;

    private FirebaseAuth.AuthStateListener authStateListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        mIdeaViewModel =
                new ViewModelProvider(this).get(IdeaViewModel.class);

    binding = FragmentIdeaBinding.inflate(inflater, container, false);
    View root = binding.getRoot();
        binding.ideaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomIdeaInputFragment bottomIdeaInputFragment =new BottomIdeaInputFragment();
                bottomIdeaInputFragment.show(getChildFragmentManager(),bottomIdeaInputFragment.getTag());

            }
        });

        auth=FirebaseAuth.getInstance();
        ideaRecyclerView=binding.ideaRecycler;
        ideaRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ideaRecyclerView.setLayoutManager(linearLayoutManager);
        mLoadingDialog=new LoadingDialog(getActivity());
        mLoadingDialog.startLoadingDialog();
        ideaList =new ArrayList<>();
        idearef= FirebaseDatabase.getInstance().getReference().child("Ideas");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseUser user= auth.getCurrentUser();
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");


        IdeaAdapter adapter =new IdeaAdapter(ideaList,getContext());
        ideaRecyclerView.setAdapter(adapter);

        Query sortIdea=idearef.orderByChild("counter");

        sortIdea.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ideaList.clear();
                for(DataSnapshot ideaSnap: snapshot.getChildren()) {

                    Ideas ideas = ideaSnap.getValue(Ideas.class);

                    userRef.child(ideas.getUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot1) {
                            if(snapshot1.hasChild("profilepic")){
                                ideas.setProfilepic(snapshot1.child("profilepic").getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                friendRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild(ideas.getUserId())||user.getUid().equals(ideas.getUserId())){
                                ideaList.add(ideas);
                            }
                            IdeaAdapter adapter =new IdeaAdapter(ideaList,getContext());
                            ideaRecyclerView.setAdapter(adapter);
                            mLoadingDialog.dismissDialog();
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            if(user.getUid().equals(ideas.getUserId())){
                                ideaList.add(ideas);
                            }
                            IdeaAdapter adapter =new IdeaAdapter(ideaList,getContext());
                            ideaRecyclerView.setAdapter(adapter);
                      mLoadingDialog.dismissDialog();
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                   //ideaList.add(ideas);










//                    final StorageReference reference = storage.getReference().child("profile pictures")
//                            .child(ideas.getUserId());
//
//                    idearef.child("profilepic").setValue(reference.getDownloadUrl().toString());
//                    ideas.setProfilepic(reference.getDownloadUrl().toString());


                }
                IdeaAdapter adapter =new IdeaAdapter(ideaList,getContext());
                ideaRecyclerView.setAdapter(adapter);
              mLoadingDialog.dismissDialog();
                adapter.notifyDataSetChanged();


                ideaRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                        ideaRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Ideas ideas = ideaList.get(position);
                        final String postKey = ideas.getPostId();
                        final String title =ideas.getTitle();
                        final String idea=ideas.getIdea();
                        Bundle bundle =new Bundle();
                        bundle.putString("Key",postKey);
                        bundle.putString("title",title);
                        bundle.putString("idea",idea);
                        if(user.getUid().equals(ideas.getUserId())){
                            BottomIdeaEditFragment bottomIdeaEditFragment =new BottomIdeaEditFragment();
                            bottomIdeaEditFragment.setArguments(bundle);

                            bottomIdeaEditFragment.show(getChildFragmentManager(),bottomIdeaEditFragment.getTag());
                            Log.d("TAG", "onItemClick: "+postKey);}
                        else{
                            Toast.makeText(getContext(), "You can not edit or delete other user's idea", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onRadioButtonClick(View view, Tasks tasks) {

                    }
                }));

            }




            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //final TextView name_txt = binding.textSlideshow;
//        mIdeaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                name_txt.setText(s);
//            }
//        });
        return root;



    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}