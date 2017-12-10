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
import android.widget.CheckBox;
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
import java.util.concurrent.ExecutionException;

import androides.stayquiet.tools.Tools;
import androides.stayquiet.tools.Validator;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin, btnRegister, btnForgot;
    private EditText etUsername, etPassword;
    private TextInputLayout tilUsername, tilPassword;
    private CheckBox chbxRemember;
    private String username, password;
    private StayQuietDBManager dbManager;
    private Intent intentHome;
    private Intent intentRegister;
    private FirebaseManager firebaseManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intentHome = new Intent(getApplicationContext(), HomeActivity.class);
        intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);

        session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()) {
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intentHome);
            finish();
        }

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegister =(Button)findViewById(R.id.btnRegister);
        btnForgot =(Button)findViewById(R.id.btnForgot);
        tilUsername = findViewById(R.id.lyUsername);
        tilPassword = findViewById(R.id.lyPassword);
        chbxRemember = findViewById(R.id.chbxRemember);

        getParams();

        etUsername.setText(username);
        etPassword.setText(password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    firebaseManager.login(username, password);
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
        firebaseManager = new FirebaseManager(this);
        firebaseManager.setCallback(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Tools.hideProgressbar(LoginActivity.this);
                if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() == null) {
                    Tools.showMessage(LoginActivity.this, R.string.MSJ1_6);
                    firebaseManager.logout();
                } else {
                    dbManager.saveProfileIntoCache(firebaseManager.getUser(), intentHome, chbxRemember.isChecked());
                }
            }
        });

        Tools.hideProgressbar(this);
    }

    @Override
    protected  void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getValues() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
    }

    private boolean isValid() {
        boolean isValid = true;

        if (username.isEmpty()) {
            Tools.showTextError(this, tilUsername, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.usernameIsValid(username)) {    // RN1,2;
            Tools.showTextError(this, tilUsername, R.string.MSJ1_2);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilUsername);
        }

        if (password.isEmpty()) {    // RN1,1
            Tools.showTextError(this, tilPassword, R.string.MSJ1_1);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilPassword);
        }

        return isValid;
    }

    private void getParams() {
        try {
            username = getIntent().getExtras().getString("username");
            password = getIntent().getExtras().getString("password");

            if (username == null) {
                username = "";
                chbxRemember.setChecked(false);
            } else {
                chbxRemember.setChecked(true);
            }

            if (password == null) {
                password = "";
            }
        } catch(Exception e) {
            chbxRemember.setChecked(false);
        }
    }
}
