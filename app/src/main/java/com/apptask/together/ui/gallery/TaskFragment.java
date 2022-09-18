package com.apptask.together.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Adapters.IdeaAdapter;
import com.apptask.together.Adapters.TaskAdapter;
import com.apptask.together.Classes.RecyclerItemClickListener;
import com.apptask.together.Fragment.BottomIdeaEditFragment;
import com.apptask.together.Fragment.BottomIdeaInputFragment;
import com.apptask.together.Fragment.BottomTaskEditFragment;
import com.apptask.together.Fragment.BottomTaskInputFragment;
import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Tasks;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentGalleryBinding;
import com.apptask.together.databinding.FragmentIdeaBinding;
import com.apptask.together.ui.slideshow.IdeaViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TaskFragment extends Fragment {

    private TaskViewModel mTaskViewModel;
private FragmentGalleryBinding binding;
    private RecyclerView taskRecyclerView;
    private ArrayList<Tasks> taskList;
    private DatabaseReference taskRef,friendRef;
    private FirebaseAuth auth;
    private RadioButton checkBtn;
    private LoadingDialog mLoadingDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        mTaskViewModel =
                new ViewModelProvider(this).get(TaskViewModel.class);

    binding = FragmentGalleryBinding.inflate(inflater, container, false);
    View root = binding.getRoot();





//        final TextView textView = binding.textGallery;
//        mTaskViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        binding.taskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomTaskInputFragment bottomTaskInputFragment =new BottomTaskInputFragment();
                bottomTaskInputFragment.show(getChildFragmentManager(),bottomTaskInputFragment.getTag());
            }
        });
        auth=FirebaseAuth.getInstance();
        taskRecyclerView=binding.taskRecycler;
        taskRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        mLoadingDialog=new LoadingDialog(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskList=new ArrayList<>();
        taskRef= FirebaseDatabase.getInstance().getReference().child("Tasks");
        FirebaseUser user= auth.getCurrentUser();
        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        checkBtn=root.findViewById(R.id.check_btn);
        Query sortTask=taskRef.orderByChild("date");
        mLoadingDialog.startLoadingDialog();
        sortTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                taskList.clear();
                if(snapshot.exists()){
                for(DataSnapshot taskSnap: snapshot.getChildren()) {

                    Tasks tasks = taskSnap.getValue(Tasks.class);
                    //taskList.add(tasks);


                    friendRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                if(snapshot.hasChild(tasks.getUserId())||user.getUid().equals(tasks.getUserId())){
                                    taskList.add(tasks);
                                }
                                TaskAdapter adapter =new TaskAdapter(taskList,getContext());
                                taskRecyclerView.setAdapter(adapter);
                                mLoadingDialog.dismissDialog();
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                if(user.getUid().equals(tasks.getUserId())){
                                    taskList.add(tasks);
                                }
                             mLoadingDialog.dismissDialog();
                                TaskAdapter adapter =new TaskAdapter(taskList,getContext());
                                taskRecyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                }}


      mLoadingDialog.dismissDialog();



                TaskAdapter adapter =new TaskAdapter(taskList,getContext());
                taskRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                taskRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                        taskRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Tasks tasks = taskList.get(position);
                        final String taskKey = tasks.getTaskId();
                        final String task =tasks.getTask();
                        Bundle bundle =new Bundle();
                        bundle.putString("Key",taskKey);
                        bundle.putString("task",task);

                        if(user.getUid().equals(tasks.getUserId())){
                            BottomTaskEditFragment bottomTaskEditFragment =new BottomTaskEditFragment();
                            bottomTaskEditFragment.setArguments(bundle);

                            bottomTaskEditFragment.show(getChildFragmentManager(),bottomTaskEditFragment.getTag());
//                            Log.d("TAG", "onItemClick: "+postKey);
                        }
                        else{
                            Toast.makeText(getContext(), "You can not edit it", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onRadioButtonClick(View view, Tasks tasks) {
                        Log.d("TAG", "onRadioButtonClick: "+tasks.getTask());
                    }
                }));





            }




            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });









        return root;

    }



@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}