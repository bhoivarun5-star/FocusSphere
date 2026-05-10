package com.focussphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Name is required.")
    @Pattern(regexp = "^[A-Za-z][A-Za-z ]{1,99}$", message = "Name can contain letters and spaces only.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.com$", message = "Enter a valid .com email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits.")
    private String phone;

    @NotBlank(message = "Roll number is required.")
    @Pattern(regexp = "^[0-9]{3,30}$", message = "Roll number must contain digits only.")
    private String rollNo;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters long.")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}