package com.apptask.together.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Models.Ideas;
import com.apptask.together.Models.Users;
import com.apptask.together.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FindMembersAdapter extends RecyclerView.Adapter<FindMembersAdapter.viewHolder> {

    ArrayList<Users>userList;
    Context context;


    public FindMembersAdapter(ArrayList<Users>userList,Context context){

        this.context=context;
        this.userList=userList;
    }

    DatabaseReference userRef;
    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_user_layout,parent,false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
     Users users = userList.get(position);


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
        return userList.size();
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
