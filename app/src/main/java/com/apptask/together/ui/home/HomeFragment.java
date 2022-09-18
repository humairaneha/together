package com.apptask.together.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Adapters.GroupAdapter;
import com.apptask.together.ChatActivity;
import com.apptask.together.Classes.RecyclerItemClickListener;
import com.apptask.together.Fragment.BottomTeamCreateFragment;
import com.apptask.together.Models.Tasks;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
private FragmentHomeBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference teamRef,ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView teamRecyclerView;
    ArrayList<String> TeamList;
    private Toolbar mToolbar;
    private FloatingActionButton btn;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        teamRecyclerView=binding.teamRecycler;
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        mToolbar= root.findViewById(R.id.toolbar);
        btn=root.findViewById(R.id.fabGroup);
        teamRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        teamRecyclerView.setLayoutManager(linearLayoutManager);

        TeamList =new ArrayList<>();

    teamRef=FirebaseDatabase.getInstance().getReference();
    teamRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            TeamList.clear();
            if(snapshot.hasChild("Team")){



                 for(DataSnapshot snap:snapshot.child("Team").getChildren()){
                     String name =snap.getKey();

                     for(DataSnapshot snap2:snapshot.child("Team").child(name).getChildren()){

                         if(snap2.getKey().equals(user.getUid())){
                             TeamList.add(name);
                             btn.setVisibility(View.GONE);
                             btn.setEnabled(false);
                         }

                     }

                 }
                GroupAdapter adapter =new GroupAdapter(TeamList,getContext());
                teamRecyclerView.setAdapter(adapter);
//                binding.fabGroup.setVisibility(View.GONE);
//                binding.fabGroup.setEnabled(false);

                teamRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext()
                        , teamRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String name = TeamList.get(position);
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("teamName",name);

                        Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                        startActivity(intent);


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onRadioButtonClick(View view, Tasks tasks) {

                    }
                }));

            }

        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });




    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BottomTeamCreateFragment bottomTeamCreateFragment =new BottomTeamCreateFragment();
            bottomTeamCreateFragment.show(getChildFragmentManager(),bottomTeamCreateFragment.getTag());
        }
    });





//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
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