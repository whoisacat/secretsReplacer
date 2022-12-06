package org.example.model;

public class LoginForm {
    private final String password;
    private final String username;

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public LoginForm(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
