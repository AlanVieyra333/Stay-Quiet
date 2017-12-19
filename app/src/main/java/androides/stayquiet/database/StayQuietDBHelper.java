package androides.stayquiet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by developer on 15/10/17.
 */

public class StayQuietDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "StayQuiet";
    private static final int DB_VERSION = 3;

    public static final String USER_TABLE = "user";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_USERNAME = "username";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String USER_COLUMN_PHOTO = "photo";
    public static final String USER_COLUMN_PHOTO_URL = "photoUrl";
    public static final String USER_COLUMN_PASSWORD = "password";

    public static final String PROTECTION_TABLE = "protection";
    public static final String PROTECTION_COLUMN_ID = "id";
    public static final String PROTECTION_COLUMN_PROTECTOR = "protector";
    public static final String PROTECTION_COLUMN_PROTECTED = "protected";

    public static final String LOCATION_TABLE = "location";
    public static final String LOCATION_COLUMN_ID = "id";
    public static final String LOCATION_COLUMN_LONGITUDE = "longitude";
    public static final String LOCATION_COLUMN_LATITUDE = "latitude";

    public static final String URL_IMAGES = "images/";
    public static final String PHOTO_DEFAULT = "default.png";

    private final String DROP_USER_TABLE = "DROP TABLE IF EXIST " + USER_TABLE;
    private final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE + " (" +
            USER_COLUMN_ID + " TEXT NOT NULL PRIMARY KEY," +
            USER_COLUMN_USERNAME + " TEXT NOT NULL," +
            USER_COLUMN_NAME + " TEXT NOT NULL," +
            USER_COLUMN_PHONE_NUMBER + " TEXT NOT NULL," +
            USER_COLUMN_EMAIL + " TEXT NOT NULL," +
            USER_COLUMN_PHOTO + " BLOB" +
            ")";

    private final String CREATE_PROTECTION_TABLE = "CREATE TABLE " + PROTECTION_TABLE + " (" +
            PROTECTION_COLUMN_ID + " TEXT NOT NULL PRIMARY KEY," +
            PROTECTION_COLUMN_PROTECTOR + " TEXT NOT NULL," +
            PROTECTION_COLUMN_PROTECTED + " TEXT NOT NULL" +
            ")";

    public StayQuietDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_PROTECTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_USER_TABLE);
        onCreate(sqLiteDatabase);
    }
}
