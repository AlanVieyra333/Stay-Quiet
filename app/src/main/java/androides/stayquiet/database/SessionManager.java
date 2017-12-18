package androides.stayquiet.database;

/**
 * Created by developer on 9/12/17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androides.stayquiet.user.User;
import androides.stayquiet.activities.LoginActivity;

public class SessionManager {
    SharedPreferences pref;                 // Shared Preferences
    Editor editor;                          // Editor for Shared preferences
    Context _context;                       // Context
    int PRIVATE_MODE = 0;                   // Shared pref mode

    private static final String PREF_NAME = "StayQuietPref";        // Sharedpref file name
    private static final String IS_LOGIN = "IsLoggedIn";            // All Shared Preferences Keys
    private static final String REMEMBER = "remember";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(User user){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing user in pref
        editor.putString(StayQuietDBHelper.USER_COLUMN_ID, user.getId());
        editor.putString(StayQuietDBHelper.USER_COLUMN_USERNAME, user.getUsername());
        editor.putString(StayQuietDBHelper.USER_COLUMN_NAME, user.getName());
        editor.putString(StayQuietDBHelper.USER_COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        editor.putString(StayQuietDBHelper.USER_COLUMN_EMAIL, user.getEmail());
        editor.putString(StayQuietDBHelper.USER_COLUMN_PASSWORD, user.getPassword());
        editor.putString(StayQuietDBHelper.USER_COLUMN_PHOTO_URL, user.getPhotoUrl());

        // commit changes
        editor.commit();
    }

    /**
     * Add a flag for remember the username and password.
     */
    public void setReminder(boolean remember) {
        editor.putBoolean(REMEMBER, remember);
    }

    public boolean getReminder() {
        return pref.getBoolean(REMEMBER, false);
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public User getUser(){
        User user = new User();

        user.setId(pref.getString(StayQuietDBHelper.USER_COLUMN_ID, null));
        user.setUsername(pref.getString(StayQuietDBHelper.USER_COLUMN_USERNAME, null));
        user.setName(pref.getString(StayQuietDBHelper.USER_COLUMN_NAME, null));
        user.setEmail(pref.getString(StayQuietDBHelper.USER_COLUMN_EMAIL, null));
        user.setPassword(pref.getString(StayQuietDBHelper.USER_COLUMN_PASSWORD, null));
        user.setPhoneNumber(pref.getString(StayQuietDBHelper.USER_COLUMN_PHONE_NUMBER, null));
        user.setPhotoUrl(pref.getString(StayQuietDBHelper.USER_COLUMN_PHOTO_URL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        boolean remember = pref.getBoolean(REMEMBER, false);

        if(remember) {
            i.putExtra("username", pref.getString(StayQuietDBHelper.USER_COLUMN_USERNAME, null));
            i.putExtra("password", pref.getString(StayQuietDBHelper.USER_COLUMN_PASSWORD, null));
        }

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}