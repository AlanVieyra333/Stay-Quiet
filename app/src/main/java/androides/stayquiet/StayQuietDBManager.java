package androides.stayquiet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import androides.stayquiet.tools.Tools;

/**
 * Created by developer on 15/10/17.
 */

public class StayQuietDBManager {
    private StayQuietDBHelper dbHelper;
    private SQLiteDatabase db;
    private AppCompatActivity activity;

    public StayQuietDBManager(AppCompatActivity activity) {
        this.activity = activity;
        dbHelper = new StayQuietDBHelper(activity.getApplicationContext());
    }

    public long insertUser(User user) {
        long status;
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(dbHelper.USER_COLUMN_ID, user.getId());
        values.put(dbHelper.USER_COLUMN_USERNAME, user.getUsername());
        values.put(dbHelper.USER_COLUMN_NAME, user.getName());
        values.put(dbHelper.USER_COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(dbHelper.USER_COLUMN_EMAIL, user.getEmail());
        values.put(dbHelper.USER_COLUMN_PHOTO, user.getPhoto());

        status = db.insert(dbHelper.USER_TABLE, null, values);
        db.close();

        return status;
    }

    public User getUser(String id) {
        Cursor cursor = null;
        User user = null;
        String[] columns = {
                dbHelper.USER_COLUMN_ID,
                dbHelper.USER_COLUMN_USERNAME,
                dbHelper.USER_COLUMN_NAME,
                dbHelper.USER_COLUMN_PHONE_NUMBER,
                dbHelper.USER_COLUMN_EMAIL,
                dbHelper.USER_COLUMN_PHOTO};
        String[] selectionArgs = {
                id};
        String selection = dbHelper.USER_COLUMN_ID + " = ?";

        db = dbHelper.getWritableDatabase();
        cursor = db.query(dbHelper.USER_TABLE, columns, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()){
            user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), null, null,
                    cursor.getBlob(5));
        }

        db.close();
        cursor.close();
        return user;
    }

    public boolean updateUser(User user) {
        int status = 1;
        ContentValues values = new ContentValues();

        values.put(dbHelper.USER_COLUMN_NAME, user.getName());
        values.put(dbHelper.USER_COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(dbHelper.USER_COLUMN_EMAIL, user.getEmail());
        values.put(dbHelper.USER_COLUMN_PHOTO, user.getPhoto());

        String selection = dbHelper.USER_COLUMN_ID + " = ?";
        String[] selectionArgs = {
                user.getId()};

        db = dbHelper.getWritableDatabase();
        status = db.update(dbHelper.USER_TABLE, values, selection, selectionArgs);
        db.close();

        return status == 1;
    }

    public boolean existsUser(String id) {
        boolean result = true;

        if(getUser(id) == null) {
            result = false;
        }

        return result;
    }

    public void  saveProfileIntoCache(final Intent intent) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String id = currentUser.getUid();
        String username = "";
        String name = currentUser.getDisplayName();
        String phoneNumber = currentUser.getPhoneNumber();
        String email = currentUser.getEmail();
        final String photoUri = currentUser.getPhotoUrl().toString();

        final User user = new User(id, username, name, phoneNumber, email, null, photoUri, null);

        Tools.showProgressbar(activity);
        FirebaseStorage.getInstance()
                .getReference().child(photoUri)
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        user.setPhoto(bytes);
                        boolean operationSucces = true;

                        // Save into SQLite
                        if(!existsUser(user.getId())) {
                            Long status = insertUser(user);
                            if (status != 1) {
                                Tools.showMessage(activity, R.string.MSJ1_6);
                                operationSucces = false;
                            }
                        }else if (!updateUser(user)) {
                            Tools.showMessage(activity, R.string.MSJ1_6);
                            operationSucces = false;
                        }

                        if(operationSucces) {
                            intent.putExtra("id", user.getId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(activity.getApplicationContext(),
                                photoUri.toString() + " : " + exception.toString(),
                                Toast.LENGTH_LONG).show();
                        Tools.showMessage(activity, R.string.MSJ1_6);
                    }
                });
    }
}
