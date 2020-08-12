package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<CardDetails> cardArray;

    static RecyclerView recyclerView;
    static RecyclerView.Adapter<CustomAdapter.CustomViewHolder> adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rvMain);
        cardArray = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CustomAdapter(MainActivity.cardArray);
        MainActivity.recyclerView.setAdapter(adapter);
    }

    public void addPressed(View view) {
        Intent toTextActivity = new Intent(MainActivity.this, AddActivity.class);
        startActivity(toTextActivity);
    }

}
