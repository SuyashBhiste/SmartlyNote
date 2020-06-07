package com.example.notes;

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

    public CustomAdapter(ArrayList<CardDetails> cardArray) {
        detailsArray = cardArray;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton del;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            del = itemView.findViewById(R.id.delete);

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    detailsArray.remove(pos);
                    notifyItemRemoved(pos);
                }
            });

        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        CustomViewHolder tempVH = new CustomViewHolder(v);
        return tempVH;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        CardDetails tempDetails = detailsArray.get(position);
        holder.title.setText(tempDetails.getTitle());
    }

    @Override
    public int getItemCount() {
        return detailsArray.size();
    }

}
