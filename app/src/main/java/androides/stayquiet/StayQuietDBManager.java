package androides.stayquiet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by developer on 15/10/17.
 */

public class StayQuietDBManager {
    private StayQuietDBHelper dbHelper;
    private SQLiteDatabase db;
    private AppCompatActivity activity;
    private FirebaseAuth mAuth;
    private User user;
    private Intent intentVerifyPhone, intentLogin;

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

    public void signUp(User user, final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        this.user = user;
        final String name = user.getName();
        final String phoneNumber = user.getPhoneNumber();
        intentVerifyPhone = new Intent(activity, VerifyPhoneActivity.class);
        intentLogin = new Intent(activity, LoginActivity.class);

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(Uri.parse(StayQuietDBHelper.STORAGE_URL + "images/add_camera.png"))
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profile);

                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+52" + phoneNumber,        // Phone number to verify
                                    1,                  // Timeout duration
                                    TimeUnit.MINUTES,   // Unit of timeout
                                    activity,               // Activity (for callback binding)
                                    mCallbacks);        // OnVerificationStateChangedCallbacks
                        }
                    }
                })
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
                });;
    }

    public static User FirebaseUserToUser(FirebaseUser currentUser) {
        String name = currentUser.getDisplayName();
        String phoneNumber = currentUser.getPhoneNumber();
        String email = currentUser.getEmail();
        Uri photoUrl = currentUser.getPhotoUrl();
        byte[] photo;
        Bitmap bitmap;

        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(photoUrl.toString()).getContent());
            photo = bitmapToBytes(bitmap);
        } catch (Exception e) {
            photo = null;
        }

        return new User(name, phoneNumber, email, "", photo);
    }

    // convert from bitmap to byte array
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap bytesToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
