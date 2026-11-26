package com.example.w3_login_activity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.w3_login_activity.R;
import com.example.w3_login_activity.ServiceLocator;
import com.example.w3_login_activity.auth.AuthManager;
import com.example.w3_login_activity.auth.AuthResult;
import com.example.w3_login_activity.session.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;
    private SessionManager sessionManager;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private TextView tvError;

    /** Opens RegisterActivity and, on success, pre-fills the email it returns. */
    private final ActivityResultLauncher<Intent> registerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String email = result.getData().getStringExtra(RegisterActivity.EXTRA_EMAIL);
                    etEmail.setText(email);
                    etPassword.setText("");
                    etPassword.requestFocus();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = ServiceLocator.getAuthManager();
        sessionManager = ServiceLocator.getSessionManager(this);

        // Auto-login: skip this screen if a valid session exists.
        String savedEmail = sessionManager.getLoggedInEmail();
        if (savedEmail != null) {
            if (authManager.getUser(savedEmail) != null) {
                openHomeAndFinish();
                return;
            }
            // Session points to a user that no longer exists (in-memory store was wiped).
            sessionManager.logout();
        }

        setContentView(R.layout.activity_login);
        bindViews();
    }

    private void bindViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        tvError = findViewById(R.id.tvError);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);
        TextView tvDemoHint = findViewById(R.id.tvDemoHint);

        tvDemoHint.setText(getString(R.string.demo_hint,
                ServiceLocator.DEMO_EMAIL, ServiceLocator.DEMO_PASSWORD));

        btnLogin.setOnClickListener(v -> attemptLogin());

        // Pressing "Done" on the keyboard in the password field also logs in.
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        tvGoToRegister.setOnClickListener(v ->
                registerLauncher.launch(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        clearErrors();

        String email = textOf(etEmail);
        String password = textOf(etPassword);

        AuthResult result = authManager.login(email, password);

        if (result.isSuccess()) {
            sessionManager.createSession(result.getUser().getEmail(), cbRememberMe.isChecked());
            Toast.makeText(this, getString(R.string.welcome_toast, result.getUser().getFullName()),
                    Toast.LENGTH_SHORT).show();
            openHomeAndFinish();
            return;
        }

        switch (result.getField()) {
            case EMAIL:
                tilEmail.setError(result.getErrorMessage());
                etEmail.requestFocus();
                break;
            case PASSWORD:
                tilPassword.setError(result.getErrorMessage());
                etPassword.requestFocus();
                break;
            default:
                tvError.setText(result.getErrorMessage());
                tvError.setVisibility(TextView.VISIBLE);
                etPassword.setText("");
                break;
        }
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        tvError.setVisibility(TextView.GONE);
    }

    private void openHomeAndFinish() {
        Intent intent = new Intent(this, HomeActivity.class);
        // Clear the back stack so pressing Back on Home doesn't return to Login.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}
