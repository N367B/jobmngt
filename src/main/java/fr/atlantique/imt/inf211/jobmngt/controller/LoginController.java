package fr.atlantique.imt.inf211.jobmngt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import fr.atlantique.imt.inf211.jobmngt.entity.AppUser;
import fr.atlantique.imt.inf211.jobmngt.service.AppUserService;

@Controller
public class LoginController {

    // Value injected from the application.properties file
    @Value("${jobmngt.admin}")
    private String adminLogin;

    @Autowired
    private AppUserService userAppService;

    // Login form Get Method
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    // Login form post method
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView checkLog(@RequestParam("mail") String mail, @RequestParam("password") String pwd,
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        AppUser appUser = new AppUser();
        appUser.setMail(mail);
        appUser.setPassword(pwd);
        Optional<AppUser> user = userAppService.checkLogin(appUser);
        if (user.isPresent()) {
            appUser = user.get();
            System.out.println("User found uid: " + appUser.getId());
            session.setAttribute("user", appUser);
            if (appUser.getUsertype().equals("candidate"))
                session.setAttribute("usertype", "candidate");
            else
                session.setAttribute("usertype", "company");

            session.setAttribute("uid", appUser.getId());
            return new ModelAndView("redirect:/");
        } else {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Password or username incorrect.");
            return mav;

        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("uid", null);
        session.setAttribute("user", null);
        return "redirect:/";
    }

}