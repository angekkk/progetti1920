package it.uniba.di.piu1920.healthapp.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import it.uniba.di.piu1920.healthapp.Home;


//check del 22/06
public class SessionManager {
    // User numero (make variable public to access from outside)
    public static final String KEY_NAME = "email";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "password";
    public static final String KEY_TYPE = "tipo";
    public static final String KEY_ID = "id";
    public static final String KEY_LINK = "link";
    // Sharedpref file name
    private static final String PREF_NAME = "HealthApp";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String numero, String password, int tipo, int id, String link) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, numero);

        // Storing email in pref
        editor.putString(KEY_EMAIL, password);

        editor.putInt(KEY_TYPE, tipo);


        editor.putInt(KEY_ID, id);

        editor.putString(KEY_LINK, link);
        //	editor.putString(KEY_LANG, lang);
        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public boolean checkLogin() {
        // Check login status
        boolean g;
        g = this.isLoggedIn();
        return g;
    }


    /**
     * Get stored session data
     */
    public Sessione getUserDetails() {
        Sessione x;


        x = new Sessione(pref.getInt(KEY_ID, 0), pref.getInt(KEY_TYPE, 0), pref.getString(KEY_NAME, ""), pref.getString(KEY_EMAIL, ""), pref.getString(KEY_LINK, ""));
        // return user
        return x;
    }

    /**
     * Clear session details
     */

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Home.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


}
