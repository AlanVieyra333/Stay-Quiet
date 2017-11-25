package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import androides.stayquiet.tools.Tools;
import androides.stayquiet.tools.Validator;

public class ProfileActivity extends AppCompatActivity {
    private String name, phoneNumber, email;
    private Bitmap photo;
    private EditText etName, etEmail, etPhoneNumber;
    private Button btnSave;
    private Intent intentSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView add = (ImageView) findViewById(R.id.imageProfile);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnSave = (Button)findViewById(R.id.btnSave);

        intentSecurity = new Intent(this, SecurityActivity.class);

        getParams();

        etName.setText(name);
        etEmail.setText(email);
        etPhoneNumber.setText(phoneNumber);

        //add.setImageBitmap(photo);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Cambiar foto",
                        Toast.LENGTH_LONG).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValues();

                if(isValid()){
                    User user = new User(name, phoneNumber, email, "", Tools.bitmapToBytes(photo));

                    intentSecurity.putExtra("user", user);
                    startActivity(intentSecurity);
                }
            }
        });
    }

    private void getParams(){
        User user = (User) getIntent().getExtras().getSerializable("user");

        name = user.getName();
        phoneNumber = user.getPhoneNumber();
        email = user.getEmail();
        if(user.getPhoto() != null) {
            //photo = BitmapFactory.decodeByteArray(user.getPhoto(), 0, user.getPhoto().length);
        }
    }

    private void getValues() {
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
    }

    private boolean isValid() {
        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {    // RN1,1
            Toast.makeText(getApplicationContext(), R.string.MSJ1_1,
                    Toast.LENGTH_LONG).show();
            return false;
        } else if(!Validator.nameIsValid(name)) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_2,
                    Toast.LENGTH_LONG).show();
            return false;
        } else if(!Validator.phoneNumberIsValid(phoneNumber)) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_3,
                    Toast.LENGTH_LONG).show();
            return false;
        } else if(!Validator.emailIsValid(email)) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_10,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
