package com.focussphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginForm {

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.com$", message = "Enter a valid .com email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters long.")
    private String password;

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