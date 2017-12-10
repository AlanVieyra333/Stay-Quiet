package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androides.stayquiet.tools.Tools;
import androides.stayquiet.tools.Validator;

/**
 * Created by developer on 23/11/17.
 */

public class SecurityActivity extends AppCompatActivity {

    private EditText etPassword;
    private TextInputLayout tilPassword;
    private Button btnContinue;
    private Intent intentHome;
    private User user;
    private String password;
    private StayQuietDBManager dbManager;
    private FirebaseManager firebaseManager;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        // Session data.
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        intentHome = new Intent(this, HomeActivity.class);

        etPassword = (EditText) findViewById(R.id.etPassword);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        tilPassword = findViewById(R.id.lyPassword);

        getParams();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    firebaseManager.login(user.getUsername(), password);
                }
            }
        });

        dbManager = new StayQuietDBManager(this);
        firebaseManager = new FirebaseManager(this);
        firebaseManager.setCallback(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseManager.setCallback(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dbManager.saveProfileIntoCache(firebaseManager.getUser(), intentHome, session.getReminder());
                    }
                });
                firebaseManager.updateProfile(user);
            }
        });

        Tools.hideProgressbar(this);
    }

    private void getParams(){
        user = session.getUser();

        user.setName(getIntent().getExtras().getString("name"));
        user.setPhoneNumber(getIntent().getExtras().getString("phoneNumber"));
        user.setEmail(getIntent().getExtras().getString("email"));
        user.setPhotoUrl(getIntent().getExtras().getString("photoUri"));
    }

    private void getValues() {
        password = etPassword.getText().toString();
    }

    private boolean isValid() {
        boolean isValid = true;

        if (password.isEmpty()) {
            Tools.showTextError(this, tilPassword, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.passwordIsValid(password)) {
            Tools.showTextError(this, tilPassword, R.string.MSJ1_4);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilPassword);

            if (!password.equals(session.getUser().getPassword())) {
                Tools.showMessage(this, R.string.MSJ1_7);
                isValid = false;
            }
        }

        return isValid;
    }
}