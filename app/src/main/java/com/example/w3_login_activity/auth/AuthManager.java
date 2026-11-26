package com.example.w3_login_activity.auth;

import com.example.w3_login_activity.data.UserRepository;
import com.example.w3_login_activity.data.model.User;
import com.example.w3_login_activity.util.PasswordHasher;
import com.example.w3_login_activity.util.Validator;
import com.example.w3_login_activity.auth.AuthResult;

import java.util.Locale;

/**
 * All login / registration business logic lives here, independent of Android UI.
 * Activities call this class and simply render the {@link AuthResult}.
 */
public class AuthManager {

    /** Deliberately vague so attackers can't tell whether an email is registered. */
    static final String INVALID_CREDENTIALS = "Invalid email or password";
    static final String EMAIL_TAKEN = "An account with this email already exists";

    private final UserRepository repository;

    public AuthManager(UserRepository repository) {
        this.repository = repository;
    }

    public AuthResult login(String email, String password) {
        String emailError = Validator.validateEmail(email);
        if (emailError != null) return AuthResult.failure(AuthResult.Field.EMAIL, emailError);

        String passwordError = Validator.validateLoginPassword(password);
        if (passwordError != null) return AuthResult.failure(AuthResult.Field.PASSWORD, passwordError);

        User user = repository.findByEmail(email);
        if (user == null || !PasswordHasher.verify(password, user.getSalt(), user.getPasswordHash())) {
            return AuthResult.failure(AuthResult.Field.GENERAL, INVALID_CREDENTIALS);
        }
        return AuthResult.success(user);
    }

    public AuthResult register(String fullName, String email, String password, String confirmPassword) {
        String error;

        if ((error = Validator.validateFullName(fullName)) != null)
            return AuthResult.failure(AuthResult.Field.FULL_NAME, error);

        if ((error = Validator.validateEmail(email)) != null)
            return AuthResult.failure(AuthResult.Field.EMAIL, error);

        if ((error = Validator.validateNewPassword(password)) != null)
            return AuthResult.failure(AuthResult.Field.PASSWORD, error);

        if ((error = Validator.validatePasswordConfirmation(password, confirmPassword)) != null)
            return AuthResult.failure(AuthResult.Field.CONFIRM_PASSWORD, error);

        if (repository.exists(email))
            return AuthResult.failure(AuthResult.Field.EMAIL, EMAIL_TAKEN);

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);
        User user = new User(fullName.trim(), email.trim().toLowerCase(Locale.ROOT), hash, salt);

        if (!repository.add(user)) {
            // Race condition safety net: someone registered the same email in between.
            return AuthResult.failure(AuthResult.Field.EMAIL, EMAIL_TAKEN);
        }
        return AuthResult.success(user);
    }

    /** @return the user for an email (used to validate a restored session), or null. */
    public User getUser(String email) {
        return repository.findByEmail(email);
    }
}
