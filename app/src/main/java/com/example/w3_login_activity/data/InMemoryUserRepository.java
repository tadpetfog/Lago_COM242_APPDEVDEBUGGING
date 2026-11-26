package com.example.w3_login_activity.data;

import com.example.w3_login_activity.data.model.User;

import  java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// NOTE: Data lives only as long as the app process.
public class InMemoryUserRepository implements UserRepository{
    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public synchronized User findByEmail(String email){
        if (email == null) return null;
        return usersByEmail.get(normalize(email));
    }
    @Override
    public synchronized boolean exists(String email) {
        return email != null && usersByEmail.containsKey(normalize(email));
    }
    @Override
    public synchronized boolean add(User user) {
        String key = normalize(user.getEmail());
        if (usersByEmail.containsKey(key)) {
            return false;
        }
        usersByEmail.put(key, user);
        return true;
    }
    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
