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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView forgetPassTV;
    private EditText emailTB, passTB;
    private Button logInBtn, signUpBtn;
    private SignInButton googleLoginbtn;

    private FirebaseAuth mAuth;
    private static int RC_SIGN_IN = 12354;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        forgetPassTV = findViewById(R.id.forgetPassTV);
        emailTB = findViewById(R.id.emailTB);
        passTB = findViewById(R.id.passTB);
        logInBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signupBtn);
        googleLoginbtn = findViewById(R.id.googleLoginBtn);

        imageView.getLayoutParams().height = 400;

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailTB.getText().toString();
                final String password = passTB.getText().toString();

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (password.length() >= 8) {
                        loginFunc(email, password);
                    } else {
                        passTB.setError("Password must be atleast 8 characters");
                    }
                }else{
                    emailTB.setError("Invalid email");
                }
            }
        });

        googleLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLoginFunc();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(toSignUp);
                finish();
            }
        });

        forgetPassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailTB.getText().toString();

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Reset link sent to mail", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email not registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    emailTB.setError("Fill email to reset password");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (currentUser!=null || account!=null) {
            System.out.println("User Signed IN already");
            Log.i("TAG", "Already Signed In: TRUE");
//            updateUI(currentUser);
        }
    }

    public void loginFunc(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void googleLoginFunc() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i("TAG", "signInResult:success");
//            updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }

}
