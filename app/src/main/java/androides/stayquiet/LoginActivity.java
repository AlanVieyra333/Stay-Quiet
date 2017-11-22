package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private EditText etEmail, etPassword;
    private String email, password;
    private StayQuietDBManager dbManager;
    private Intent intentHome;
    private Intent intentRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intentHome = new Intent(this, HomeActivity.class);
        intentRegister = new Intent(this, RegisterActivity.class);

        etEmail = (EditText) findViewById(R.id.etLogin_email);
        etPassword = (EditText) findViewById(R.id.etLogin_password);
        btnLogin = (Button)findViewById(R.id.btnLogin_login);
        btnRegister =(Button)findViewById(R.id.btnLogin_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    dbManager.login(email, password);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentRegister);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        dbManager = new StayQuietDBManager(this, mAuth);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                changeActivity(user);
            }
        };
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
        if (email.isEmpty() || password.isEmpty()) {    // RN1,1
            Toast.makeText(getApplicationContext(), R.string.MSJ1_1,
                    Toast.LENGTH_LONG).show();
            return false;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_10,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void changeActivity(FirebaseUser currentUser) {
        if (currentUser != null && currentUser.getPhoneNumber() != null) {
            User user = StayQuietDBManager.FirebaseUserToUser(currentUser);

            Toast.makeText(getApplicationContext(), "Change: " + user,
                    Toast.LENGTH_LONG).show();

            intentHome.putExtra("user", user);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHome);
            finish();
        }
    }
}
