package com.example.w3_login_activity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.w3_login_activity.R;
import com.example.w3_login_activity.ServiceLocator;
import com.example.w3_login_activity.auth.AuthResult;
import com.example.w3_login_activity.auth.AuthManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    /** Key used to return the newly registered email to LoginActivity. */
    public static final String EXTRA_EMAIL = "com.example.simplelogin.EXTRA_EMAIL";

    private AuthManager authManager;

    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = ServiceLocator.getAuthManager();

        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvError = findViewById(R.id.tvError);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());

        etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });

        // Going back to login = just close this screen.
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        clearErrors();

        AuthResult result = authManager.register(
                textOf(etFullName),
                textOf(etEmail),
                textOf(etPassword),
                textOf(etConfirmPassword));

        if (result.isSuccess()) {
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra(EXTRA_EMAIL, result.getUser().getEmail());
            setResult(RESULT_OK, data);
            finish();
            return;
        }

        switch (result.getField()) {
            case FULL_NAME:
                showFieldError(tilFullName, etFullName, result.getErrorMessage());
                break;
            case EMAIL:
                showFieldError(tilEmail, etEmail, result.getErrorMessage());
                break;
            case PASSWORD:
                showFieldError(tilPassword, etPassword, result.getErrorMessage());
                break;
            case CONFIRM_PASSWORD:
                showFieldError(tilConfirmPassword, etConfirmPassword, result.getErrorMessage());
                break;
            default:
                tvError.setText(result.getErrorMessage());
                tvError.setVisibility(TextView.VISIBLE);
                break;
        }
    }

    private void showFieldError(TextInputLayout layout, TextInputEditText field, String message) {
        layout.setError(message);
        field.requestFocus();
    }

    private void clearErrors() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tvError.setVisibility(TextView.GONE);
    }

    private static String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}