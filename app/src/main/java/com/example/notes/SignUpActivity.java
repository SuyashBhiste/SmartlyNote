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

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signUpBtn;
    private EditText emailTB, passTB, nameTB, confirmPassTB;
    private String email, password, name, confirmPassword;

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

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() != 0 && name.length() != 0) {
                    if (password.equals(confirmPassword)) {
                        signUp(email, password);
                    } else {
                        confirmPassTB.setError("Password is different");
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Field is not filled correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Account already created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
