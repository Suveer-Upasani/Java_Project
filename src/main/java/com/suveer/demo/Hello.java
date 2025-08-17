package com.suveer.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Hello {
    
    // API endpoint
    @GetMapping("/api")
    @ResponseBody
    public String greet() {
        return "Hello World, Trying Spring for the first time!";
    }
    
    // Login page
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }
    
    // Signup page
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }
    
    // Login form submission (dummy implementation)
    @PostMapping("/login")
    public String login(String username, String password) {
        // In a real app, you would validate credentials here
        return "redirect:/welcome";
    }
    
    // Signup form submission (dummy implementation)
    @PostMapping("/signup")
    public String signup(String username, String password, String email) {
        // In a real app, you would create a new user here
        return "redirect:/welcome";
    }
    
    // Welcome page after successful login/signup
    @GetMapping("/welcome")
    public String welcomePage() {
        return "welcome";
    }
}