package fr.atlantique.imt.inf211.jobmngt.controller;

import fr.atlantique.imt.inf211.jobmngt.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String redirect, Model model) {
        if (redirect != null && !redirect.isEmpty()) {
            model.addAttribute("redirect", redirect);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                                @RequestParam String password,
                                @RequestParam(required = false) String redirect,
                                HttpServletRequest request) {
        HttpSession session = request.getSession();
        boolean success = authService.authenticate(email, password, session);

        if (success) {
            if (redirect != null && !redirect.isEmpty() && !redirect.equals("/login")) {
                return "redirect:" + redirect;
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        authService.logout(request.getSession());
        return "redirect:/login";
    }
}