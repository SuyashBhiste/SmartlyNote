package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText emailTB, passTB, nameTB, confirmPassTB;
    private String email, password, name, confirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference rootref = db.getReference();
    DatabaseReference usersref = rootref.child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        emailTB = findViewById(R.id.signupEmailTB);
        passTB = findViewById(R.id.signupPassTB);
        nameTB = findViewById(R.id.nameTB);
        confirmPassTB = findViewById(R.id.cpassTB);
        signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailTB.getText().toString();
                password = passTB.getText().toString();
                name = nameTB.getText().toString();
                confirmPassword = confirmPassTB.getText().toString();

                if (name.length() != 0) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (password.length() >= 8) {
                            if (password.equals(confirmPassword)) {
                                signUp(email, password, name);
                                try {
                                    String userID = usersref.push().getKey();
                                    DatabaseReference userData = usersref.child(userID);
                                    userData.child("name").setValue(name);
                                    userData.child("email").setValue(email);
                                } catch (Exception e) {
                                    Log.w("TAG", "Firebase User Data Upload: FALIED");
                                    System.out.println(e);
                                }
                            } else {
                                confirmPassTB.setError("Password didn't match");
                            }
                        }else{
                            passTB.setError("Password must be atleast 8 characters");
                        }
                    }else{
                        emailTB.setError("Invalid email");
                    }
                }else{
                    nameTB.setError("Field is empty");
                }
            }
        });
    }

    private void signUp(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            Intent signup2Login = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(signup2Login);
                            finish();
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Account already created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
