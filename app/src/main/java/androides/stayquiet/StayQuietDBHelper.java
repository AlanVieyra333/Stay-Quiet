package androides.stayquiet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by developer on 15/10/17.
 */

public class StayQuietDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "StayQuiet";
    private static final int DB_VERSION = 1;

    public static final String USER_TABLE = "user";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_PASSWORD = "password";
    public static final String USER_COLUMN_IMAGE = "image";

    public static final String STORAGE_URL = "https://storage.googleapis.com/staging.stay-quiet.appspot.com/";
    public static final String PHOTO_DEFAULT = "images/add_camera.png";

    private final String DROP_USER_TABLE = "DROP TABLE IF EXIST " + USER_TABLE;
    private final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE +" (" +
            USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            USER_COLUMN_NAME + " TEXT NOT NULL," +
            USER_COLUMN_PHONE_NUMBER + " TEXT NOT NULL," +
            USER_COLUMN_EMAIL + " TEXT NOT NULL," +
            USER_COLUMN_PASSWORD + " TEXT NOT NULL," +
            USER_COLUMN_IMAGE + " BLOB" +
            ")";

    public StayQuietDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_USER_TABLE);
        onCreate(sqLiteDatabase);
    }
}
