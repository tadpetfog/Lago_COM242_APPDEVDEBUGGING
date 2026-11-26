package com.example.w3_login_activity.data.model;

public class User {
    private final String fullName;
    private final String email; // stored normalized (trimmed, lowercase)
    private final String passwordHash; // hex-encoded SHA-256 (salt + password)
    private final String salt; // hex-encoded random salt

    public User(String fullName, String email, String passwordHash, String salt){
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getFullName() {return fullName;}
    public String getEmail() {return email;}
    public String getPasswordHash() {return passwordHash;}
    public String getSalt(){return salt;}
}
