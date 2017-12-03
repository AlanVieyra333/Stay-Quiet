package androides.stayquiet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import androides.stayquiet.tools.Tools;
import androides.stayquiet.tools.Validator;

public class LoginActivity extends AppCompatActivity {
    private AppCompatActivity activity;
    private Button btnLogin, btnRegister, btnForgot;
    private EditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private String email, password;
    private StayQuietDBManager dbManager;
    private Intent intentHome;
    private Intent intentRegister;
    private FirebaseAuth mAuth;
    private FirebaseManager firebaseManager;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;
        intentHome = new Intent(this, HomeActivity.class);
        intentRegister = new Intent(this, RegisterActivity.class);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegister =(Button)findViewById(R.id.btnRegister);
        btnForgot =(Button)findViewById(R.id.btnForgot);
        tilEmail = findViewById(R.id.lyEmail);
        tilPassword = findViewById(R.id.lyPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    firebaseManager.login(email, password);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentRegister);
            }
        });

        dbManager = new StayQuietDBManager(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseManager = new FirebaseManager(this);
        firebaseManager.setCallback(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Tools.hideProgressbar(LoginActivity.this);
                if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() == null) {
                    Tools.showMessage(LoginActivity.this, R.string.MSJ1_6);
                    firebaseManager.logout();
                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null && currentUser.getPhoneNumber() != null) {
                    dbManager.saveProfileIntoCache(intentHome);
                }
            }
        };

        Tools.hideProgressbar(this);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void getValues() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    private boolean isValid() {
        boolean isValid = true;

        if (email.isEmpty()) {
            Tools.showTextError(this, tilEmail, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.emailIsValid(email)) {    // RN1,2;
            Tools.showTextError(this, tilEmail, R.string.MSJ1_10);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilEmail);
        }

        if (password.isEmpty()) {    // RN1,1
            Tools.showTextError(this, tilPassword, R.string.MSJ1_1);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilPassword);
        }

        return isValid;
    }
}
