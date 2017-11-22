package androides.stayquiet;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class VerifyPhoneActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnVerify;
    private Intent intentHome;
    private String code, verificationId;
    private FirebaseAuth mAuth;
    private AppCompatActivity activity;
    private StayQuietDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        intentHome = new Intent(this, HomeActivity.class);

        mAuth = FirebaseAuth.getInstance();
        activity = this;
        dbManager = new StayQuietDBManager(activity, mAuth);

        etCode = (EditText) findViewById(R.id.etCode);
        btnVerify = (Button) findViewById(R.id.btnVerify);

        getParams();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                    associate(credential);
                }
            }
        });
    }

    private void getParams(){
        verificationId = getIntent().getExtras().getString("verificationId");
    }

    private void getValues() {
        code = etCode.getText().toString();
    }

    private boolean isValid() {
        if(validateCode(code)) {
            return true;
        }

        return false;
    }

    private boolean validateCode(String code){
        String regex = "[0-9][0-9][0-9][0-9][0-9][0-9]" ;

        return  code.matches(regex);
    }

    private void associate(PhoneAuthCredential credential) {
        // Update data profile.
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        currentUser.updatePhoneNumber(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            User user = StayQuietDBManager.FirebaseUserToUser(currentUser);

                            intentHome.putExtra("user", user);
                            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intentHome);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_31,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
