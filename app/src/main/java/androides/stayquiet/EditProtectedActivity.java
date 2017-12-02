package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androides.stayquiet.tools.Tools;
import androides.stayquiet.tools.Validator;

public class EditProtectedActivity extends AppCompatActivity {
    private String name, phoneNumber, email, password, passwordConf;
    private String id, idProtected;
    private ImageView ivPhoto;
    private EditText etName, etEmail, etPhoneNumber, etPassword, etPasswordConf;
    private Button btnSave;
    private Intent intentSecurity;
    private User user;
    private StayQuietDBManager dbManager;
    private Uri photoUri = null;
    private final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_protected);

        ivPhoto = (ImageView) findViewById(R.id.imageProfile);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConf = (EditText) findViewById(R.id.etPasswordConf);
        btnSave = (Button)findViewById(R.id.btnSave);

        intentSecurity = new Intent(this, SecurityActivity.class);

        dbManager = new StayQuietDBManager(this);

        getParams();

        etName.setText(name);
        etEmail.setText(email);
        etPhoneNumber.setText(phoneNumber);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValues();

                if(isValid()){
                    intentSecurity.putExtra("id", id);
                    intentSecurity.putExtra("idProtected", idProtected);
                    intentSecurity.putExtra("photoUri", Tools.getPathFromURI(EditProtectedActivity.this, photoUri));
                    intentSecurity.putExtra("name", name);
                    intentSecurity.putExtra("email", email);
                    intentSecurity.putExtra("phoneNumber", phoneNumber);

                    startActivity(intentSecurity);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (reqCode == GALLERY_REQUEST) {
                try {
                    photoUri = data.getData();
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    ivPhoto.setImageBitmap(photo);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.MSJ1_6, Toast.LENGTH_LONG).show();
                }
            }
        }else {
            Toast.makeText(this, R.string.MSJ1_6,Toast.LENGTH_LONG).show();
        }
    }

    private void getParams(){
        id = getIntent().getExtras().getString("id");
        idProtected = getIntent().getExtras().getString("idProtected");
        user = dbManager.getUser(idProtected);

        name = user.getName();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        phoneNumber = phoneNumber.substring(3, phoneNumber.length());

        if(user.getPhoto() != null) {
            Bitmap photo = Tools.bytesToBitmap(user.getPhoto());
            ivPhoto.setImageBitmap(photo);
        } else{
            Toast.makeText(getApplicationContext(), R.string.MSJ1_6,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void getValues() {
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
        password = etPassword.getText().toString();
        passwordConf = etPasswordConf.getText().toString();
    }

    private boolean isValid() {
        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || passwordConf.isEmpty()) {    // RN1,1
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
        } else if(password.equals(passwordConf)) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_5,
                    Toast.LENGTH_LONG).show();
            return false;
        } else if(!Validator.passwordIsValid(password)) {    // RN1,2
            Toast.makeText(getApplicationContext(), R.string.MSJ1_4,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
