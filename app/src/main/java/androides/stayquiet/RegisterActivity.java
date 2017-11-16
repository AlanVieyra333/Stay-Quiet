package androides.stayquiet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

/**
 * Created by gerardo on 17/09/17.
 */

public class RegisterActivity extends AppCompatActivity {

    private Button btnSave;
    private EditText etFirstName, etLastName, etPhoneNumber, etEmail, etPassword, etPhoneNumberConf, etPasswordConf;
    private String name, phoneNumber, email, password, phoneNumberConf, passwordConf;
    private StayQuietDBManager dbManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser userDB;
    private AppCompatActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Intent intentHome = new Intent(RegisterActivity.this, HomeActivity.class);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    Toast.makeText(getApplicationContext(), "Usuario ha sido registrado",
                            Toast.LENGTH_SHORT).show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+52" + phoneNumber,        // Phone number to verify
                            1,                  // Timeout duration
                            TimeUnit.MINUTES,   // Unit of timeout
                            activity,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        dbManager = new StayQuietDBManager(this, mAuth);
        etFirstName = (EditText) findViewById(R.id.etRegister_FirstName);
        etPhoneNumber = (EditText) findViewById(R.id.etRegister_phoneNumber);
        etPhoneNumberConf = (EditText) findViewById(R.id.etRegister_phoneNumberConf);
        etEmail = (EditText) findViewById(R.id.etRegister_email);
        etPassword = (EditText) findViewById(R.id.etRegister_password);
        etPasswordConf = (EditText) findViewById(R.id.etRegister_passwordConf);
        btnSave = (Button) findViewById(R.id.btnRegister_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if ( validateForm()) {
                    User user = new User(name, phoneNumber, email, password, null);
                    //long status;

                    //if (!dbManager.existsAccount(user)) {
                        dbManager.signUp(user);
                        //status = dbManager.insertUser(user);

                        /*if (status != -1) {
                            intentHome.putExtra("user", user);

                            startActivity(intentHome);
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR. No se puede conectar a la base de datos",
                                    Toast.LENGTH_LONG).show();
                        }*/
                    //} else {
                    //    Toast.makeText(getApplicationContext(), "ERROR. Correo y/o teléfono ya han sido registrados anteriormente.",
                    //            Toast.LENGTH_LONG).show();
                    //}
                }

                etPassword.setText("");
                etPasswordConf.setText("");
            }
        });
    }

    private void getValues() {
        name = etFirstName.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
        phoneNumberConf = etPhoneNumberConf.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        passwordConf = etPasswordConf.getText().toString();
    }

    private  boolean validateForm() {
        if (name.compareTo("") == 0 || phoneNumber.compareTo("") == 0 || phoneNumberConf.compareTo("") == 0
                || email.compareTo("") == 0 || password.compareTo("") == 0 || passwordConf.compareTo("") == 0) {
            Toast.makeText(getApplicationContext(), "ERROR. Campos incompletos.",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (!(validName(name))) {
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
        String regex ="(/^(?=.*[a-z]).+$/)(/^(?=.*[A-Z]).+$/)(/^(?=.*[0-9_\\W]).+$/)";
       // boolean valid = password.matches(regex);
        return true;
    }

    public boolean validEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
            Toast.makeText(getApplicationContext(), "Codigo Verificado",
                    Toast.LENGTH_LONG).show();

            /*mAuth.signInWithCredential(credential)
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.MSJ1_31,
                                    Toast.LENGTH_LONG).show();
                        }
                    });

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(StayQuietDBHelper.STORAGE_URL + "images/add_camera.png"))
                    .build();
            userDB.updatePhoneNumber(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User email address updated.");
                            }
                        }
                    });

            userDB.updateProfile(profile);

            // User is signed in
            // Name, email address, and profile photo Url
            String name = userDB.getDisplayName();
            String phoneNumber = userDB.getPhoneNumber();
            Uri photoUrl = userDB.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            // String uid = user.getUid();

            //User myUser = new User(name, phoneNumber, email, "", photoUrl.toString());
            User myUser = new User();

            intentHome.putExtra("user", myUser);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHome);*/
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), R.string.MSJ1_32 + " " + e,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            //Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            //mVerificationId = verificationId;
            //mResendToken = token;
            Toast.makeText(getApplicationContext(), "Codigo enviado: " + verificationId + " : " + token,
                    Toast.LENGTH_LONG).show();
        }
    };
}
