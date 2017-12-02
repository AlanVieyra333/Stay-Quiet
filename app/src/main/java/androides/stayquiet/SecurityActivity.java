package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
    private Button btnContinue;
    private ProgressBar progressBar;
    private Intent intentHome;
    private User user;
    private String name, email, phoneNumber, password, id;
    private String photoUri;
    private StayQuietDBManager dbManager;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        intentHome = new Intent(this, HomeActivity.class);

        etPassword = (EditText) findViewById(R.id.etPassword);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(false);

        getParams();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    firebaseManager.login(FirebaseAuth.getInstance().getCurrentUser().getEmail(), password);
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
                        dbManager.saveProfileIntoCache(intentHome);
                    }
                });
                firebaseManager.updateProfile(user);
            }
        });
    }

    private void getParams(){
        id = getIntent().getExtras().getString("id");
        photoUri = getIntent().getExtras().getString("photoUri");
        name = getIntent().getExtras().getString("name");
        email = getIntent().getExtras().getString("email");
        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        user = new User(name, phoneNumber, email, "", null, id, photoUri);
    }

    private void getValues() {
        password = etPassword.getText().toString();
    }

    private boolean isValid() {
        if(!Validator.passwordIsValid(password)) {
            Tools.showMessage(this, R.string.MSJ1_4);
            return false;
        }

        return true;
    }
}