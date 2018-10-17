package com.example.gautam.chattingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class recycle extends RecyclerView.Adapter<recycle.ViewHolder> {
   private ArrayList<String> chatfeed;
    recycle(ArrayList<String> chat)
    {
        this.chatfeed=chat;
    }
    @NonNull
    @Override
    public recycle.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cc=(CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclercard,viewGroup,false);
        return new ViewHolder(cc);
    }

    @Override
    public int getItemCount() {
        return chatfeed.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        CardView ccc=viewHolder.cardView;
        TextView textView=(TextView)ccc.findViewById(R.id.textView22);
        textView.setText(chatfeed.get(i));

    }

    class  ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ViewHolder(CardView cv)
        {
            super(cv);
            cardView=cv;
        }

    }
}
