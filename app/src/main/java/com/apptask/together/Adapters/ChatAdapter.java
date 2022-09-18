package com.apptask.together.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apptask.together.ChatActivity;
import com.apptask.together.Models.Messages;
import com.apptask.together.R;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<Messages>messageList;
    Context context;
    int SENDER_VIEW_TYPE=1;
    int RECEIVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<Messages> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    public ChatAdapter(ArrayList<Messages> messageList, ChatActivity chatActivity) {
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
if(viewType == SENDER_VIEW_TYPE){
    View view= LayoutInflater.from(context).inflate(R.layout.sample_sender_layout,parent,false);
    return new SenderViewHolder(view);

}
   else{


        View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver_layout,parent,false);
        return new ReceiverViewHolder(view);
}
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
         Messages messages = messageList.get(position);

         if(holder.getClass()==SenderViewHolder.class){

             ((SenderViewHolder) holder).senderMsg.setText(messages.getText());
             ((SenderViewHolder) holder).senderName.setText(messages.getUsername());
             SimpleDateFormat sfd = new SimpleDateFormat("hh:mm");
             String time =sfd.format(new Date(messages.getTimestamp()));
             ((SenderViewHolder) holder).senderTime.setText(time);
         }
         else{


             ((ReceiverViewHolder)holder).receiverMsg.setText(messages.getText());
             ((ReceiverViewHolder) holder).receiverName.setText(messages.getUsername());
             SimpleDateFormat sfd = new SimpleDateFormat("hh:mm");
             String time =sfd.format(new Date(messages.getTimestamp()));
             ((ReceiverViewHolder) holder).receiverTime.setText(time);

         }

    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if(messageList.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }
        else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverName, receiverTime;

        public ReceiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.rcv_txt);
            receiverName=itemView.findViewById(R.id.rcvr_name);
            receiverTime=itemView.findViewById(R.id.rcv_time);
        }
    }


    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderName, senderTime;

        public SenderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.sender_text);
            senderName=itemView.findViewById(R.id.sender_name);
            senderTime=itemView.findViewById(R.id.sent_time);
        }
    }

}

