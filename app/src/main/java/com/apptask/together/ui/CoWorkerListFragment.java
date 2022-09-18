package com.apptask.together.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apptask.together.Adapters.FindMembersAdapter;
import com.apptask.together.Adapters.coWorkerAdapter;
import com.apptask.together.Classes.RecyclerItemClickListener;
import com.apptask.together.Fragment.BottomProfileFragment;
import com.apptask.together.Models.Tasks;
import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CoWorkerListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CoWorkerListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CoWorkerListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CoWorkerListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CoWorkerListFragment newInstance(String param1, String param2) {
        CoWorkerListFragment fragment = new CoWorkerListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private RecyclerView co_workerRecycler;
    private ArrayList<Users> coWorkerList;
    private ArrayList<String>keyList;
    private DatabaseReference userRef,friendRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
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
        View view =inflater.inflate(R.layout.fragment_co_worker_list, container, false);
        co_workerRecycler=view.findViewById(R.id.coWorkerRecycler);
        co_workerRecycler.setHasFixedSize(true);
        co_workerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        keyList=new ArrayList<>();
        coWorkerList =new ArrayList<>();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        friendRef=FirebaseDatabase.getInstance().getReference().child("Friends").child(user.getUid());


       friendRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               keyList.clear();
               coWorkerList.clear();

               if(snapshot.exists()){


                   for(DataSnapshot snap: snapshot.getChildren()){

                       String key = snap.getKey();
                       keyList.add(key);
                       Log.d("TAGloop", "onDataChange: "+key);


                   }
                   Log.d("TAG", "onDataChange: "+keyList);
                   for (String k:keyList) {
                       Users users =new Users();
                       userRef.child(k).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot1) {

                               if(snapshot1.exists()){


                                   users.setEmail(snapshot1.child("email").getValue().toString());
                                   users.setUsername(snapshot1.child("username").getValue().toString());
                                   users.setUserId(k);
                                   if(snapshot1.hasChild("profilepic")){
                                       users.setProfilepic(snapshot1.child("profilepic").getValue().toString());
                                       Log.d("TAG", "onDataChange: "+users.getEmail()+users.getUsername()+users.getProfilepic());
                                   }
                                   else{Log.d("TAG", "onDataChange: "+users.getEmail()+users.getUsername());}

//                                   for(DataSnapshot snap1: snapshot1.getChildren()){
//                                      // Users users = snap1.getValue(Users.class);
//
//
//                                   }
                                   coWorkerList.add(users);
//


                               }
                               coWorkerAdapter adapter= new coWorkerAdapter(coWorkerList,getContext());
                               co_workerRecycler.setAdapter(adapter);


                               co_workerRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                                      co_workerRecycler, new RecyclerItemClickListener.OnItemClickListener(){

                                   @Override
                                   public void onItemClick(View view, int position) {

                                       Users users =coWorkerList.get(position);
                                       Bundle bundle =new Bundle();
                                       final String key = users.getUserId();
                                       bundle.putString("Key",key);
                                       BottomProfileFragment bottomProfileFragment=new BottomProfileFragment();
                                       bottomProfileFragment.setArguments(bundle);

                                       bottomProfileFragment.show(getChildFragmentManager(),bottomProfileFragment.getTag());






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



                   }






               }



           }


           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });


        return view;
    }
}