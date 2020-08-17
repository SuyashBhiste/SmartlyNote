package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<CardDetails> cardArray;

    static RecyclerView recyclerView;
    static RecyclerView.Adapter<CustomAdapter.CustomViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;
    static int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvMain);
        cardArray = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);

        CustomAdapter.getContext(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CustomAdapter(cardArray);
        recyclerView.setAdapter(adapter);
        System.out.println("get");
        getData();
        System.out.println("lol");
    }

    private void getData() {
        AddActivity.notesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                count = (int) datasnapshot.getChildrenCount();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    CardDetails user = snapshot.getValue(CardDetails.class);
                    cardArray.add(user);
                }
                System.out.println("Data Fetched from Firebase");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void addPressed(View view) {
        Intent toTextActivity = new Intent(MainActivity.this, AddActivity.class);
        startActivity(toTextActivity);
    }

}
