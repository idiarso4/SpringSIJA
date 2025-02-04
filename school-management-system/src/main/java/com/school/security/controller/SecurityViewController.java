package com.school.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SecurityViewController {

    @GetMapping("/forgot-password")
    public String showRequestResetPage() {
        return "request-reset";
    }

    @GetMapping("/reset-password")
    public ModelAndView showResetPasswordPage(@RequestParam(required = false) String token) {
        ModelAndView modelAndView = new ModelAndView("reset-password");
        if (token == null || token.trim().isEmpty()) {
            return new ModelAndView("redirect:/forgot-password");
        }
        modelAndView.addObject("token", token);
        return modelAndView;
    }
}
