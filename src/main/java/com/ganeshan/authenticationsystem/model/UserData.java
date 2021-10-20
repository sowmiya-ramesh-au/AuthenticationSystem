package com.ganeshan.authenticationsystem.model;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;


public class UserData implements Serializable {
    @NotEmpty(message = "{registration.validation.username}")
    private String username;
    @NotEmpty(message = "{registration.validation.email.notnull}")
    @Email(message = "{registration.validation.email}")
    private String email;
    @NotEmpty(message = "{registration.validation.password}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
