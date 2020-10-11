package com.smartlynote.notes;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.smartlynote.notes.MainActivity.auth;
import static com.smartlynote.notes.MainActivity.cardArray;

public class AddActivity extends AppCompatActivity {

    //Firebase Declarations
    static FirebaseUser user = auth.getCurrentUser();
    static FirebaseDatabase db = FirebaseDatabase.getInstance();
    static DatabaseReference rootRef = db.getReference();
    static DatabaseReference usersRef = rootRef.child("Users");
    static DatabaseReference uidRef = usersRef.child(user.getUid());

    //XML Attributes
    private TextInputEditText tbTitle, tbDescription;
    private TextView tvDate, tvTime;
    private SwitchMaterial swRemind;
    private Button btImage, btAudio;
    private ImageView ivImage;
    private ImageButton ibtAudio;
    private Button btRemoveImage;
    private Button btRemoveAudio;
    static DatabaseReference notesRef = uidRef.child("Notes");
    private Calendar cal = Calendar.getInstance();
    private Date timy, daty;
    private static int IMAGE_REQUEST_CODE = 45632;
    private static int AUDIO_REQUEST_CODE = 43652;

    //Firebase Storage Declarations
    private StorageReference storage;
    private ProgressDialog progressDialog;

    //Services Declarations
    private MediaPlayer mediaPlayer;
    private Bundle bundle;
    private Uri uriImage, uriAudio;

    private String mTitle;
    private String mDescription;
    private String mDate;
    private String mTime;
    private String strUriImage;
    private String strUriAudio;
    private int pos;
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //Assign Id's
        tbTitle = findViewById(R.id.tietTitle);
        tbDescription = findViewById(R.id.tietDescription);
        btImage = findViewById(R.id.btImage);
        btAudio = findViewById(R.id.btAudio);
        swRemind = findViewById(R.id.swRemind);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        ivImage = findViewById(R.id.ivSavedImage);
        btRemoveImage = findViewById(R.id.btRemoveImage);
        ibtAudio = findViewById(R.id.ibSavedAudio);
        btRemoveAudio = findViewById(R.id.btRemoveAudio);

        //Initialization
        mediaPlayer = new MediaPlayer();
        storage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        check = false;

        try { //Edit Note
            //Getting Card Position
            bundle = getIntent().getExtras();
            pos = getIntent().getIntExtra("pos",-1);
            System.out.println("poss"+pos);
            fnEdit();
        } catch (Exception e) { //New Note
            pos = -1;
            System.out.println("catch");
            btAudio.setVisibility(View.VISIBLE);
            btImage.setVisibility(View.VISIBLE);
        }
    }

    //Edit Note
    public void fnEdit() {
        //Fetch & set details from card
        tbTitle.setText(bundle.getString("sendTitle"));
        tbDescription.setText(bundle.getString("sendDescription"));
        tvDate.setText(bundle.getString("sendDate"));
        tvTime.setText(bundle.getString("sendTime"));
        strUriImage = bundle.getString("sendImage");
        strUriAudio = bundle.getString("sendAudio");

        //Handling Visibility
        if (!tvDate.getText().equals("") || !tvTime.getText().equals("")) {
            swRemind.setChecked(true);
            tvDate.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
        }

        btImage.setVisibility(View.GONE);
        btAudio.setVisibility(View.GONE);

        if (!bundle.getString("sendImage").isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);

            //Assign Assets
            Glide.with(this)
                    .load(Uri.parse(bundle.getString("sendImage")))
                    .into(ivImage);
            btRemoveImage.setVisibility(View.VISIBLE);
        }

        if (!bundle.getString("sendAudio").isEmpty()) {
            ibtAudio.setVisibility(View.VISIBLE);

            //Assign Assets
            try {
                progressDialog.setMessage("Loading....");
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(bundle.getString("sendAudio"));
                mediaPlayer.prepareAsync();
                progressDialog.show();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        progressDialog.dismiss();
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        ibtAudio.setImageResource(R.drawable.ic_play);
                    }
                });
                btRemoveAudio.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.w("Audio fetch", "ERROR", e);
            }
        } else {
            ibtAudio.setVisibility(View.GONE);
        }
    }

    public void audio(View view) {
        if (mediaPlayer.isPlaying()) {
            ibtAudio.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
            ibtAudio.setImageResource(R.drawable.ic_pause);
        }
    }

    public void fnSwitch(View view) {
        if (check) {
            swRemind.setChecked(false);
            tvDate.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
            check = false;
        } else {
            swRemind.setChecked(true);
            tvDate.setText("Date");
            tvTime.setText("Time");
            tvDate.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            check = true;
            Toast.makeText(AddActivity.this, "Notification feature coming soon...", Toast.LENGTH_SHORT).show();
        }
    }

    public void fnImage(View view) {
        Intent toImageGalley = new Intent(Intent.ACTION_PICK);
        toImageGalley.setType("image/*");
        if (isNetworkConnected()) {
            startActivityForResult(toImageGalley, IMAGE_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Turn On Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void fnAudio(View view) {
        Intent toAudioGallery = new Intent(Intent.ACTION_PICK);
        toAudioGallery.setType("audio/*");
        toAudioGallery.setAction(Intent.ACTION_GET_CONTENT);
        if (isNetworkConnected()) {
            startActivityForResult(toAudioGallery, AUDIO_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Turn On Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Get image path
        progressDialog.setMessage("UPLOADING...");
        Uri path = data.getData();
        progressDialog.show();

        //Create path in firebase
        if (requestCode == IMAGE_REQUEST_CODE) {
            StorageReference imagePath = storage.child("Images/" + path.getLastPathSegment());
            upload(imagePath, path, 0);
        } else if (requestCode == AUDIO_REQUEST_CODE) {
            StorageReference audioPath = storage.child("Audio/" + path.getLastPathSegment());
            upload(audioPath, path, 1);
        }
    }

    public void upload(final StorageReference filePath, Uri uri, final int check) {
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AddActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

                //Fetching Firebase upload media uri
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (check == 0) {
                            btImage.setText("Image Uploaded");
                            uriImage = uri;
                        } else if (check == 1) {
                            btAudio.setText("Audio Uploaded");
                            uriAudio = uri;
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddActivity.this, "Upload Failed. Check Internet", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Log.w("Media upload", "Failed", e);
            }
        });
    }

    public void date(View view) {
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        String dateFormat = "dd/MM/yy";
        final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                daty = cal.getTime();
                mDate = sdf.format(daty);
                tvDate.setText(mDate);
                System.out.println("mDate: " + mDate);
            }
        }, mYear, mMonth, mDay);
        mDatePickerDialog.show();
    }

    public void time(View view) {
        int mHour = cal.get(Calendar.HOUR);
        int mMinute = cal.get(Calendar.MINUTE);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:MM", Locale.getDefault());

        TimePickerDialog mTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                cal.set(Calendar.HOUR, hour);
                cal.set(Calendar.MINUTE, minute);
                timy = cal.getTime();
                mTime = sdf.format(timy);
                tvTime.setText(mTime);
                System.out.println("mTme: " + mTime);
            }
        }, mHour, mMinute, false);
        mTimePickerDialog.show();
    }

    //Adding done button in navbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    //On done button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.iDone) {
            //Grab inserted data
            mTitle = tbTitle.getText().toString();
            mDescription = tbDescription.getText().toString();

            //Check switch condition
            if (!mTitle.isEmpty()) {
                //Check Internet
                if (isNetworkConnected()) {
                    if (!swRemind.isChecked()) {
                        mDate = null;
                        mTime = null;
                    }
                    int id=(bundle.getInt("sendId"));
                    System.out.println(id + "id");
                    if (pos != -1) {
                        //Edit
                        System.out.println("edit");
                        System.out.println(pos);
                        strUriAudio = bundle.getString("sendAudio");

                        System.out.println(cardArray.get(pos).getId());
                        CardDetails cd = new CardDetails(mTitle, mDate, mTime, mDescription, strUriImage, strUriAudio, cardArray.get(pos).getId());
                        cardArray.set(pos, cd);
                        uidRef = notesRef.child(cardArray.get(pos).getId());
                        uploadData(cd);

                        Toast.makeText(this, "Restart your app", Toast.LENGTH_SHORT).show();
                        Log.i("Edit Note", String.valueOf(pos));
                    } else {
                        //New
//                        CardDetails cd = new CardDetails(mTitle, mDate, mTime, mDescription, String.valueOf(uriImage), String.valueOf(uriAudio), String.valueOf(MainActivity.id++));
                        CardDetails cd = new CardDetails(mTitle, mDate, mTime, mDescription, String.valueOf(uriImage), String.valueOf(uriAudio), String.valueOf(MainActivity.id));
//                        Bundle b = getIntent().getExtras();
//                        int cnt = b.getInt("sendCount");
                        cardArray.add(cd);
                        uidRef = notesRef.child(String.valueOf(MainActivity.id++));
                        Log.i("New Note", "Created");
                        uploadData(cd);
                    }


                    try {
                        Log.i("Alarm", "Added");
                    } catch (Exception e) {
                        Log.i("Alarm", "Failed");
                        Toast.makeText(AddActivity.this, "Not Working", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Turn On Internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Title is Empty", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadData(CardDetails cd) {
        uidRef.child("Title").setValue(cd.getTitle());
        uidRef.child("Date").setValue(cd.getDate());
        uidRef.child("Time").setValue(cd.getTime());
        uidRef.child("Description").setValue(cd.getDescription());
        uidRef.child("Image").setValue(cd.getImage());
        uidRef.child("Audio").setValue(cd.getAudio());
        uidRef.child("Id").setValue(cd.getId());
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

    public void fnRemoveImage(View view) {
        ivImage.setVisibility(View.GONE);
        btRemoveImage.setVisibility(View.GONE);
        strUriImage = "";
    }

    public void fnRemoveAudio(View view) {
        ibtAudio.setVisibility(View.GONE);
        btRemoveAudio.setVisibility(View.GONE);
        strUriAudio = "";
    }
}
