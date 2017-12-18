package androides.stayquiet.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import androides.stayquiet.R;
import androides.stayquiet.database.FirebaseManager;
import androides.stayquiet.database.SessionManager;
import androides.stayquiet.database.StayQuietDBManager;
import androides.stayquiet.tools.Tools;
import androides.stayquiet.user.Protected;
import androides.stayquiet.user.User;
import androides.stayquiet.user.UsersAdapter;

public class HomeActivity extends AppCompatActivity implements AddProtectedDialog.addProtectedDialogListener{

    private String username, name, phoneNumber, email, id;
    private TextView tvNameMine, tvEmailMine, tvPhoneNumber;
    private ImageView ivPhoto, add;
    private ListView lvProtected;
    private Bitmap photo;
    private Intent intentLogin, intentMaps;
    private User user;
    private StayQuietDBManager dbManager;
    private FirebaseManager firebaseManager;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(androides.stayquiet.R.layout.activity_home);

        // Session data.
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        user = session.getUser();

        intentMaps = new Intent(this, MapsActivity.class);
        intentLogin = new Intent(this, LoginActivity.class);

        tvNameMine = (TextView) findViewById(R.id.tvName);
        tvEmailMine = (TextView) findViewById(R.id.tvEmail);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        ivPhoto = (ImageView)findViewById(R.id.imageProfile);
        lvProtected = findViewById(R.id.lvProtected);
        add = findViewById(R.id.add);

        dbManager = new StayQuietDBManager(this);
        firebaseManager = new FirebaseManager(this);

        getParams();
        getProtected();

        tvNameMine.setText(name);
        tvPhoneNumber.setText(phoneNumber);
        tvEmailMine.setText(email);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askUsername();
            }
        });

        checkSelfPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile:
                Intent intentProfile = new Intent(getApplicationContext(), ProfileActivity.class);

                startActivity(intentProfile);
                return true;
            case R.id.menu_settings:
                // Change it.
                return true;
            case R.id.menu_close_session:
                firebaseManager.logout();
                session.logoutUser();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void getProtected() {
        final ArrayList<User> listProtected = new ArrayList<User>();

        final UsersAdapter usersAdapter = new UsersAdapter(this, listProtected);

        lvProtected.setAdapter(usersAdapter);

        firebaseManager.getDBReferenceProtection().child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Protected newProtected = dataSnapshot.getValue(Protected.class);

                OnSuccessListener<Void> callback = new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listProtected.add(firebaseManager.getUser());
                        usersAdapter.notifyDataSetChanged();
                    }
                };

                firebaseManager.setCallback(callback);
                firebaseManager.findUserByUsername(newProtected.getUsername());

                user.addProtected(newProtected);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void askUsername() {
        AddProtectedDialog addProtectedDialog = new AddProtectedDialog();
        addProtectedDialog.show(getSupportFragmentManager(), "addProtected");
    }

    @Override
    public void applyText(String username) {
        Protected newProtected = new Protected(username, false);

        firebaseManager.getDBReferenceProtection().child(user.getUsername())
                .child("" + (user.getProtecteds().size() + 1))
                .child("username")
                .setValue(newProtected.getUsername());

        firebaseManager.getDBReferenceProtection().child(user.getUsername())
                .child("" + (user.getProtecteds().size() + 1))
                .child("canWatch")
                .setValue(newProtected.getCanWatch());
    }
}
