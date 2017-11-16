package androides.stayquiet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private String name, phoneNumber, email;
    private TextView tvNameMine, tvEmailMine;
    private Button btnMapExample;
    private Bitmap bm;
    private Intent intentLogin, intentMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        intentMaps = new Intent(this, MapsActivity.class);
        intentLogin = new Intent(this, LoginActivity.class);

        tvNameMine = (TextView) findViewById(R.id.tvNameMine);
        tvEmailMine = (TextView) findViewById(R.id.tvEmailMine);

        getParams();

        tvNameMine.setText(name);
        tvEmailMine.setText(email);

        btnMapExample = (Button)findViewById(R.id.btn_mapExample);
        btnMapExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentMaps);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        changeActivity(user);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile:
                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
                return true;
            case R.id.menu_settings:
                // Change it.
                return true;
            case R.id.menu_close_session:
                FirebaseAuth.getInstance().signOut();
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentLogin);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getParams(){
        User user = (User) getIntent().getExtras().getSerializable("user");

        name = user.getName();
        phoneNumber = user.getPhoneNumber();
        email = user.getEmail();
        if(user.getPhoto() != null) {
            bm = BitmapFactory.decodeByteArray(user.getPhoto(), 0, user.getPhoto().length);
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
}
