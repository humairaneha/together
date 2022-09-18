package com.apptask.together.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.Models.Tasks;
import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentBottomTaskInputBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.Timestamp;
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
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomTaskInputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomTaskInputFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomTaskInputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomTaskInputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomTaskInputFragment newInstance(String param1, String param2) {
        BottomTaskInputFragment fragment = new BottomTaskInputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private EditText task;
    private ImageButton taskbtn, calbtn;
    private FragmentBottomTaskInputBinding binding;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate, createdDate;
    Calendar calendar;
    private LoadingDialog mLoadingDialog;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String currentDate, currentTime, postRandomName;


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


        View rootView = inflater.inflate(R.layout.fragment_bottom_task_input, container, false);
        // Inflate the layout for this fragment
        binding = FragmentBottomTaskInputBinding
                .inflate(inflater, container, false);
        View root = binding.getRoot();
        task = rootView.findViewById(R.id.task_txt);
        taskbtn = rootView.findViewById(R.id.taskAddbtn);
        //calendarGroup=rootView.findViewById(R.id.calendar_group);
        calendarView = rootView.findViewById(R.id.calendar_view);
        calbtn = rootView.findViewById(R.id.date_btn);
        calendar = Calendar.getInstance();
        mLoadingDialog=new LoadingDialog(getActivity());

        Chip todayChip = rootView.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = rootView.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = rootView.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);
        calendarView.setVisibility(rootView.GONE);
        calbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                if (calendarView.getVisibility() == rootView.GONE || calendarView.getVisibility() == rootView.INVISIBLE) {
                    // Toast.makeText(getContext(), "gone", Toast.LENGTH_SHORT).show();
                    calendarView.setVisibility(rootView.VISIBLE);
                } else {
                    calendarView.setVisibility(rootView.INVISIBLE);
                }
            }
        });
        calendar = Calendar.getInstance();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE,dd MMM yy");
                dueDate = calendar.getTime();

                Log.d("TAG", "onSelectedDayChange: " + dueDate);
            }
        });

//
//        taskbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            String task_txt = task.getText().toString().trim();
//                Calendar calDate = Calendar.getInstance();
//
//                Tasks tasks = new Tasks();
//                tasks.setCreated_at(calDate.getTime());
//                Log.d("TAG", "onClick: "+tasks.getCreated_at());
//                Timestamp timestamp =new Timestamp(calDate.getTime());
//
//                Log.d("TAG", "onClick: "+timestamp);
//
//
//
//            }
//        });


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        taskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String task_txt = task.getText().toString().trim();

                if (!TextUtils.isEmpty(task_txt) && dueDate != null) {
                    mLoadingDialog.startLoadingDialog();
                    Calendar calDate = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM,yyyy");
                    currentDate = dateFormat.format(calDate.getTime());
                    createdDate = calDate.getTime();
                    Calendar calTime = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    currentTime = timeFormat.format(calTime.getTime());

                    postRandomName = currentDate + currentTime;


                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {

                        database.getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    Users users = snapshot.getValue(Users.class);
                                    Tasks tasks = new Tasks();
                                    tasks.setTask(task_txt);
                                    tasks.setUserId(user.getUid());
                                    tasks.setUserEmail(users.getEmail());
                                    tasks.setUserName(users.getUsername());
                                    tasks.setIs_done(false);

                                    //String dueDate= dateFormat.format(calendar.getTime());
                                    tasks.setDue_date(dueDate);
                                    tasks.setCreated_at(createdDate);


                                    tasks.setTaskId(user.getUid() + postRandomName);

                                    databaseReference.child("Tasks").child(user.getUid() + postRandomName).setValue(tasks)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    mLoadingDialog.dismissDialog();
                                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                                    dismiss();

                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    mLoadingDialog.dismissDialog();
                                                    Toast.makeText(getActivity(), "Task Added", Toast.LENGTH_SHORT).show();
                                                    task.setText("");


                                                    dismiss();


                                                }


                                            });


                                }


                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }


                } else {
                    Toast.makeText(getContext(), "Please write the task to continue", Toast.LENGTH_SHORT).show();
                }
            }


        });


        return rootView;
    }


    @Override
    public int getTheme() {

        return R.style.CustomBottomSheetDialog;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.today_chip) {

            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calendar.getTime();


        } else if (id == R.id.tomorrow_chip) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calendar.getTime();
        } else if (id == R.id.next_week_chip) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            dueDate = calendar.getTime();
        }
    }
}