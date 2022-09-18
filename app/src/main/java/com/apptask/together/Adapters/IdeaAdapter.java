package com.apptask.together.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.Models.Ideas;
import com.apptask.together.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.viewHolder> {

    ArrayList<Ideas>ideaList;
    Context context;

    public IdeaAdapter(ArrayList<Ideas> ideaList, Context context) {
        this.ideaList = ideaList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_idea_layout,parent,false);
        return new viewHolder(view);
    }
     DatabaseReference ideaRef;
    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {


        Ideas ideas = ideaList.get(position);
    String url =ideas.getProfilepic();
    if(url!=null){Log.d("TAG", "onBindViewHolder: "+ ideas.getProfilepic());
        Picasso.get().load(url)
                .resize(300,300)
                .centerCrop()
                .placeholder(R.drawable.ic_icons8_account)
                .into(holder.profile_image);
        Picasso.get().setLoggingEnabled(true);}

        holder.date_txt.setText(ideas.getDate());
        holder.idea_txt.setText(ideas.getIdea());
        holder.title_txt.setText(ideas.getTitle());
        holder.username_txt.setText(ideas.getUsername());




    }


    @Override
    public int getItemCount() {
        return ideaList.size();
    }

    public  class viewHolder extends RecyclerView.ViewHolder{

        TextView username_txt,title_txt,idea_txt,date_txt;
        de.hdodenhof.circleimageview.CircleImageView profile_image;

        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            username_txt =itemView.findViewById(R.id.name_txt);
            title_txt = itemView.findViewById(R.id.title_txt);
            idea_txt = itemView.findViewById(R.id.idea_txt);
            date_txt=itemView.findViewById(R.id.date);
            profile_image =itemView.findViewById(R.id.profile_pic);



        }
    }
}
