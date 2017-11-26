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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import androides.stayquiet.tools.Validator;

/**
 * Created by developer on 23/11/17.
 */

public class SecurityActivity extends AppCompatActivity {

    private EditText etPassword;
    private Button btnContinue;
    private ProgressBar progressBar;
    private Intent intentHome;
    private FirebaseAuth mAuth;
    private User user;
    private String name, email, phoneNumber, password, id;
    private String photoUri;
    private StayQuietDBManager dbManager;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        intentHome = new Intent(this, HomeActivity.class);

        etPassword = (EditText) findViewById(R.id.etPassword);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        progressBar = (ProgressBar) findViewById(R.id.pbSecurity);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(false);

        mAuth = FirebaseAuth.getInstance();
        storage =  FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("images");

        dbManager = new StayQuietDBManager(this, mAuth);

        getParams();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    if(photoUri != null) {
                        progressBar.setVisibility(View.VISIBLE);

                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        mAuth.signInWithEmailAndPassword(currentUser.getEmail(), password)
                                .addOnCompleteListener(SecurityActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            updatePhoto();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(SecurityActivity.this, R.string.MSJ1_9,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void getParams(){
        id = getIntent().getExtras().getString("id");
        photoUri = getIntent().getExtras().getString("photoUri");
        name = getIntent().getExtras().getString("name");
        email = getIntent().getExtras().getString("email");
        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        user = new User(name, phoneNumber, email, "", null, id);
    }

    private void getValues() {
        password = etPassword.getText().toString();
    }

    private boolean isValid() {
        if(!Validator.passwordIsValid(password)) {
            Toast.makeText(SecurityActivity.this, R.string.MSJ1_4,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void  updatePhoto() {
        try {
            StorageReference photoRef = imagesRef.child(id + ".jpg");
            InputStream stream = new FileInputStream(new File(photoUri));

            photoRef.putStream(stream)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SecurityActivity.this, R.string.MSJ1_6,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            if(downloadUrl != null) {
                                // Update profile
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(downloadUrl)
                                        .build();

                                mAuth.getCurrentUser().updateProfile(profile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mAuth.getCurrentUser().updateEmail(email)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Save into SQLite
                                                                dbManager.saveProfileIntoCache( progressBar, intentHome);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                // Handle unsuccessful uploads
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(SecurityActivity.this, R.string.MSJ1_6,
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle unsuccessful uploads
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(SecurityActivity.this, R.string.MSJ1_6,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }else {
                                Toast.makeText(SecurityActivity.this, R.string.MSJ1_6,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
            });
        } catch (Exception e){
            Toast.makeText(SecurityActivity.this, R.string.MSJ1_6,
                    Toast.LENGTH_LONG).show();
        }
    }
}