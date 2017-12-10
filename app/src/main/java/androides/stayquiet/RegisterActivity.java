package androides.stayquiet;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import androides.stayquiet.tools.Tools;

/**
 * Created by gerardo on 17/09/17.
 */

public class RegisterActivity extends AppCompatActivity {

    private Button btnSave;
    private EditText etUsername, etName, etLastName, etPhoneNumber, etEmail, etPassword, etPhoneNumberConf, etPasswordConf;
    private String username, name, phoneNumber, email, password, phoneNumberConf, passwordConf;
    private StayQuietDBManager dbManager;
    private Intent intentHome, intentVerifyPhone;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseManager firebaseManager;
    private ProgressBar progressBar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intentHome = new Intent(this, HomeActivity.class);
        intentVerifyPhone = new Intent(this, VerifyPhoneActivity.class);

        etUsername = findViewById(R.id.etUserName);
        etName = (EditText) findViewById(R.id.etName);
        etPhoneNumber = (EditText) findViewById(R.id.etRegister_phoneNumber);
        etEmail = (EditText) findViewById(R.id.etRegister_email);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSave = (Button) findViewById(R.id.btnRegister_save);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if ( validateForm()) {
                    user = new User(username, name, phoneNumber, email, password);

                    firebaseManager.signUp(user);
                }


            }
        });

        firebaseManager = new FirebaseManager(this);
        dbManager = new StayQuietDBManager(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null && currentUser.getPhoneNumber() != null) {
                    dbManager.saveProfileIntoCache(user, intentHome, false);
                }
            }
        };
    }

    @Override
    protected  void onStart() {
        super.onStart();
        FirebaseAuth.getInstance()
                .addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance()
                    .removeAuthStateListener(mAuthListener);
        }
    }

    private void getValues() {
        username = etUsername.getText().toString();
        name = etName.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    private  boolean validateForm() {

        if (!(validName(name))) {
            Toast.makeText(getApplicationContext(), "ERROR. Solo se permiten letras",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (!(validateNumber(phoneNumber))) {
            Toast.makeText(getApplicationContext(), "ERROR. Solo se permiten Numeros",
                    Toast.LENGTH_LONG).show();
            return false;
        }else if( phoneNumber.length() != 10){
            Toast.makeText(getApplicationContext(), "ERROR. El telefono debe tener 10 digitos ",
                    Toast.LENGTH_LONG).show();
            return false;
        }else if(!(validPassword(password)) || !(password.length() >8 || password.length()< 16 ) ){
            Toast.makeText(getApplicationContext(), "ERROR. La contraseña es débil.",
                    Toast.LENGTH_LONG).show();
            return  false;
        }else if(!(validEmail(email))){
            Toast.makeText(getApplicationContext(), "ERROR.El formato de correo incorrecto, Solo: example@mail.com.",
                    Toast.LENGTH_LONG).show();
            return  false;
        }else{
            return true;
        }


    }

    private boolean validName(String name){
        String regex =   "^[a-zA-Z\\s]+" ;
        boolean valid=name.matches(regex);
        return  valid;
    }

    private boolean validateNumber(String number){
        String regex = "[0-9]+" ;

        return  number.matches(regex);
    }

    private boolean validPassword(String password){
        String regex ="^.*(?!.*\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\=\\¡\\£\\_\\+\\`\\~\\.\\,\\<\\>\\/\\?\\;\\:\\'\\\"\\\\\\|\\[\\]\\{\\}]).*$";
       boolean valid = password.matches(regex);
        return valid;
    }

    public boolean validEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }
}
