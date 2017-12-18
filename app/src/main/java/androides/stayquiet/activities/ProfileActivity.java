package androides.stayquiet.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androides.stayquiet.R;
import androides.stayquiet.database.SessionManager;
import androides.stayquiet.database.StayQuietDBManager;
import androides.stayquiet.user.User;
import androides.stayquiet.tools.Tools;
import androides.stayquiet.tools.Validator;

public class ProfileActivity extends AppCompatActivity {
    private String username, name, phoneNumber, email, id;
    private EditText etUsername, etName, etEmail, etPhoneNumber;
    private TextInputLayout tilUsername, tilName, tilEmail, tilPhoneNumber;
    private Button btnSave;
    private Intent intentSecurity;
    private ImageView ivPhoto;
    private User user;
    private Uri photoUri = null;
    private StayQuietDBManager dbManager;
    private final int GALLERY_REQUEST = 1;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Session data.
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();

        ivPhoto = (ImageView) findViewById(R.id.imageProfile);
        etUsername = findViewById(R.id.etUsername);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnSave = (Button)findViewById(R.id.btnSave);
        tilUsername = findViewById(R.id.lyUsername);
        tilName = findViewById(R.id.lyName);
        tilEmail = findViewById(R.id.lyEmail);
        tilPhoneNumber = findViewById(R.id.lyPhoneNumber);

        intentSecurity = new Intent(this, SecurityActivity.class);

        dbManager = new StayQuietDBManager(this);

        getParams();

        etUsername.setText(username);
        etName.setText(name);
        etEmail.setText(email);
        etPhoneNumber.setText(phoneNumber);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValues();

                if(isValid()){
                    intentSecurity.putExtra("name", name);
                    intentSecurity.putExtra("email", email);
                    intentSecurity.putExtra("phoneNumber", phoneNumber);
                    intentSecurity.putExtra("photoUri", Tools.getPathFromURI(ProfileActivity.this, photoUri));

                    startActivity(intentSecurity);
                }
            }
        });

        Tools.hideProgressbar(this);
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
                    Tools.showMessage(this, R.string.MSJ1_6);
                }
            }
        }
    }

    private void getParams(){
        user = dbManager.getUser(user.getId());

        username = user.getUsername();
        name = user.getName();
        phoneNumber = user.getPhoneNumber();
        email = user.getEmail();

        if(user.getPhoto() != null) {
            Bitmap photo = Tools.bytesToBitmap(user.getPhoto());
            ivPhoto.setImageBitmap(photo);
        } else{
            Tools.showMessage(this, R.string.MSJ1_6);
        }
    }

    private void getValues() {
        username = etUsername.getText().toString();
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
    }

    private boolean isValid() {
        boolean isValid = true;

        // Username.
        if (username.isEmpty()) {
            Tools.showTextError(this, tilUsername, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.usernameIsValid(username)) {    // RN1,2;
            Tools.showTextError(this, tilUsername, R.string.MSJ1_2);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilUsername);
        }

        // Name.
        if (name.isEmpty()) {
            Tools.showTextError(this, tilName, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.nameIsValid(name)) {    // RN1,2;
            Tools.showTextError(this, tilName, R.string.MSJ1_2);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilName);
        }

        // Email.
        if (email.isEmpty()) {
            Tools.showTextError(this, tilEmail, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.emailIsValid(email)) {    // RN1,2;
            Tools.showTextError(this, tilEmail, R.string.MSJ1_10);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilEmail);
        }

        // Phone number.
        if (phoneNumber.isEmpty()) {    // RN1,1
            Tools.showTextError(this, tilPhoneNumber, R.string.MSJ1_1);
            isValid = false;
        } else if(!Validator.phoneNumberIsValid(phoneNumber)) {    // RN1,2;
            Tools.showTextError(this, tilPhoneNumber, R.string.MSJ1_3);
            isValid = false;
        } else {
            Tools.hideTextError(this, tilPhoneNumber);
        }

        // Same data.
        if (name.equals(session.getUser().getName()) &&
                email.equals(session.getUser().getEmail()) &&
                phoneNumber.equals(session.getUser().getPhoneNumber()) &&
                photoUri == null) {
            Tools.showMessage(this, R.string.MSJ1_35);
            isValid = false;
        }

        return isValid;
    }
}
