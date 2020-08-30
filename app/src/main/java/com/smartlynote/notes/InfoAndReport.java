package com.smartlynote.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InfoAndReport extends AppCompatActivity {

    EditText etFeature, etReport, etMessage;
    Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_and_report);

        etFeature = findViewById(R.id.etFeature);
        etReport = findViewById(R.id.etReport);
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String togetherText = "";

                String mFeature = etFeature.getText().toString();
                String mReport = etReport.getText().toString();
                String mMessage = etMessage.getText().toString();

                togetherText = "Feature: " + mFeature + "\n\n" + "Report: " + mReport + "\n\n" + "Message: " + mMessage + "\n\n";

                if (!(mFeature.isEmpty() && mReport.isEmpty() && mMessage.isEmpty())) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"smartlynote@gmail.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Message for Note Smartly");
                    i.putExtra(Intent.EXTRA_TEXT, togetherText);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(InfoAndReport.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }

                Intent toMain = new Intent(InfoAndReport.this, MainActivity.class);
                startActivity(toMain);
                finish();

            }
        });

    }
}