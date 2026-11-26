package com.example.w3_login_activity.util;

import java.util.regex.Pattern;

public final class Validator {

    public static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validator() { }

    public static String validateFullName(String name) {
        if (isBlank(name)) return "Full name is required";
        if (name.trim().length() < 2) return "Full name is too short";
        return null;
    }

    public static String validateEmail(String email) {
        if (isBlank(email)) return "Email is required";
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) return "Enter a valid email address";
        return null;
    }

    /** Used on registration: enforces strength rules. */
    public static String validateNewPassword(String password) {
        if (password == null || password.isEmpty()) return "Password is required";
        if (password.length() < MIN_PASSWORD_LENGTH)
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters";
        boolean hasLetter = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasLetter || !hasDigit) return "Password must contain letters and numbers";
        return null;
    }

    /** Used on login: only checks that something was typed. */
    public static String validateLoginPassword(String password) {
        if (password == null || password.isEmpty()) return "Password is required";
        return null;
    }

    public static String validatePasswordConfirmation(String password, String confirm) {
        if (confirm == null || confirm.isEmpty()) return "Please confirm your password";
        if (!confirm.equals(password)) return "Passwords do not match";
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}