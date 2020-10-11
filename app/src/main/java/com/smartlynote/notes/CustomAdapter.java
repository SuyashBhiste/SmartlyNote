package com.smartlynote.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<CardDetails> detailsArray;
    public static Context context;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    //Constructor
    public CustomAdapter(ArrayList<CardDetails> cardArray) {
        detailsArray = cardArray;
    }

    //Capture Context
    public static void getContext(Context mContext) {
        context = mContext;
    }

    //Inflate view for layout
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        return new CustomViewHolder(v);
    }

    //Bind title to card
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        CardDetails tempDetails = detailsArray.get(position);
        if (tempDetails.getTitle().length() > 24) {
            holder.title.setText(tempDetails.getTitle().substring(0, 22) + "...");
        } else {
            holder.title.setText(tempDetails.getTitle());
        }
    }

    //Array size
    @Override
    public int getItemCount() {
        return detailsArray.size();
    }

    //View Holder
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton del;

        //Inner Constructor
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign Id's
            title = itemView.findViewById(R.id.tvTitle);
            del = itemView.findViewById(R.id.btDelete);

            //On note delete
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Log.i("Deleted array Position", String.valueOf(pos));
                    final String delId = detailsArray.get(pos).getId();
//                    final String delId = String.valueOf(pos);
                    try {
                        //Delete image from Firebase Storage
                        String delImage = detailsArray.get(pos).getImage();
                        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(delImage);
                        System.out.println("delImage" + delImage);
                        if (!delImage.equals("null")) {
                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("Image deleted at", String.valueOf(delId));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("Image deletion failed", String.valueOf(delId));
                                }
                            });
                        }
                    }catch (Exception e){

                    }
                    //Delete audio from Firebase Storage
                    try {
                        String delAudio = detailsArray.get(pos).getAudio();
                        StorageReference musicRef = mFirebaseStorage.getReferenceFromUrl(delAudio);
                        if (!delAudio.equals("null")) {
                            musicRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("Image deleted at", String.valueOf(delId));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("Image deletion failed", String.valueOf(delId));
                                }
                            });
                        }
                    }catch (Exception e){}
                    //Delete array element from local and firebase database
                    detailsArray.remove(pos);
                    AddActivity.notesRef.child(String.valueOf(delId)).removeValue();
                    notifyDataSetChanged();
                }
            });

            //On note view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    System.out.println("position"+pos);

                    Bundle bundle = new Bundle();
                    bundle.putString("sendTitle", detailsArray.get(pos).getTitle());
                    bundle.putString("sendDescription", detailsArray.get(pos).getDescription());
                    bundle.putString("sendDate", detailsArray.get(pos).getDate());
                    bundle.putString("sendTime", detailsArray.get(pos).getTime());
                    bundle.putString("sendImage", detailsArray.get(pos).getImage());
                    bundle.putString("sendAudio", detailsArray.get(pos).getAudio());
                    bundle.putString("sendId", String.valueOf(pos));
                    bundle.putString("sendPos", String.valueOf(pos));
                    System.out.println(detailsArray.get(pos).getId()+"bundle");

                    Intent intent = new Intent(context, AddActivity.class);
                    intent.putExtras(bundle);
                    intent.putExtra("pos",pos);
                    context.startActivity(intent);
                }
            });

        }
    }

}
