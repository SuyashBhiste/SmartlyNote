package com.smartlynote.notes;

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

    //XML Attributes
    private ImageView ivLogo;
    private EditText etEmail, etPassword;
    private SignInButton btGoogleLogIn;

    //Firebase Declarations
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference rootRef;
    private DatabaseReference usersRef;
    private DatabaseReference uidRef;

    private static int RC_SIGN_IN = 12354;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Assign Id's
        ivLogo = findViewById(R.id.ivLogo);
        etEmail = findViewById(R.id.tbEmail);
        etPassword = findViewById(R.id.tbPassword);
        btGoogleLogIn = findViewById(R.id.btGoogleLogIn);

        ivLogo.getLayoutParams().height = 400;

        //Initializing Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        usersRef = rootRef.child("Users");

        //Google SignIn Button Call
        btGoogleLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //Already SignIn Check
        if (user != null && user.isEmailVerified()) {
            quickIntent(MainActivity.class);
            finish();
            Log.i("User already signIn", user.getEmail());
        }
    }

    public void logIn(View view) {
        //Get account details
        final String mEmail = etEmail.getText().toString();
        final String mPassword = etPassword.getText().toString();

        boolean isEmailPatternCheck = Patterns.EMAIL_ADDRESS.matcher(mEmail).matches();
        boolean isPasswordCheck = mPassword.length() >= 8;

        //Login User Authentication
        if (isEmailPatternCheck) {
            if (isPasswordCheck) {
                auth.signInWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    emailVerification(mEmail);
                                } else {
                                    Log.w("SignIn with email fail", mEmail, task.getException());
                                    quickToast("Invalid email/password or not registered");
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

    public void emailVerification(String mEmail) {
        user = auth.getCurrentUser();

        if (user.isEmailVerified()) {
            quickIntent(MainActivity.class);
            Log.i("SignIn with Email", mEmail);
            finish();

        } else {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i("Verification mail sent", user.getEmail());
                    } else {
                        Log.w("Verification mail fail", user.getEmail(), task.getException());
                    }
                }
            });

            quickToast("Email ID is not verified. Check your mail");
        }
    }

    public void forgetPassword(View view) {
        final String mEmail = etEmail.getText().toString();
        boolean isEmailPatternCheck = Patterns.EMAIL_ADDRESS.matcher(mEmail).matches();

        if (isEmailPatternCheck) {
            auth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        quickToast("Reset link sent to mail.");
                        Log.i("Reset link sent", mEmail);
                    } else {
                        quickToast("Email ID is not registered.");
                        Log.w("Reset link sending fail", mEmail, task.getException());
                    }
                }
            });
        } else {
            etEmail.setError("Fill email to reset password");
        }
    }

    public void googleSignIn() {
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

            //Get account details
            final String mName = account.getDisplayName();
            final String mEmail = account.getEmail();
            final String idToken = account.getIdToken();

            //Creating Google user in Firebase
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("SignIn with Google", mEmail);

                        //Pushing details to Firebase database
                        user = auth.getCurrentUser();
                        try {
                            uidRef = usersRef.child(user.getUid());
                            uidRef.child("name").setValue(mName);
                            uidRef.child("email").setValue(mEmail);
                            quickIntent(MainActivity.class);
                            Log.i("Data upload", mEmail);
                            finish();

                        } catch (Exception e) {
                            Log.w("Data upload failed", mEmail, e);
                        }
                    }
                }
            });
        } catch (ApiException e) {
            Log.w("SignIn with Google fail", e);
        }
    }

    public void signUp(View view) {
        quickIntent(SignUpActivity.class);
    }

    public void quickIntent(Class destination) {
        Intent changeIntent = new Intent(LoginActivity.this, destination);
        startActivity(changeIntent);
    }

    public void quickToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
