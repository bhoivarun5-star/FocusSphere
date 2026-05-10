package com.focussphere.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ForgotPasswordForm {

    @NotBlank(message = "Email is required.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.com$", message = "Enter a valid .com email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits.")
    private String phone;

    @NotBlank(message = "Roll number is required.")
    @Pattern(regexp = "^[0-9]{3,30}$", message = "Roll number must contain digits only.")
    private String rollNo;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters long.")
    private String newPassword;

    @NotBlank(message = "Confirm password is required.")
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters long.")
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match.")
    public boolean isPasswordMatch() {
        if (newPassword == null || confirmPassword == null) {
            return false;
        }
        return newPassword.equals(confirmPassword);
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
