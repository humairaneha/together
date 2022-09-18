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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.apptask.together.Adapters.FindMembersAdapter;
import com.apptask.together.Adapters.IdeaAdapter;
import com.apptask.together.Classes.RecyclerItemClickListener;
import com.apptask.together.Fragment.BottomIdeaEditFragment;
import com.apptask.together.Fragment.BottomProfileFragment;
import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Tasks;
import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentFindTeamBinding;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindTeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindTeamFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FindTeamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindTeamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindTeamFragment newInstance(String param1, String param2) {
        FindTeamFragment fragment = new FindTeamFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentFindTeamBinding binding;
    private EditText searchInputText;
    private ImageButton searchButton;
    private RecyclerView searchRecycler;
    private ArrayList<Users> userList;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
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
        View root=inflater.inflate(R.layout.fragment_find_team, container, false);

        searchInputText = root.findViewById(R.id.searchInput);
        searchButton= root.findViewById(R.id.search_btn);
        searchRecycler=root.findViewById(R.id.search_list);
        searchRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        searchRecycler.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
       auth=FirebaseAuth.getInstance();
        mLoadingDialog=new LoadingDialog(getActivity());

        userList =new ArrayList<>();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        user= auth.getCurrentUser();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mLoadingDialog.startLoadingDialog();
                String Email = searchInputText.getText().toString().toLowerCase().trim();



                Query searchPeopleByEmail=userRef.orderByChild("email").startAt(Email).endAt(Email+"\uf8ff");
                searchPeopleByEmail.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        userList.clear();
                        if(snapshot.exists()){

                            for(DataSnapshot snap:snapshot.getChildren()){

                                Users users = snap.getValue(Users.class);
                                Log.d("TAG", "onDataChange: "+users.getEmail());
                                userList.add(users);
                       mLoadingDialog.dismissDialog();
                            }
                            searchInputText.setText("");}
                            else{
                                mLoadingDialog.dismissDialog();
                            Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();

                            }



                        FindMembersAdapter adapter =new   FindMembersAdapter(userList,getContext());
                        searchRecycler.setAdapter(adapter);


                        searchRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                                searchRecycler, new RecyclerItemClickListener.OnItemClickListener(){

                            @Override
                            public void onItemClick(View view, int position) {

                                Users users =userList.get(position);
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
        });



        return root ;
    }
}