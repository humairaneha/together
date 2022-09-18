package com.apptask.together.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class coWorkerAdapter extends RecyclerView.Adapter<coWorkerAdapter.viewHolder> {

    ArrayList<Users>coWorkerList;
    Context context;




    public coWorkerAdapter( ArrayList<Users>coWorkerList,Context context){
        this.context=context;
        this.coWorkerList=coWorkerList;
    }


    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_user_layout,parent,false);
        return new coWorkerAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        Users users = coWorkerList.get(position);


        String url =users.getProfilepic();
        if(url!=null){
            Log.d("TAG", "onBindViewHolder: "+ users.getProfilepic());

            Picasso.get().load(url)
                    .resize(300,300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_icons8_account)
                    .into(holder.mCircleImageView);
            Picasso.get().setLoggingEnabled(true);}

        holder.userName.setText(users.getUsername());
        holder.userEmail.setText(users.getEmail());

    }

    @Override
    public int getItemCount() {
        return coWorkerList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private de.hdodenhof.circleimageview.CircleImageView mCircleImageView;
        private TextView userName,userEmail;

        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mCircleImageView=itemView.findViewById(R.id.search_image);
            userName=itemView.findViewById(R.id.searchUsername);
            userEmail=itemView.findViewById(R.id.searchUserEmail);

        }
    }
}
