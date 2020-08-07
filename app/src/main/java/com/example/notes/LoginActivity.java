package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private EditText etEmail, etPassword;
    private SignInButton btGoogleLogIn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference rootRef;
    private DatabaseReference usersRef;
    private static int RC_SIGN_IN = 12354;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivLogo = findViewById(R.id.ivLogo);
        etEmail = findViewById(R.id.tbEmail);
        etPassword = findViewById(R.id.tbPassword);
        btGoogleLogIn = findViewById(R.id.btGoogleLogIn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        usersRef = rootRef.child("Users");

        ivLogo.getLayoutParams().height = 400;

        btGoogleLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logicGoogleSignIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (currentUser != null && currentUser.isEmailVerified()) {
            Log.i(currentUser.getEmail(), "Already Signed In: TRUE");
            Intent login2Main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(login2Main);
        } else if (account != null) {
            Log.i(account.getEmail(), "Already Signed In: TRUE");
            Intent login2Main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(login2Main);
        }
    }

    public void logicLogIn(View view) {
        final String mEmail = etEmail.getText().toString();
        final String mPassword = etPassword.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            if (mPassword.length() >= 8) {
                mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.i(mEmail, "signInWithEmail:success");
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.isEmailVerified()) {
                                        Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(toMainActivity);
                                        finish();
                                    } else {
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i(user.getEmail(), "Verification mail sent successfully.");
                                                } else {
                                                    Log.w(user.getEmail(), "Verification mail sending failed.");
                                                }
                                            }
                                        });
                                        Toast.makeText(LoginActivity.this, "Email ID is not verified. Check your mail", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.w(mEmail, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                etPassword.setError("Password must be atleast 8 characters");
            }
        } else {
            etEmail.setError("Invalid email");
        }
    }

    public void logicForgetPassword(View view) {
        final String mEmail = etEmail.getText().toString();

        if (Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to mail.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email ID is not registered.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            etEmail.setError("Fill email to reset password");
        }
    }

    public void logicGoogleSignIn() {
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
            final String mName = account.getDisplayName();
            final String mEmail = account.getEmail();
            final String idToken = account.getIdToken();

            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("User data added", mEmail);
                        user = mAuth.getCurrentUser();
                        Log.i("Hi", "i");
                        //Added Details in Database
                        try {
                            Log.i("Hi1", "1");
                            DatabaseReference userData = usersRef.child(user.getUid());
                            Log.i("Hi2", "2");
                            userData.child("name").setValue(mName);
                            Log.i("Hi3", "3");
                            userData.child("email").setValue(mEmail);
                            Log.i("firebaseUserDataUpload", "Success");
                        } catch (Exception e) {
                            Log.w("firebaseUserDataUpload", "Failed", e);
                        }
                    }
                }
            });

            Log.i("Account created(Google)", mEmail);
            Log.i(mEmail, "signInResult:success");

            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMainActivity);
            finish();
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            Log.w("Google Login", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public void logicSignUp(View view) {
        Intent toSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(toSignUp);
    }

}
