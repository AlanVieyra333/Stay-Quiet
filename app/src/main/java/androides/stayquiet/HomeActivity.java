package androides.stayquiet;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import androides.stayquiet.tools.Tools;

public class HomeActivity extends AppCompatActivity {

    private String name, phoneNumber, email, id;
    private TextView tvNameMine, tvEmailMine, tvPhoneNumber;
    private ImageView ivPhoto;
    private Bitmap photo;
    private Intent intentLogin, intentMaps;
    private FirebaseUser currentUser;
    private User user;
    private StayQuietDBManager dbManager;
    private FirebaseManager firebaseManager;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        intentMaps = new Intent(this, MapsActivity.class);
        intentLogin = new Intent(this, LoginActivity.class);

        tvNameMine = (TextView) findViewById(R.id.tvNameMine);
        tvEmailMine = (TextView) findViewById(R.id.tvEmailMine);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        ivPhoto = (ImageView)findViewById(R.id.imageProfile);

        dbManager = new StayQuietDBManager(this);

        getParams();

        tvNameMine.setText(name);
        tvPhoneNumber.setText(phoneNumber);
        tvEmailMine.setText(email);

        checkSelfPermission();

        firebaseManager = new FirebaseManager(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        changeActivity(currentUser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile:
                Intent intentProfile = new Intent(getApplicationContext(), ProfileActivity.class);

                intentProfile.putExtra("id", id);

                startActivity(intentProfile);
                return true;
            case R.id.menu_settings:
                // Change it.
                return true;
            case R.id.menu_close_session:
                firebaseManager.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getParams(){
        id = getIntent().getExtras().getString("id");
        user = dbManager.getUser(id);

        name = user.getName();
        phoneNumber = user.getPhoneNumber();
        email = user.getEmail();
        if(user.getPhoto() != null) {
            photo = BitmapFactory.decodeByteArray(user.getPhoto(), 0, user.getPhoto().length);
            ivPhoto.setImageBitmap(photo);
        } else{
            firebaseManager.logout();
        }
    }

    private void changeActivity(FirebaseUser currentUser) {
        if (currentUser == null) {
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLogin);
            finish();
        }
    }

    private void checkSelfPermission() {
        // Check permissions
        for(int i=0; i<PERMISSIONS.length; i++){
            int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS[i]);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS,
                        REQUEST_EXTERNAL_STORAGE
                );

                break;
            }
        }
    }
}
