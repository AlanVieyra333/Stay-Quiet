package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
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
    private String name, phoneNumber, email, id;
    private EditText etName, etEmail, etPhoneNumber;
    private Button btnSave;
    private Intent intentSecurity;
    private ImageView ivPhoto;
    private User user;
    private Uri photoUri = null;
    private StayQuietDBManager dbManager;
    private final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivPhoto = (ImageView) findViewById(R.id.imageProfile);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etPhoneNumber.setEnabled(false);
        btnSave = (Button)findViewById(R.id.btnSave);

        intentSecurity = new Intent(this, SecurityActivity.class);

        dbManager = new StayQuietDBManager(this, null);

        getParams();

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
                    intentSecurity.putExtra("id", id);
                    intentSecurity.putExtra("photoUri", Tools.getPathFromURI(ProfileActivity.this, photoUri));
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
        user = dbManager.getUser(id);

        name = user.getName();
        phoneNumber = user.getPhoneNumber();
        phoneNumber = phoneNumber.substring(3, phoneNumber.length());
        email = user.getEmail();

        if(user.getPhoto() != null) {
            Bitmap photo = BitmapFactory.decodeByteArray(user.getPhoto(), 0, user.getPhoto().length);
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
