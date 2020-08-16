package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<CardDetails> detailsArray;
    public static Context context;

    public CustomAdapter(ArrayList<CardDetails> cardArray) {
        detailsArray = cardArray;
    }

    public static void getContext(Context mContext) {
        context = mContext;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        CardDetails tempDetails = detailsArray.get(position);
        holder.title.setText(tempDetails.getTitle());
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        return new CustomViewHolder(v);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton del;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            del = itemView.findViewById(R.id.btDelete);

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
//                    detailsArray.remove(pos);
//                    notifyItemRemoved(pos);
                    AddActivity.notesRef.child(String.valueOf(pos)).removeValue();
//                    notifyDataSetChanged();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Bundle bundle = new Bundle();
                    bundle.putString("sendTitle", detailsArray.get(pos).getTitle());
                    bundle.putString("sendDescription", detailsArray.get(pos).getDescription());
                    bundle.putString("sendDate", detailsArray.get(pos).getDate());
                    bundle.putString("sendTime", detailsArray.get(pos).getTime());
                    bundle.putString("sendImage", detailsArray.get(pos).getImage());
                    bundle.putString("sendAudio", detailsArray.get(pos).getAudio());
                    bundle.putString("sendPos", String.valueOf(pos));

                    Intent intent = new Intent(context, AddActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return detailsArray.size();
    }

}
