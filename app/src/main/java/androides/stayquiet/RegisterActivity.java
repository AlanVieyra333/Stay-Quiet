package androides.stayquiet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by gerardo on 17/09/17.
 */

public class RegisterActivity extends AppCompatActivity {

    private Button btnSave;
    private EditText etFirstName, etLastName, etPhoneNumber, etEmail, etPassword, etPhoneNumberConf, etPasswordConf;
    private String name, phoneNumber, email, password, phoneNumberConf, passwordConf;
    private StayQuietDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Intent intentHome = new Intent(RegisterActivity.this, HomeActivity.class);
        dbManager = new StayQuietDBManager(getApplicationContext());
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
                    User user = new User(name, phoneNumber, email, password, null);
                    long status;

                    if (!dbManager.existsAccount(user)) {
                        status = dbManager.insertUser(user);

                        if (status != -1) {
                            intentHome.putExtra("user", user);

                            startActivity(intentHome);
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR. No se puede conectar a la base de datos",
                                    Toast.LENGTH_LONG).show();
                        }
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
        } else if (!(validateNumber(phoneNumber)) || phoneNumber.length() != 10) {
            Toast.makeText(getApplicationContext(), "ERROR. Solo se permiten letras",
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
        }else if((phoneNumber.compareTo(phoneNumberConf) == 0)){
            Toast.makeText(getApplicationContext(), "ERROR. No coincide el Numero..",
                    Toast.LENGTH_LONG).show();
            return  false;
        }else if( password.compareTo(passwordConf) == 0){
            Toast.makeText(getApplicationContext(), "ERROR. No coincide la contraseña.",
                    Toast.LENGTH_LONG).show();
            return  false;
        }else{
            return true;
        }


    }

    private boolean validName(String name){

        String regex = "\\p{Alpha}" ;

        return  name.matches(regex);
    }

    private boolean validateNumber(String number){
        String regex = "\\D" ;

        return  number.matches(regex);
    }


    private boolean validPassword(String password){
        String regex ="(\\p{Upper})+ (\\p{Lower})+ (\\D)+ (\\p{Punct})+";
        return password.matches(regex);

    }

    public boolean validEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }
}
