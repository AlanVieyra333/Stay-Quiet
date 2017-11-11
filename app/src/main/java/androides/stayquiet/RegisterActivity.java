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

                if ((phoneNumber.compareTo(phoneNumberConf) == 0 &&
                        password.compareTo(passwordConf) == 0)) {
                    User user = new User(name, phoneNumber, email, password, null);
                    long status;

                    if (!dbManager.existsAccount(user)) {
                        status = dbManager.insertUser(user);

                        if (status != -1) {
                            intentHome.putExtra("user", user);

                            startActivity(intentHome);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error al intentar registrar. " + status,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "El usuario ya existe.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "El telefono o la contaseña no coinciden.",
                            Toast.LENGTH_LONG).show();
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
}
