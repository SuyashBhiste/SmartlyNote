package com.example.notes;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import static com.example.notes.LoginActivity.usersRef;
import static com.example.notes.MainActivity.cardArray;
import static com.example.notes.MainActivity.count;

public class AddActivity extends AppCompatActivity {

    private TextInputEditText tietTitle, tietDescription;
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Switch sRemindMe;
    private TextView tvDate, tvTime;

    private String mTitle;
    private String mDescription;
    private String mDate;
    private String mTime;
    static DatabaseReference uidRef = usersRef.child(mAuth.getCurrentUser().getUid());
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mHour;
    private int mMinute;
    private static int i = 0;
    boolean swCheck = false;
    static DatabaseReference notesRef = uidRef.child("Notes");
    private Calendar cal;
    private Uri uriImage;
    private Uri uriAudio;
    private Button btImage, btAudio;
    private int pos;
    private DatabaseReference uniqueRef;
    private StorageReference mStorage;
    private StorageReference imageName;
    private StorageReference audioName;
    private ProgressDialog mProgressDialog;
    private static int IMAGE_REQUEST_CODE = 45632;
    private static int AUDIO_REQUEST_CODE = 43652;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        tietTitle = findViewById(R.id.tietTitle);
        tietDescription = findViewById(R.id.tietDescription);
        btImage = findViewById(R.id.btImage);
        btAudio = findViewById(R.id.btAudio);
        sRemindMe = findViewById(R.id.sRemindMe);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        cal = Calendar.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mDay = cal.get(Calendar.DATE);
        mMonth = cal.get(Calendar.MONTH);
        mYear = cal.get(Calendar.YEAR);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);

        try {
            final Bundle bundle = getIntent().getExtras();
            tietTitle.setText(bundle.getString("sendTitle"));
            tietDescription.setText(bundle.getString("sendDescription"));
            tvDate.setText(bundle.getString("sendDate"));
            tvTime.setText(bundle.getString("sendTime"));
            pos = Integer.parseInt(bundle.getString("sendPos"));
            btImage.setText("View Image");
            btAudio.setText("Listen Audio");

            btImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(bundle.getString("sendImage")));
                    startActivity(i);
                }
            });
            btAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(bundle.getString("sendAudio")));
                    startActivity(i);
                }
            });

            sRemindMe.setChecked(true);
            tvDate.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            Log.i("No preData", "False");

        } catch (Exception e) {
            Log.i("No preData", "True");
            pos = -1;

            btImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toImageGalley = new Intent(Intent.ACTION_PICK);
                    toImageGalley.setType("image/*");
                    startActivityForResult(toImageGalley, IMAGE_REQUEST_CODE);
                }
            });

            btAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toAudioGallery = new Intent(Intent.ACTION_PICK);
                    toAudioGallery.setType("audio/*");
                    startActivityForResult(toAudioGallery, AUDIO_REQUEST_CODE);
                }
            });

            sRemindMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean status) {
                    if (status) {
                        tvDate.setVisibility(View.VISIBLE);
                        tvTime.setVisibility(View.VISIBLE);
                        swCheck = true;
                    } else {
                        tvDate.setVisibility(View.INVISIBLE);
                        tvTime.setVisibility(View.INVISIBLE);
                        swCheck = false;
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mProgressDialog.setMessage("UPLOADING...");

        if (requestCode == IMAGE_REQUEST_CODE) {
            uriImage = data.getData();
            mProgressDialog.show();

            imageName = mStorage.child("Images/" + uriImage.getLastPathSegment());
            upload(imageName, uriImage, 0);
        } else if (requestCode == AUDIO_REQUEST_CODE) {
            uriAudio = data.getData();
            mProgressDialog.show();

            audioName = mStorage.child("Audio/" + uriAudio.getLastPathSegment());
            upload(audioName, uriAudio, 1);
        }
    }

    public void upload(final StorageReference fileName, Uri uri, final int check) {
        fileName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                fileName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (check == 0) {
                            uriImage = uri;
                        } else if (check == 1) {
                            uriAudio = uri;
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    public void logicDate(View view) {
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                mDate = day + "/" + month + "/" + year;
                tvDate.setText(mDate);
            }
        }, mYear, mMonth, mDay);
        mDatePickerDialog.show();
    }

    public void logicTime(View view) {
        TimePickerDialog mTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mTime = hour + ":" + minute;
                tvTime.setText(mTime);
            }
        }, mHour, mMinute, false);
        mTimePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.iDone) {
            mTitle = tietTitle.getText().toString();
            mDescription = tietDescription.getText().toString();

            if (!mTitle.isEmpty()) {
                if (!swCheck) {
                    mDate = null;
                    mTime = null;
                }
                CardDetails cd = new CardDetails(mTitle, mDate, mTime, mDescription, String.valueOf(uriImage), String.valueOf(uriAudio));
                if (pos != -1) {
                    cardArray.add(pos, cd);
                    uniqueRef = notesRef.child(String.valueOf(pos));
                    Log.i("Note Edited: ", "TRUE");
                } else {
                    cardArray.add(cd);
                    uniqueRef = notesRef.child(String.valueOf(count++));
                }

                uploadData(cd);

//                adapter.notifyDataSetChanged();

                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Title is Empty", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadData(CardDetails cd) {
        uniqueRef.child("Title").setValue(cd.getTitle());
        uniqueRef.child("Date").setValue(cd.getDate());
        uniqueRef.child("Time").setValue(cd.getTime());
        uniqueRef.child("Description").setValue(cd.getDescription());
        uniqueRef.child("Image").setValue(cd.getImage());
        uniqueRef.child("Audio").setValue(cd.getAudio());
    }

}
