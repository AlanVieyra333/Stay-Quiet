package androides.stayquiet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

/**
 * Created by developer on 15/10/17.
 */

public class StayQuietDBManager {
    private StayQuietDBHelper dbHelper;
    private SQLiteDatabase db;
    private AppCompatActivity activity;
    private FirebaseAuth mAuth;

    public StayQuietDBManager(AppCompatActivity activity, FirebaseAuth mAuth) {
        this.activity = activity;
        this.mAuth = mAuth;
        dbHelper = new StayQuietDBHelper(activity.getApplicationContext());
    }

    public long insertUser(User user) {
        long status;
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.USER_COLUMN_NAME, user.getName());
        values.put(dbHelper.USER_COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(dbHelper.USER_COLUMN_EMAIL, user.getEmail());
        values.put(dbHelper.USER_COLUMN_PASSWORD, user.getPassword());

        status = db.insert(dbHelper.USER_TABLE, null, values);
        db.close();

        return status;
    }

    public User getUser(Account account) {
        Cursor cursor = null;
        User user = null;
        String[] columns = {
                dbHelper.USER_COLUMN_NAME,
                dbHelper.USER_COLUMN_PHONE_NUMBER,
                dbHelper.USER_COLUMN_EMAIL,
                dbHelper.USER_COLUMN_IMAGE};
        String[] selectionArgs = {
                account.getEmail(),
                account.getPassword()};
        String selection = dbHelper.USER_COLUMN_EMAIL + " = ? AND " +
                dbHelper.USER_COLUMN_PASSWORD + " = ?";

        db = dbHelper.getWritableDatabase();
        cursor = db.query(dbHelper.USER_TABLE, columns, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()){
            user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    "", cursor.getBlob(3));
        }

        db.close();
        cursor.close();
        return user;
    }

    public boolean existsAccount(Account account){
        boolean existsAccount = false;
        Cursor cursor = null;
        String[] columns = {
                dbHelper.USER_COLUMN_ID};
        String[] selectionArgs = {
                account.getEmail()};
        String selection = dbHelper.USER_COLUMN_EMAIL + " = ?";

        db = dbHelper.getWritableDatabase();
        cursor = db.query(dbHelper.USER_TABLE, columns, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()){
            existsAccount = true;
        }

        db.close();
        cursor.close();
        return existsAccount;
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e.getMessage().indexOf("user") != -1)
                            Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_7,
                                    Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_6,
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void signUp(User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity.getApplicationContext(), "Error: Registrar " + e,
                                Toast.LENGTH_LONG).show();

                        if(e.getMessage().indexOf("user") != -1)
                            Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_7,
                                    Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(activity.getApplicationContext(), R.string.MSJ1_6,
                                    Toast.LENGTH_LONG).show();
                    }
                });
    }
}
