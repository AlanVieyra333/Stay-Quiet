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
    private EditText etFirstName, etLastName, etPhoneNumber, etEmail, etPassword, etPhoneNumberConf, etPasswordConf;
    private String name, phoneNumber, email, password, phoneNumberConf, passwordConf;
    private StayQuietDBManager dbManager;
    private Intent intentHome, intentVerifyPhone;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser userDB;
    private AppCompatActivity activity = this;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intentHome = new Intent(this, HomeActivity.class);
        intentVerifyPhone = new Intent(this, VerifyPhoneActivity.class);

        etFirstName = (EditText) findViewById(R.id.etRegister_FirstName);
        etPhoneNumber = (EditText) findViewById(R.id.etRegister_phoneNumber);
        etPhoneNumberConf = (EditText) findViewById(R.id.etRegister_phoneNumberConf);
        etEmail = (EditText) findViewById(R.id.etRegister_email);
        etPassword = (EditText) findViewById(R.id.etRegister_password);
        etPasswordConf = (EditText) findViewById(R.id.etRegister_passwordConf);
        btnSave = (Button) findViewById(R.id.btnRegister_save);
        progressBar = (ProgressBar) findViewById(R.id.pbRegister);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if ( validateForm()) {
                    User user = new User(name, phoneNumber, email, password, null);
                    dbManager.signUp(user, mCallbacks);
                }


            }
        });

        mAuth = FirebaseAuth.getInstance();
        dbManager = new StayQuietDBManager(this, mAuth);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null && currentUser.getPhoneNumber() != null) {
                    dbManager.saveProfileIntoCache(progressBar, intentHome);
                }
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
        name = etFirstName.getText().toString();
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
        }else if((phoneNumber.compareTo(phoneNumberConf) != 0)){
            Toast.makeText(getApplicationContext(), "ERROR. No coincide el Numero.",
                    Toast.LENGTH_LONG).show();
            return  false;
        }else if( password.compareTo(passwordConf) != 0){
            Toast.makeText(getApplicationContext(), "ERROR. No coincide la contraseña.",
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

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
            associate(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_32,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            //mResendToken = token;
            intentVerifyPhone.putExtra("verificationId", verificationId);

            startActivity(intentVerifyPhone);
        }
    };

    private void associate(PhoneAuthCredential credential) {
        // Update data profile.
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        currentUser.updatePhoneNumber(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dbManager.saveProfileIntoCache(progressBar, intentHome);
                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_31,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
