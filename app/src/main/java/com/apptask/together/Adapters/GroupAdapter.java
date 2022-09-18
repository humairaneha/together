package com.apptask.together.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Models.Users;
import com.apptask.together.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.viewHolder> {

    ArrayList<String> TeamList;
    Context context;

    public GroupAdapter( ArrayList<String> TeamList,Context context){
        this.context=context;
        this.TeamList=TeamList;
    }


    @NonNull
    @NotNull
    @Override
    public GroupAdapter.viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_chat_layout,parent,false);
        return new GroupAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull GroupAdapter.viewHolder holder, int position) {

        String name = TeamList.get(position);
        holder.text.setText(name);

    }

    @Override
    public int getItemCount() {
        return TeamList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private TextView text;
        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.team);
        }
    }
}
