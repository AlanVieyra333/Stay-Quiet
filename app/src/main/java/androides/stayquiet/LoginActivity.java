package androides.stayquiet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    EditText etEmail, etPassword;
    String email, password;
    StayQuietDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Intent intentLogin = new Intent(LoginActivity.this, HomeActivity.class);
        final Intent intentRegister = new Intent(LoginActivity.this,RegisterActivity.class);
        dbManager = new StayQuietDBManager(getApplicationContext());
        etEmail = (EditText) findViewById(R.id.etLogin_email);
        etPassword = (EditText) findViewById(R.id.etLogin_password);
        btnLogin = (Button)findViewById(R.id.btnLogin_login);
        btnRegister =(Button)findViewById(R.id.btnLogin_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    Account account = new User(email, password);
                    User user = dbManager.getUser(account);

                    // RN1.3
                    if ( user != null ) {
                        intentLogin.putExtra("user", user);

                        startActivity(intentLogin);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.MSJ1_7,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentRegister);
            }
        });
    }

    private void getValues() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    private boolean isValid() {
        if (email.isEmpty() || password.isEmpty()) {    // RN1,1
            Toast.makeText(getApplicationContext(), R.string.MSJ1_1,
                    Toast.LENGTH_LONG).show();
            return false;
        }else if(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_10,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
