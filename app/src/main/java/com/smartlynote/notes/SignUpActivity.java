package com.smartlynote.notes;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    //XML Attributes
    private EditText tbName, tbEmail, tbPassword, tbConfirmPassword;

    //Firebase Declarations
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tbName = findViewById(R.id.tbName);
        tbEmail = findViewById(R.id.tbEmail);
        tbPassword = findViewById(R.id.tbPassword);
        tbConfirmPassword = findViewById(R.id.tbConfirmPassword);
    }

    public void logicSignUp(View view) {
        final String mEmail, mPassword, mName, mConfirmPassword;

        //Assign Id's
        mName = tbName.getText().toString();
        mEmail = tbEmail.getText().toString();
        mPassword = tbPassword.getText().toString();
        mConfirmPassword = tbConfirmPassword.getText().toString();

        //Initialization
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        //Validate Input Data
        boolean isNameNotNull = mName.length() != 0;
        boolean isEmailPattern = Patterns.EMAIL_ADDRESS.matcher(mEmail).matches();
        boolean isPasswordLength = mPassword.length() >= 8;
        boolean isConfirmPassword = mPassword.equals(mConfirmPassword);

        if (!isNameNotNull) {
            tbName.setError("Field is empty.");
        } else if (!isEmailPattern) {
            tbEmail.setError("Invalid email");
        } else if (!isPasswordLength) {
            tbPassword.setError("Password must be atleast 8 characters");
        } else if (!isConfirmPassword) {
            tbConfirmPassword.setError("Password didn't match");
        } else {
            auth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Account Created
                                Log.i("Account created(Email)", mEmail);
                                Log.i(mEmail, "signUpResult:success");

                                //Added Details in Database
                                try {
                                    DatabaseReference userData = usersRef.child(user.getUid());
                                    userData.child("name").setValue(mName);
                                    userData.child("email").setValue(mEmail);
                                    Log.i("firebaseUserDataUpload", "Success");
                                } catch (Exception e) {
                                    Log.w("firebaseUserDataUpload", "Failed", e);
                                }

                                //Sent verification mail
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                            Log.i(user.getEmail(), "Verification email sent successfully");
                                        } else {
                                            Log.i(user.getEmail(), "Verification email sent failed", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                //Return to Login Activity
                                finish();
                            } else {
                                Log.w("Account created(Email)", mEmail, task.getException());
                                Toast.makeText(SignUpActivity.this, "Account creation failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
