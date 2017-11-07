package androides.stayquiet;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private String firstName, lastName, phoneNumber, email;
    private TextView tvNameMine, tvEmailMine;
    private Button btnMapExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Intent intentMaps = new Intent(HomeActivity.this, MapsActivity.class);
        tvNameMine = (TextView) findViewById(R.id.tvNameMine);
        tvEmailMine = (TextView) findViewById(R.id.tvEmailMine);

        getParams();

        tvNameMine.setText(firstName + " " + lastName);
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
                // Change it.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getParams(){
        User user = (User) getIntent().getExtras().getSerializable("user");

        firstName = user.getFirstName();
        lastName = user.getLastName();
        phoneNumber = user.getPhoneNumber();
        email = user.getEmail();
    }
}
