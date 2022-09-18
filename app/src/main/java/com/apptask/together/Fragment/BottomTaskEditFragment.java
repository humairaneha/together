package com.apptask.together.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.apptask.together.Models.LoadingDialog;
import com.apptask.together.R;
import com.apptask.together.databinding.FragmentBottomIdeaEditBinding;
import com.apptask.together.databinding.FragmentBottomTaskEditBinding;
import com.apptask.together.databinding.FragmentBottomTaskInputBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
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
 * Use the {@link BottomTaskEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomTaskEditFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomTaskEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomTaskEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomTaskEditFragment newInstance(String param1, String param2) {
        BottomTaskEditFragment fragment = new BottomTaskEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference,taskRef;
    ///private FragmentBottomIdeaEditBinding binding;
    private EditText task_edit;
    private ImageButton taskbtn,calbtn,delbtn;
    //private FragmentBottomTaskEditBinding binding;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate,createdDate;
    Calendar calendar;
    private String currentDate,currentTime,postRandomName;
    private RadioButton radioButton;
    ConstraintLayout mLayout;
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
        View view =inflater.inflate(R.layout.fragment_bottom_task_edit, container, false);
        task_edit=view.findViewById(R.id.task_txt);
        taskbtn=view.findViewById(R.id.taskAddbtn);
        //calendarGroup=rootView.findViewById(R.id.calendar_group);
        calendarView=view.findViewById(R.id.calendar_view);
        calbtn =view.findViewById(R.id.date_btn);
        delbtn=view.findViewById(R.id.delete_img_btn);
        calendar=Calendar.getInstance();
        radioButton= view.findViewById(R.id.radioButton);
        mLoadingDialog=new LoadingDialog(getActivity());
        Bundle bundle = this.getArguments();
        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);
        String taskKey = bundle.getString("Key");
        Log.d("TAG", "onCreateView:task "+taskKey);
        taskRef=FirebaseDatabase.getInstance().getReference().child("Tasks").child(taskKey);
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){


                    final String task_txt = snapshot.child("task").getValue().toString();

                    task_edit.setText(task_txt);

                    task_edit.setSelection(task_txt.length());


                   }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        calendarView.setVisibility(view.GONE);
        calbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
                if(calendarView.getVisibility()==view.GONE||calendarView.getVisibility()==view.INVISIBLE){
                    // Toast.makeText(getContext(), "gone", Toast.LENGTH_SHORT).show();
                    calendarView.setVisibility(view.VISIBLE);
                }
                else{
                    calendarView.setVisibility(view.INVISIBLE);
                }
            }
        });
         //Calendar calendar =Calendar.getInstance();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE,dd MMM yy");
                dueDate =calendar.getTime();

                Log.d("TAG", "onSelectedDayChange: "+ dueDate);
            }
        });

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mLoadingDialog.startLoadingDialog();
                taskRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                              mLoadingDialog.dismissDialog();
                                Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                task_edit.setText("");

                                dismiss();



                            }
                        });
            }
        });

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!radioButton.isChecked()){radioButton.toggle();}
                else{
                    radioButton.toggle(); //change
                }

            }
        });
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    taskRef.child("is_done").setValue(true);}
                else{taskRef.child("is_done").setValue(false);}

            }
        });

        taskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String task= task_edit.getText().toString().trim();
               final Date date= dueDate;
                taskRef.child("task").setValue(task);
                if(dueDate!=null){
                    mLoadingDialog.startLoadingDialog();

                taskRef.child("due_date").setValue(dueDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mLoadingDialog.dismissDialog();
                        Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });


            }}
        });

        return view;
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