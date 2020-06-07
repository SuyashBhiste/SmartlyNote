package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Objects;

import com.google.android.material.textfield.TextInputEditText;

public class TextActivity extends AppCompatActivity {

    private TextView mdateBox, mtimeBox;
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        TextInputEditText titleTB, descTB;
        Switch remindMeSwtich;

        remindMeSwtich = findViewById(R.id.remindMeSwitch);
        mdateBox = findViewById(R.id.dateBox);
        mtimeBox = findViewById(R.id.timeBox);
        titleTB = findViewById(R.id.title);
        descTB = findViewById(R.id.desc);

        String title = Objects.requireNonNull(titleTB.getText()).toString();
        String desc = Objects.requireNonNull(descTB.getText()).toString();

        remindMeSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mdateBox.setVisibility(View.VISIBLE);
                    mtimeBox.setVisibility(View.VISIBLE);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void datePick(View view) {
        final int mDay, mMonth, mYear;
        mDay = cal.get(Calendar.DATE);
        mMonth = cal.get(Calendar.MONTH);
        mYear = cal.get(Calendar.YEAR);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mdateBox.setText(String.format("%d-%d-%d", day, month+1, year));
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

    public void timePick(View view) {
        int hh, mm;
        hh = cal.get(Calendar.HOUR_OF_DAY);
        mm = cal.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                mtimeBox.setText(String.format("%d:%d", hour, min));
            }
        }, hh, mm, false);
        tpd.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            Toast.makeText(TextActivity.this, "Done clicked", Toast.LENGTH_SHORT).show();

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
