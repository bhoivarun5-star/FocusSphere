package com.focussphere.controller;

import com.focussphere.dto.LoginForm;
import com.focussphere.dto.ForgotPasswordForm;
import com.focussphere.dto.RegisterForm;
import com.focussphere.model.User;
import com.focussphere.model.UserRole;
import com.focussphere.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("sessionUser") != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "login";
        }

        return userService.login(loginForm.getEmail(), loginForm.getPassword())
                .map(user -> {
                    session.setAttribute("sessionUser", user);
                    if (user.getRole() == UserRole.ADMIN) {
                        return "redirect:/admin/dashboard";
                    }
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid email or password.");
                    return "login";
                });
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") RegisterForm registerForm,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "register";
        }

        try {
            userService.register(
                    registerForm.getName(),
                    registerForm.getEmail(),
                    registerForm.getPhone(),
                    registerForm.getRollNo(),
                    registerForm.getPassword());
            model.addAttribute("success", "Registration successful. Please login.");
            model.addAttribute("loginForm", new LoginForm());
            return "login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) {
            model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        }
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm forgotPasswordForm,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "forgotPassword";
        }

        try {
            userService.resetUserPasswordByIdentity(
                    forgotPasswordForm.getEmail(),
                    forgotPasswordForm.getPhone(),
                    forgotPasswordForm.getRollNo(),
                    forgotPasswordForm.getNewPassword());
            model.addAttribute("success", "Password reset successful. Please login.");
            return "login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "forgotPassword";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
