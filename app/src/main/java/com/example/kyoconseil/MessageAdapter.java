package com.example.kyoconseil;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messagelist ;

    public MessageAdapter(List<Message> messagelist) {
        this.messagelist= messagelist;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


       @SuppressLint("InflateParams")
       View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,null);
       MyViewHolder myViewHolder = new MyViewHolder(chatView);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            Message message = messagelist.get(position);
            if (message.getSentBy().equals(Message.SENT_BY_ME)){
                holder.leftchat.setVisibility(View.GONE);
                holder.rightchat.setVisibility(View.VISIBLE);
                holder.rightText.setText(message.getMessage());
            }
            else {
                holder.leftchat.setVisibility(View.VISIBLE);
                holder.rightchat.setVisibility(View.GONE);
                holder.leftText.setText(message.getMessage());
            }

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public class  MyViewHolder  extends RecyclerView.ViewHolder{
LinearLayout leftchat , rightchat ;
TextView leftText , rightText ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftchat =itemView.findViewById(R.id.Left_view);
            rightText = itemView.findViewById(R.id.right_message);
            leftText= itemView.findViewById(R.id.left_message);
            rightchat= itemView.findViewById(R.id.Right_view);



        }
    }

}
