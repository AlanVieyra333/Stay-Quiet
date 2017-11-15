package androides.stayquiet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    //private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Intent intentHome = new Intent(RegisterActivity.this, HomeActivity.class);
        mAuth = FirebaseAuth.getInstance();
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    // User is signed in
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String phoneNumber = user.getPhoneNumber();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    // String uid = user.getUid();

                    //User myUser = new User(name, phoneNumber, email, "", photoUrl.toString());
                    User myUser = new User();

                    intentHome.putExtra("user", myUser);
                    intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentHome);
                }
            }
        };
        dbManager = new StayQuietDBManager(this, mAuth);
        etFirstName = (EditText) findViewById(R.id.etRegister_FirstName);
        etLastName = (EditText) findViewById(R.id.etRegister_LastName);
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
                    User user = new User(name, phoneNumber, email, password, "");
                    long status;

                    if (!dbManager.existsAccount(user)) {
                        dbManager.signUp(user);
                        //status = dbManager.insertUser(user);

                        /*if (status != -1) {
                            intentHome.putExtra("user", user);

                            startActivity(intentHome);
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR. No se puede conectar a la base de datos",
                                    Toast.LENGTH_LONG).show();
                        }*/
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR. Correo y/o teléfono ya han sido registrados anteriormente.",
                                Toast.LENGTH_LONG).show();
                    }
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
}
