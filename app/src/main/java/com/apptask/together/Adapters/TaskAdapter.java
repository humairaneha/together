package com.apptask.together.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Classes.RecyclerItemClickListener;
import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.Tasks;
import com.apptask.together.R;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.viewHolder> {

    ArrayList<Tasks>taskList;
    Context context;
    Calendar calendar=Calendar.getInstance();
    DatabaseReference taskRef;
    RecyclerItemClickListener.OnItemClickListener mOnItemClickListener;
    Date currentDate;

    public TaskAdapter( ArrayList<Tasks>taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_task_layout,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {


        Tasks tasks = taskList.get(position);


        taskRef= FirebaseDatabase.getInstance().getReference().child("Tasks");

        holder.task.setText(tasks.getTask());
        holder.radioButton.setChecked(tasks.getIs_done());
        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                taskRef.child(tasks.getTaskId()).child("is_done").setValue(true);
                }
                else{taskRef.child(tasks.getTaskId()).child("is_done").setValue(false);}

            }
        });


        holder.task_user.setText(tasks.getUserName());
        calendar.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE,dd MMM,yy");
        holder.dueDateChip.setText(dateFormat.format(tasks.getDue_date()).toString());
        if(tasks.getIs_done()){holder.dueDateChip.setText("Completed");}
        Calendar cal =Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-1);
        //currentDate=new Date();
        currentDate=cal.getTime();
        int E = currentDate.compareTo(tasks.getDue_date());
        if(!tasks.getIs_done()) {
            if (E > 0) {
                holder.dueDateChip.setText("Expired");
            }
            else {
                holder.dueDateChip.setText(dateFormat.format(tasks.getDue_date()).toString());

            }
        }



    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView task,task_user;
            RadioButton radioButton;
            Chip dueDateChip;

        public viewHolder(@NonNull @NotNull View itemView){
            super(itemView);

          task= itemView.findViewById(R.id.name_txt);
          radioButton=itemView.findViewById(R.id.check_btn);
          task_user=itemView.findViewById(R.id.username_txt);
          dueDateChip=itemView.findViewById(R.id.dueDate_chip);
          radioButton.setOnClickListener(this);




        }

        @Override
        public void onClick(View view) {

        int id = view.getId();
        if(id==R.id.check_btn){

           radioButton.toggle();





        }
        }
    }
}
