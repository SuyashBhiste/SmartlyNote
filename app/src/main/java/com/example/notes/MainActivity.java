package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addBtn, textBtn, cameraBtn, micBtn, cancelBtn;
    private ArrayList<CardDetails> cardArray;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference();
    DatabaseReference rootref = dbRef.child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById(R.id.addBtn);
        textBtn = findViewById(R.id.textBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        micBtn = findViewById(R.id.micBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        rawData();
        config();
    }

    private void rawData() {
        cardArray = new ArrayList<>();

        cardArray.add(new CardDetails("Get Started!"));
    }

    private void config() {
        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        adapter = new CustomAdapter(cardArray);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void addData(String mtitle, String mDesc, String date, String time) {

    }

    //Button Click Visibility Settings

    public void addPressed(View view) {
        cancelBtn.setVisibility(View.VISIBLE);
        textBtn.setVisibility(View.VISIBLE);
        cameraBtn.setVisibility(View.VISIBLE);
        micBtn.setVisibility(View.VISIBLE);
        addBtn.setVisibility(View.INVISIBLE);
    }

    public void cancelPressed(View view) {
        cancelBtn.setVisibility(View.INVISIBLE);
        textBtn.setVisibility(View.INVISIBLE);
        cameraBtn.setVisibility(View.INVISIBLE);
        micBtn.setVisibility(View.INVISIBLE);
        addBtn.setVisibility(View.VISIBLE);
    }

    public void textPressed(View view) {
        Intent main2text = new Intent(MainActivity.this, TextActivity.class);
        startActivity(main2text);
    }

    public void cameraPressed(View view) {
        Intent main2camera = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(main2camera);
        finish();
    }

    public void micPressed(View view) {
        Intent main2mic = new Intent(MainActivity.this, MicActivity.class);
        startActivity(main2mic);
        finish();
    }

}
