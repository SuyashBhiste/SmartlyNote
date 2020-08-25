package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Notes Array Declarations
    static ArrayList<CardDetails> cardArray;
    //XML Attributes
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<CustomAdapter.CustomViewHolder> adapter;
    //Firebase Declarations
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference rootRef;
    private DatabaseReference usersRef;
    private DatabaseReference uidRef;
    private DatabaseReference notesRef;
    static int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning Id's
        recyclerView = findViewById(R.id.rvMain);

        //Initialization
        cardArray = new ArrayList<>();
        adapter = new CustomAdapter(cardArray);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        usersRef = rootRef.child("Users");
        uidRef = usersRef.child(user.getUid());
        notesRef = uidRef.child("Notes");

        //Setting RecyclerView properties
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //Fetch notes from firebase
        CustomAdapter.getContext(this);
        getData();
    }

    private void getData() {
        if (!isNetworkConnected()) {
            Toast.makeText(this, "You are offline. Turn on internet.", Toast.LENGTH_SHORT).show();
        }

        notesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    CardDetails user = snapshot.getValue(CardDetails.class);
                    cardArray.add(user);
                }

                Log.i("All Data Fetch Success", user.getEmail());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "No Internet. Unable to fetch notes", Toast.LENGTH_SHORT).show();
                Log.w("All Data Fetch fail", user.getEmail(), error.toException());
            }
        });


    }

    public void addPressed(View view) {
        Intent toTextActivity = new Intent(MainActivity.this, AddActivity.class);
        toTextActivity.putExtra("sendCount", cardArray.size());
        startActivity(toTextActivity);
    }

    //Adding logout button in navbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_actionbar, menu);
        return true;
    }

    //On logout button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.iLogOut) {
            System.out.println("Logout");
            auth.signOut();
            Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(toLogin);
            finish();
        }

        if (item.getItemId() == R.id.iReport) {
            System.out.println("Report");
            auth.signOut();
            Intent toInfoAndReport = new Intent(MainActivity.this, InfoAndReport.class);
            startActivity(toInfoAndReport);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

}
