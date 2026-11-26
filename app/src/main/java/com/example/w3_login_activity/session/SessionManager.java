package com.example.w3_login_activity.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.se.omapi.Session;

// Tracking who is currently logged in
public class SessionManager {
    private static final String PREFS_NAME = "session_prefs";
    private static final String KEY_EMAIL = "logged_in_email";

    private final SharedPreferences prefs;
    private String currentEmail; // in-memory session

    public SessionManager(Context context){
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void createSession(String email, boolean rememberMe){
        currentEmail = email;
        if(rememberMe){
            prefs.edit().putString(KEY_EMAIL, email).apply(); }
        else{
            prefs.edit().remove(KEY_EMAIL).apply();
        }
    }

    /** @return the logged-in email or null if nobody is logged in**/
    public String getLoggedInEmail(){
        if(currentEmail != null) return currentEmail;
        return prefs.getString(KEY_EMAIL, null);
    }
    public boolean isLoggedIn(){
        return getLoggedInEmail() != null;
    }

    public void logout(){
        currentEmail = null;
        prefs.edit().remove(KEY_EMAIL).apply();
    }
}
