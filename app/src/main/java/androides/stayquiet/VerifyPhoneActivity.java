package androides.stayquiet;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import androides.stayquiet.tools.Tools;

public class VerifyPhoneActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnVerify;
    private Intent intentHome;
    private String code, verificationId;
    private FirebaseManager firebaseManager;
    private StayQuietDBManager dbManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        intentHome = new Intent(this, HomeActivity.class);

        dbManager = new StayQuietDBManager(this);

        etCode = (EditText) findViewById(R.id.etCode);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(false);

        getParams();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();

                if(isValid()){
                    final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                    firebaseManager.associatePhoneNumber(credential);
                }
            }
        });

        firebaseManager = new FirebaseManager(this);
        firebaseManager.setCallback(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dbManager.saveProfileIntoCache(intentHome);
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
}
