package androides.stayquiet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by developer on 15/10/17.
 */

public class StayQuietDBManager {
    private StayQuietDBHelper dbHelper;
    private SQLiteDatabase db;

    public StayQuietDBManager(Context context) {
        dbHelper = new StayQuietDBHelper(context);
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
}
