package com.example.w3_login_activity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.w3_login_activity.R;
import com.example.w3_login_activity.ServiceLocator;
import com.example.w3_login_activity.data.model.User;
import com.example.w3_login_activity.session.SessionManager;

public class HomeActivity extends AppCompatActivity{
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = ServiceLocator.getSessionManager(this);

        // Guard: if there's no valid session, bounce back to login.
        String email = sessionManager.getLoggedInEmail();
        User user = email == null ? null : ServiceLocator.getAuthManager().getUser(email);
        if (user == null) {
            sessionManager.logout();
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_home);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvEmail = findViewById(R.id.tvEmail);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText(getString(R.string.home_welcome, user.getFullName()));
        tvEmail.setText(getString(R.string.home_email, user.getEmail()));

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            goToLogin();
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

