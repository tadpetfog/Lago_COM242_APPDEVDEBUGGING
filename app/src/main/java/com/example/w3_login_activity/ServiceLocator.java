package com.example.w3_login_activity;

import android.content.Context;

import com.example.w3_login_activity.auth.AuthManager;
import com.example.w3_login_activity.data.InMemoryUserRepository;
import com.example.w3_login_activity.data.UserRepository;
import com.example.w3_login_activity.session.SessionManager;

public final class ServiceLocator{
    public static final String DEMO_NAME = "Demo User";
    public static final String DEMO_EMAIL = "demo@example.com";
    public static final String DEMO_PASSWORD = "Password123";

    // Preloaded account requested for registration and login testing.
    public static final String STUDENT_NAME = "Enrie James F. Lago";
    public static final String STUDENT_EMAIL = "lagoef@students.national-u.edu.ph";
    public static final String STUDENT_PASSWORD = "password123";

    private static AuthManager authManager;
    private static SessionManager sessionManager;

    private ServiceLocator() { }

    public static synchronized AuthManager getAuthManager() {
        if (authManager == null) {
            authManager = new AuthManager(provideRepository());
            // Seed one account so you can log in immediately after install.
            authManager.register(DEMO_NAME, DEMO_EMAIL, DEMO_PASSWORD, DEMO_PASSWORD);
            // Seed the requested account so it is available immediately on launch.
            authManager.register(STUDENT_NAME, STUDENT_EMAIL,
                    STUDENT_PASSWORD, STUDENT_PASSWORD);
        }
        return authManager;
    }

    public static synchronized SessionManager getSessionManager(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context.getApplicationContext());
        }
        return sessionManager;
    }

    private static UserRepository provideRepository() {
        return new InMemoryUserRepository();
        // Later: return new RoomUserRepository(context);
    }
}